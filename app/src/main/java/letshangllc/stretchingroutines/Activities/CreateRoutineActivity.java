package letshangllc.stretchingroutines.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Locale;

import letshangllc.stretchingroutines.Data.DBTableConstants;
import letshangllc.stretchingroutines.Data.StretchesDBHelper;
import letshangllc.stretchingroutines.JavaObjects.Stretch;
import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.adapaters.StretchesAdapter;
import letshangllc.stretchingroutines.dialogs.AddStretchDialog;
import letshangllc.stretchingroutines.helpers.DbBitmapUtility;

public class CreateRoutineActivity extends AppCompatActivity {
    private static final String TAG = CreateRoutineActivity.class.getSimpleName();

    private ArrayList<Stretch> stretches;

    /* ListView for the stretches */
    private ListView lvStretches;

    /* ListView Adapter*/
    private StretchesAdapter stretchesAdapter;

    /* DataBaseHelper */
    private StretchesDBHelper stretchesDBHelper;


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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setupViews() {
        lvStretches = (ListView) findViewById(R.id.lvStretches);

        stretchesAdapter = new StretchesAdapter(this, stretches);

        lvStretches.setAdapter(stretchesAdapter);
    }

    public void addStretchOnClick(View view) {
        AddStretchDialog addStretchDialog = new AddStretchDialog();

        addStretchDialog.setCallback(new AddStretchDialog.Listener() {
            @Override
            public void onDialogPositiveClick(String name, int duration, String description, Bitmap bitmap) {
                Stretch stretch = new Stretch(bitmap, duration, description, name);
                stretches.add(stretch);
                stretchesAdapter.notifyDataSetChanged();
            }
        });

        addStretchDialog.show(getSupportFragmentManager(), TAG);
    }

    public void storeRoutineData(){
        this.storeRoutine();
    }

    public void storeRoutine(){

    }

    public void addStretchesToDatabase() {
        SQLiteDatabase db = stretchesDBHelper.getWritableDatabase();

        /* Add each stretch to the routine */
        for (Stretch stretch : stretches) {
            byte[] bytes = null;
            if (stretch.bitmap != null) {
                bytes = DbBitmapUtility.getBytes(stretch.bitmap);
            }

            ContentValues cv = new ContentValues();
            cv.put(DBTableConstants.STRETCH_NAME, stretch.getName());
            cv.put(DBTableConstants.STRETCH_IMAGE, bytes);
            cv.put(DBTableConstants.STRETCH_DURATION, stretch.getTime());
            cv.put(DBTableConstants.STRETCH_INSTRUCTION, stretch.getInstructions());

            db.insert(DBTableConstants.STRETCH_TABLE_NAME, null, cv);
        }
        db.close();
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

    /* todo confirmation cancel alert box */


}
