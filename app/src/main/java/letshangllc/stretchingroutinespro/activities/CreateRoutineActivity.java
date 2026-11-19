package letshangllc.stretchingroutinespro.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.util.ArrayList;

import letshangllc.stretchingroutinespro.JavaObjects.Stretch;
import letshangllc.stretchingroutinespro.R;
import letshangllc.stretchingroutinespro.adapaters.StretchesAdapter;
import letshangllc.stretchingroutinespro.data.DBTableConstants;
import letshangllc.stretchingroutinespro.data.StretchesDBHelper;
import letshangllc.stretchingroutinespro.dialogs.AddStretchDialog;
import letshangllc.stretchingroutinespro.dialogs.EditStretchDialog;
import letshangllc.stretchingroutinespro.helpers.DbBitmapUtility;
import letshangllc.stretchingroutinespro.helpers.EdgeToEdgeHelper;
import letshangllc.stretchingroutinespro.helpers.StoreRoutineInBackground;
import letshangllc.stretchingroutinespro.helpers.StoringRoutineComplete;

public class CreateRoutineActivity extends AppCompatActivity {
    private static final String TAG = CreateRoutineActivity.class.getSimpleName();

    private ArrayList<Stretch> stretches;

    /* Views */
    private ListView lvStretches;
    private EditText etRoutineName;
    private ImageView imgRoutine;

    /* ListView Adapter*/
    private StretchesAdapter stretchesAdapter;

    /* DataBaseHelper */
    private StretchesDBHelper stretchesDBHelper;

    private static final int SELECT_ROUTINE_PICTURE = 2;

    private Bitmap routineBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_routine);
        EdgeToEdgeHelper.apply(this, findViewById(R.id.createRoutineRoot));

        this.setupToolbar();

        stretches = new ArrayList<>();

        stretchesDBHelper = new StretchesDBHelper(this);
        this.setupViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Create Routine");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDiscard();
            }
        });
    }

    public void setupViews() {
        lvStretches = findViewById(R.id.lvStretches);

        registerForContextMenu(lvStretches);

        stretchesAdapter = new StretchesAdapter(this, stretches);

        lvStretches.setAdapter(stretchesAdapter);

        etRoutineName = findViewById(R.id.etRoutineName);

        imgRoutine = findViewById(R.id.imgRoutine);
    }

    public void addStretchOnClick(View view) {
        AddStretchDialog addStretchDialog = new AddStretchDialog();

        addStretchDialog.setCallback(new AddStretchDialog.Listener() {
            @Override
            public void onDialogPositiveClick(String name, int duration, String description, Bitmap bitmap) {
                Stretch stretch = new Stretch(name, description, bitmap, duration);
                stretches.add(stretch);
                stretchesAdapter.notifyDataSetChanged();
            }
        });

        addStretchDialog.show(getSupportFragmentManager(), TAG);
    }

    public void storeRoutineData(){
        String routineName = etRoutineName.getText().toString();
        if(routineName.isEmpty()){
            Toast.makeText(this, "Routine name is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "Storing data");
        int routineId = this.storeRoutine(routineName);
        Log.i(TAG, "Stored routine");

        new StoreRoutineInBackground(stretches, routineId, stretchesDBHelper, this, new StoringRoutineComplete() {
            @Override
            public void onDataStored() {
                Log.i(TAG, "Data stored");
                finish();
            }
        }).execute();

    }

    public int storeRoutine(String routineName){
        SQLiteDatabase db = stretchesDBHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBTableConstants.ROUTINE_NAME, routineName);
        if(routineBitmap!=null){
            cv.put(DBTableConstants.ROUTINE_IMAGE, DbBitmapUtility.getBytes(routineBitmap));
        }

        int routineId = (int) db.insert(DBTableConstants.ROUTINE_TABLE_NAME, null, cv);
        Log.i(TAG, "Routine ID: " + routineId);
        db.close();

        return routineId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_create_routine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.complete) {
            storeRoutineData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDiscard(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discard Routine?");

        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete_edit, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info == null) {
            return super.onContextItemSelected(item);
        }
        int itemId = item.getItemId();
        if (itemId == R.id.edit) {
            editStretch(stretches.get((int) info.id));
            return true;
        } else if (itemId == R.id.delete) {
            stretches.remove((int) info.id);
            stretchesAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void editStretch(final Stretch stretch){
        EditStretchDialog editStretchDialog  = new EditStretchDialog();
        editStretchDialog.setCurrentStretch(stretch);

        editStretchDialog.setCallback(new EditStretchDialog.Listener() {
            @Override
            public void onDialogPositiveClick(String name, int duration, String description, Bitmap bitmap) {
                stretch.setName(name);
                stretch.setDuration(duration);
                stretch.setInstructions(description);
                stretch.setBitmap(bitmap);
                stretchesAdapter.notifyDataSetChanged();
            }
        });
        editStretchDialog.show(getSupportFragmentManager(), TAG);
    }

    public void changeRoutinePhotoOnClick(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_ROUTINE_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "Selected Photo");
            if (requestCode == SELECT_ROUTINE_PICTURE) {
                if(data == null ){
                    Log.e(TAG, "Data Null");
                    return;
                }
                routineBitmap = null;
                try {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        Log.i(TAG, "PATH: " + selectedImageUri.getPath());
                    }
                    Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    double width = bitmap1.getWidth()* 1.0;
                    double height = bitmap1.getHeight() * 1.0;

                    double largestXY = 512.0;
                    if(width>largestXY || height>largestXY){
                        if(width>height){
                            double scale = largestXY/height;
                            routineBitmap = Bitmap.createScaledBitmap(bitmap1,(int) (scale*width),
                                    (int)  (height*scale), true);
                        }else{
                            double scale = largestXY/width;
                            routineBitmap = Bitmap.createScaledBitmap(bitmap1,(int) (scale*width),
                                    (int)  (height*scale), true);
                        }

                    } else {
                        routineBitmap = bitmap1;
                    }

                    imgRoutine.setImageBitmap(routineBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
