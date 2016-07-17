package letshangllc.stretchingroutinespro.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.io.IOException;
import java.util.ArrayList;

import letshangllc.stretchingroutinespro.JavaObjects.RoutineItem;
import letshangllc.stretchingroutinespro.data.DBTableConstants;
import letshangllc.stretchingroutinespro.data.StretchesDBHelper;
import letshangllc.stretchingroutinespro.JavaObjects.Stretch;
import letshangllc.stretchingroutinespro.R;
import letshangllc.stretchingroutinespro.adapaters.StretchesAdapter;
import letshangllc.stretchingroutinespro.dialogs.AddStretchDialog;
import letshangllc.stretchingroutinespro.dialogs.EditStretchDialog;
import letshangllc.stretchingroutinespro.helpers.DbBitmapUtility;
import letshangllc.stretchingroutinespro.helpers.StoringRoutineComplete;
import letshangllc.stretchingroutinespro.helpers.UpdateRoutineInBackground;

public class EditRoutineActivity extends AppCompatActivity {
    private static final String TAG = EditRoutineActivity.class.getSimpleName();

    private ArrayList<Stretch> stretches;

    /* Views */
    private ListView lvStretches;
    private EditText etRoutineName;
    private ImageView imgRoutine;

    /* ListView Adapter*/
    private StretchesAdapter stretchesAdapter;

    /* DataBaseHelper */
    private StretchesDBHelper stretchesDBHelper;

    /* Progress Dialog */
    private ProgressDialog progressDialog;

    private static final int SELECT_ROUTINE_PICTURE = 2;

    /* Routine */
    private String routineName;
    private int routineId;
    private Bitmap routineBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_routine);

        this.setupToolbar();

        Intent intent = getIntent();
        routineId = intent.getIntExtra(getString(R.string.routine_index_intent), 0);
        routineName = intent.getStringExtra(getString(R.string.routine_name_extra));

        stretchesDBHelper = new StretchesDBHelper(this);

        this.getStretches();
        this.setupViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Edit Routine");

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        assert toolbar != null;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDiscard();
            }
        });
    }

    public void setupViews() {
        lvStretches = (ListView) findViewById(R.id.lvStretches);

        registerForContextMenu(lvStretches);

        stretchesAdapter = new StretchesAdapter(this, stretches);

        lvStretches.setAdapter(stretchesAdapter);

        etRoutineName = (EditText) findViewById(R.id.etRoutineName);
        etRoutineName.setText(routineName);

        imgRoutine = (ImageView) findViewById(R.id.imgRoutine);
        /* todo set img from routine */
        setRoutineImage();
    }

    /* Get the routine image from the db and put it in the DB */
    private void setRoutineImage(){
        SQLiteDatabase sqLiteDatabase = stretchesDBHelper.getReadableDatabase();

        String[] projection = {DBTableConstants.ROUTINE_IMAGE};

        Cursor c = sqLiteDatabase.query(DBTableConstants.ROUTINE_TABLE_NAME, projection,
                DBTableConstants.ROUTINE_ID + " = " + routineId,null, null, null, null);
        c.moveToFirst();

        byte[] bytes = c.getBlob(0);
        c.close();
        if(bytes!=null){
            imgRoutine.setImageBitmap(DbBitmapUtility.getImage(bytes));
        };

    }

    public void getStretches(){
        stretches = new ArrayList<>();
        SQLiteDatabase db = stretchesDBHelper.getReadableDatabase();
         /* Query the db to get the muscle data */

        String sql = "SELECT *" +
                " FROM " + DBTableConstants.STRETCH_TABLE_NAME +
                " INNER JOIN " + DBTableConstants.ROUTINE_STRETCH_TABLE +
                " ON " + DBTableConstants.STRETCH_TABLE_NAME + "." + DBTableConstants.STRETCH_ID +
                " = " + DBTableConstants.ROUTINE_STRETCH_TABLE + "." + DBTableConstants.STRETCH_ID +
                " WHERE " + DBTableConstants.ROUTINE_STRETCH_TABLE + "." + DBTableConstants.ROUTINE_ID +
                " = " + routineId;


        Cursor c = db.rawQuery(sql, null);



        Log.i(TAG, "Query count: "+ c.getCount());

        c.moveToFirst();
        while(!c.isAfterLast()){
            int duration = c.getInt(c.getColumnIndex(DBTableConstants.STRETCH_DURATION));
            String instruction = c.getString(c.getColumnIndex(DBTableConstants.STRETCH_INSTRUCTION));
            byte[] bytes = c.getBlob(c.getColumnIndex(DBTableConstants.STRETCH_IMAGE));
            String name = c.getString(c.getColumnIndex(DBTableConstants.STRETCH_NAME));
            int id = c.getInt(c.getColumnIndex(DBTableConstants.STRETCH_ID));

            if(bytes == null){
                stretches.add(new Stretch(name, instruction, duration, null, id));
            }else{
                Bitmap bitmap = DbBitmapUtility.getImage(bytes);
                stretches.add(new Stretch(name, instruction,duration, bitmap, id));
            }

            c.moveToNext();
        }

        c.close();
        db.close();
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

    /* todo store routine image */
    public void storeRoutineData(){
        String routineName = etRoutineName.getText().toString();
        if(routineName.isEmpty()){
            Toast.makeText(this, "Routine name is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "Storing data");
        int routineId = this.updateRoutine(routineName);
        Log.i(TAG, "Stored routine");

        new UpdateRoutineInBackground(stretches, routineId, stretchesDBHelper, this, new StoringRoutineComplete() {
            @Override
            public void onDataStored() {
                Log.i(TAG, "Data stored");
                finish();
            }
        }).execute();

    }

    public int updateRoutine(String routineName){
        SQLiteDatabase db = stretchesDBHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBTableConstants.ROUTINE_NAME, routineName);
        if(routineBitmap!=null){
            cv.put(DBTableConstants.ROUTINE_IMAGE, DbBitmapUtility.getBytes(routineBitmap));
        }

        db.update(DBTableConstants.ROUTINE_TABLE_NAME, cv,
                DBTableConstants.ROUTINE_ID +" = "+ routineId, null);

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
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.complete:
                storeRoutineData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void confirmDiscard(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discard Edits?");

        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* Finish the exercise upon discarding */
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
        switch (item.getItemId()) {
            case R.id.edit:
                editStretch(stretches.get((int) info.id));
                return true;
            case R.id.delete:
                stretches.remove((int) info.id);
                stretchesAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
            Log.i(TAG, "RESULT_OKAY");
            if (requestCode == SELECT_ROUTINE_PICTURE) {
                if(data == null ){
                    Log.e(TAG, "Data Null");
                }
                routineBitmap = null;
                try {
                    Uri selectedImageUri = data.getData();
                    String selectedImagePath = selectedImageUri.getPath();
                    Log.i(TAG, "PATH: " + selectedImagePath);
                    Bitmap bitmap1 = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
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

                    }

                    imgRoutine.setImageBitmap(routineBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
