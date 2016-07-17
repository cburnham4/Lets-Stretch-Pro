package letshangllc.stretchingroutinespro.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import letshangllc.stretchingroutinespro.Data.DBTableConstants;
import letshangllc.stretchingroutinespro.Data.RoutineStretches;
import letshangllc.stretchingroutinespro.Data.StretchesDBHelper;
import letshangllc.stretchingroutinespro.JavaObjects.Stretch;
import letshangllc.stretchingroutinespro.R;
import letshangllc.stretchingroutinespro.helpers.DbBitmapUtility;

public class StretchActivity extends AppCompatActivity {
    private static final String TAG = StretchActivity.class.getSimpleName();

    private TextView tv_stretchName;
    private TextView tv_instructions;
    private ImageView img_stretch;
    private TextView tv_timer;
    private int currentStretch;

    private CountDownTimer countDownTimer;


    /* Alarm */
    private Ringtone r;

    private ArrayList<Stretch> stretches;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretch);

        Intent intent = getIntent();
        int routineId = intent.getIntExtra(getString(R.string.routine_index_intent), 0);

        /* todo Add routine index */
        switch (routineId){
            case 99999:
                stretches = new ArrayList<>(Arrays.asList(RoutineStretches.backStretches));
                break;
            case 99998:
                stretches = new ArrayList<>(Arrays.asList(RoutineStretches.morningStretches));
                break;
            case 99997:
                stretches = new ArrayList<>(Arrays.asList(RoutineStretches.fullBodyStretches));
                break;
            case 99996:
                stretches = new ArrayList<>(Arrays.asList(RoutineStretches.legStretches));
                break;
            case 99995:
                stretches = new ArrayList<>(Arrays.asList(RoutineStretches.beforeBedStretches));
                break;
            default:
                stretches = new ArrayList<>();
        }

        getStretches(routineId);


        this.findViews();
        currentStretch = 0;


        startStretches();
    }

    private void findViews(){
        tv_stretchName = (TextView) findViewById(R.id.tv_stretchName);
        tv_instructions = (TextView) findViewById(R.id.tv_instructions);
        img_stretch = (ImageView) findViewById(R.id.img_stretchPhoto);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
    }

    private void startStretches(){
        if(currentStretch == stretches.size()){
            finish();
            return;
        }
        Stretch stretch = stretches.get(currentStretch);
        tv_stretchName.setText(stretch.getName());
        tv_instructions.setText(stretch.getInstructions());
        if(stretch.bitmap != null){
            img_stretch.setVisibility(View.VISIBLE);
            img_stretch.setImageBitmap(stretch.bitmap);
        }else if(stretch.getDrawableIndex() == 0){
            img_stretch.setVisibility(View.INVISIBLE);
        }else{
            img_stretch.setVisibility(View.VISIBLE);
            img_stretch.setImageDrawable(ContextCompat.getDrawable(this, stretch.getDrawableIndex()));
        }


        //tv_timer.setText(stretch.getDuration() +"");
        startCountdown(stretch.getDuration()*1000);
    }

    public void nextExerciseOnClick(View view){
        countDownTimer.cancel();
        currentStretch++;
        startStretches();
    }

    private void startCountdown(int timer){
        countDownTimer = new CountDownTimer(timer, 500) {

            public void onTick(long millisUntilFinished) {
                int time = Math.round(millisUntilFinished * 0.001f);
                Log.i(TAG, String.format(Locale.getDefault(), "%d" ,time));
                tv_timer.setText(String.format(Locale.getDefault(), "%d" ,time));
            }

            public void onFinish() {
                playAlarm();
                currentStretch++;
                startStretches();

            }
        };
        countDownTimer.start();
    }

    public void getStretches(int routineId){
        StretchesDBHelper stretchesDBHelper = new StretchesDBHelper(this);
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
        int i = 1;
        c.moveToFirst();
        while(!c.isAfterLast()){
            Log.i(TAG, "Run: " + i++);
            Log.i(TAG, "Duration index: " + c.getColumnIndex(DBTableConstants.STRETCH_DURATION));
            int duration = c.getInt(c.getColumnIndex(DBTableConstants.STRETCH_DURATION));
            String instruction = c.getString(c.getColumnIndex(DBTableConstants.STRETCH_INSTRUCTION));
            byte[] bytes = c.getBlob(c.getColumnIndex(DBTableConstants.STRETCH_IMAGE));
            String name = c.getString(c.getColumnIndex(DBTableConstants.STRETCH_NAME));
            if(bytes == null){
                stretches.add(new Stretch(name, instruction, duration));
            }else{
                Bitmap bitmap = DbBitmapUtility.getImage(bytes);
                stretches.add(new Stretch(name, instruction, bitmap, duration));
            }

            c.moveToNext();
        }

        c.close();
        db.close();
    }

    private void playAlarm(){
        /* Get the alarm tone */
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // If notication is null the use ringtone
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        try {
            /* Play the alarm */
            r = RingtoneManager.getRingtone(this, alert);
            r.setStreamType(AudioManager.STREAM_ALARM);
            r.play();
        }catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    r.stop();
                } catch (Exception e) {
                    r.stop();
                }

            }
        }).start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
        finish();
    }

}
