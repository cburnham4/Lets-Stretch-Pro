package letshangllc.stretchingroutinespro.helpers;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import letshangllc.stretchingroutinespro.Data.DBTableConstants;
import letshangllc.stretchingroutinespro.Data.StretchesDBHelper;
import letshangllc.stretchingroutinespro.JavaObjects.Stretch;

/**
 * Created by Carl on 7/16/2016.
 */
public class StoreRoutineInBackground  extends AsyncTask<Void, Void, Void> {
    private static final String TAG = StoreRoutineInBackground.class.getSimpleName();

    private ArrayList<Stretch> stretches;
    private int routineId;
    private StretchesDBHelper stretchesDBHelper;
    private Context context;
    private StoringRoutineComplete callback;

    private ProgressDialog dialog;

    public StoreRoutineInBackground(ArrayList<Stretch> stretches, int routineId,
                                    StretchesDBHelper stretchesDBHelper, Context context,
                                    StoringRoutineComplete callback) {
        this.stretches = stretches;
        this.routineId = routineId;
        this.stretchesDBHelper = stretchesDBHelper;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage("Storing Data");
        dialog.show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        callback.onDataStored();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        /* Add each stretch to the routine */
        for (Stretch stretch : stretches) {
            byte[] bytes = null;
            if (stretch.bitmap != null) {
                bytes = DbBitmapUtility.getBytes(stretch.bitmap);
            }
            SQLiteDatabase db = stretchesDBHelper.getWritableDatabase();
            /* Insert stretch into db */
            ContentValues cv = new ContentValues();
            cv.put(DBTableConstants.STRETCH_NAME, stretch.getName());
            cv.put(DBTableConstants.STRETCH_IMAGE, bytes);
            cv.put(DBTableConstants.STRETCH_DURATION, stretch.getDuration());
            cv.put(DBTableConstants.STRETCH_INSTRUCTION, stretch.getInstructions());
            int stretchId = (int) db.insert(DBTableConstants.STRETCH_TABLE_NAME, null, cv);

            /* TODO check stretch id */
            db.close();

            db = stretchesDBHelper.getWritableDatabase();


            /* Insert routine Id and stretch ID into db */
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBTableConstants.ROUTINE_ID, routineId);
            contentValues.put(DBTableConstants.STRETCH_ID, stretchId);
            db.insert(DBTableConstants.ROUTINE_STRETCH_TABLE, null, contentValues);
            db.close();
        }
        Log.i(TAG, "Stored stretches");
        return null;
    }
}
