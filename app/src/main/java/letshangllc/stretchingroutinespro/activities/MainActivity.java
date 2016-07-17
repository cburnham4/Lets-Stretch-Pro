package letshangllc.stretchingroutinespro.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import letshangllc.stretchingroutinespro.data.DBTableConstants;
import letshangllc.stretchingroutinespro.data.Routines;
import letshangllc.stretchingroutinespro.data.StretchesDBHelper;
import letshangllc.stretchingroutinespro.R;
import letshangllc.stretchingroutinespro.JavaObjects.RoutineItem;
import letshangllc.stretchingroutinespro.adapaters.RoutineListAdapter;
import letshangllc.stretchingroutinespro.helpers.DbBitmapUtility;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    private ListView listView;
    private ArrayList<RoutineItem> routineItems;
    private RoutineListAdapter routineListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.lv_routines);
        registerForContextMenu(listView);
        //this.runAds();

    }

    public void addRoutinesFromDB(){
        StretchesDBHelper stretchesDBHelper = new StretchesDBHelper(this);
        SQLiteDatabase sqLiteDatabase = stretchesDBHelper.getReadableDatabase();

        String[] projection = {DBTableConstants.ROUTINE_NAME, DBTableConstants.ROUTINE_IMAGE,
            DBTableConstants.ROUTINE_ID
        };

        Cursor c = sqLiteDatabase.query(DBTableConstants.ROUTINE_TABLE_NAME, projection, null,
                null, null, null, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            String routineName = c.getString(0);
            Log.i(TAG, routineName);
            byte[] bytes = c.getBlob(1);

            int id = c.getInt(2);
            if(bytes == null){
                routineItems.add(new RoutineItem(id,R.drawable.silhouette, routineName));
            }else{
                Bitmap bitmap = DbBitmapUtility.getImage(bytes);
                routineItems.add(new RoutineItem(id, bitmap, routineName));
            }
            c.moveToNext();
        }
        routineListAdapter.notifyDataSetChanged();;
        c.close();
        sqLiteDatabase.close();
    }

    public void createRoutineOnClick(View view){
        Intent intent = new Intent(this, CreateRoutineActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Routines routines = new Routines();
        routineItems = routines.getRoutines();

        routineListAdapter = new RoutineListAdapter(this, routineItems);

        listView.setAdapter(routineListAdapter);
        //addRoutinesFromDB();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                /* When a day is selected go to the Lifts Activity */
                Log.i(TAG, "Item Clicked");
                Intent intent = new Intent(MainActivity.this, StretchActivity.class);

                RoutineItem item = routineListAdapter.getItem(position);

                intent.putExtra(getString(R.string.routine_index_intent), item.id);
                startActivity(intent);
            }
        });

        this.addRoutinesFromDB();

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
                editRoutine(routineItems.get((int) info.id));
                return true;
            case R.id.delete:
                confirmDelete(routineItems.get((int) info.id));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void editRoutine(RoutineItem routineItem){
        Intent intent = new Intent(MainActivity.this, EditRoutineActivity.class);

        intent.putExtra(getString(R.string.routine_index_intent), routineItem.id);
        intent.putExtra(getString(R.string.routine_name_extra), routineItem.getName());
        startActivity(intent);
    }

    private void confirmDelete(final RoutineItem routineItem){
        if(routineItem.id > 9900){
            Toast.makeText(this, "You cannot delete this routine", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_confirm));

        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRoutine(routineItem);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteRoutine(RoutineItem routineItem){
        StretchesDBHelper dbHelper = new StretchesDBHelper(this);
        int routineId = routineItem.id;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        /* Delete from Exercise table and routines table by using exercise id */
        db.delete(DBTableConstants.ROUTINE_STRETCH_TABLE, DBTableConstants.ROUTINE_ID + " = " + routineId, null);
        db.delete(DBTableConstants.ROUTINE_TABLE_NAME, DBTableConstants.ROUTINE_ID + " = " + routineId, null);

        db.close();

        /* Remove item from list and update list view */
        routineItems.remove(routineItem);
        routineListAdapter.notifyDataSetChanged();
    }
}
