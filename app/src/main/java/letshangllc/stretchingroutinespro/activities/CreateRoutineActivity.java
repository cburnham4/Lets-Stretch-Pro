package letshangllc.stretchingroutinespro.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import letshangllc.stretchingroutinespro.Data.DBTableConstants;
import letshangllc.stretchingroutinespro.Data.StretchesDBHelper;
import letshangllc.stretchingroutinespro.JavaObjects.Stretch;
import letshangllc.stretchingroutinespro.R;
import letshangllc.stretchingroutinespro.adapaters.StretchesAdapter;
import letshangllc.stretchingroutinespro.dialogs.AddStretchDialog;
import letshangllc.stretchingroutinespro.dialogs.EditStretchDialog;
import letshangllc.stretchingroutinespro.helpers.StoreRoutineInBackground;
import letshangllc.stretchingroutinespro.helpers.StoringRoutineComplete;

public class CreateRoutineActivity extends AppCompatActivity {
    private static final String TAG = CreateRoutineActivity.class.getSimpleName();

    private ArrayList<Stretch> stretches;

    /* ListView for the stretches */
    private ListView lvStretches;

    /* ListView Adapter*/
    private StretchesAdapter stretchesAdapter;

    /* DataBaseHelper */
    private StretchesDBHelper stretchesDBHelper;

    /* Routine Name EditText */
    private EditText etRoutineName;

    /* Progress Dialog */
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_routine);

        this.setupToolbar();

        stretches = new ArrayList<>();

        stretchesDBHelper = new StretchesDBHelper(this);
        this.setupViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Create Routine");

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
        builder.setTitle("Discard Routine?");

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
                stretch.bitmap = bitmap;
                stretchesAdapter.notifyDataSetChanged();
            }
        });
        editStretchDialog.show(getSupportFragmentManager(), TAG);
    }




}
