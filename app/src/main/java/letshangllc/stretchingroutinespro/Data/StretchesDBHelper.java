package letshangllc.stretchingroutinespro.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cvburnha on 6/22/2016.
 */
public class StretchesDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private  static final String DATABASE_NAME ="StretchesDatabase.db";
    //private static final String DICTIONARY_TABLE_NAME = "dictionary";

    private static final String STRETCHES_TABLE_CREATE =
            "CREATE TABLE "+ DBTableConstants.STRETCH_TABLE_NAME + "("
                    + DBTableConstants.STRETCH_ID +" integer primary key AUTOINCREMENT, "
                    + DBTableConstants.STRETCH_NAME + " text, "
                    + DBTableConstants.STRETCH_DURATION + " integer, "
                    + DBTableConstants.STRETCH_INSTRUCTION + " text, "
                    + DBTableConstants.STRETCH_IMAGE + " BLOB "
                    + " )";

    private static final String ROUTINE_TABLE_CREATE =
            "CREATE TABLE "+  DBTableConstants.ROUTINE_TABLE_NAME +" ("
                    + DBTableConstants.ROUTINE_ID + " integer primary key AUTOINCREMENT, " //spotid
                    + DBTableConstants.ROUTINE_NAME +"  TEXT, "
                    + DBTableConstants.ROUTINE_IMAGE + " BLOB "
                    + " )";

    private static final String ROUTINE_STRETCH_TABLE_CREATE =
            "CREATE TABLE "+  DBTableConstants.ROUTINE_STRETCH_TABLE +" ("
                    + DBTableConstants.ROUTINE_ID + " integer, "
                    + DBTableConstants.STRETCH_ID +"  integer primary key"
                    + ")";

    public StretchesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        //if(checkDataBase()){
        db.execSQL(STRETCHES_TABLE_CREATE);
        db.execSQL(ROUTINE_TABLE_CREATE);
        db.execSQL(ROUTINE_STRETCH_TABLE_CREATE);
        //
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // If you need to add a new column
        if (newVersion > oldVersion) {
        }
    }
}