package com.example.pibalstametbwi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.StringBufferInputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] orientationAngles = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] accelerometerData = new float[3];
    private float[] magnetometerData = new float[3];
    private TextView azimuth;
    private TextView pitch;
    private TextView waktuPengamatan;
    private TextView timer;
    private TextView textView;
    private int intervalRead = 100;
    private int intervalPengukuran = 0;
    private int waktuPengukuran = 0;
    private Handler mHandler;
    private Handler mHandler1;
    private Button startButton;
    private int buttonCondition = 0;
    private LinearLayout hasilPengukuran;
    private SimpleDateFormat hariTanggal = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat jamDetik = new SimpleDateFormat("HH:mm:ss");
    private int nomor = 1;
    NumberFormat formatter = NumberFormat.getInstance(Locale.US);
    DecimalFormat decimalFormat = (DecimalFormat) formatter;
    CountDownTimer countDownTimer = null;

    Runnable startPengukuran = new Runnable() {
        @Override
        public void run() {
            try {
                if (intervalPengukuran == 0){
                    createNewTextView("");
                    hasilPengukuran.removeView(textView);
                    intervalPengukuran = 90000 - (50 * 1000 / 3);
                    startTimer(intervalPengukuran);
                }
                else{
                    pengukuran();
                    intervalPengukuran = 60000;
                    startTimer(intervalPengukuran);
                }
                System.out.println("Interval Pengukuran " + intervalPengukuran);
            } finally {
                mHandler1.postDelayed(startPengukuran, intervalPengukuran);
            }
        }
    };

    Runnable startReadOrientation = new Runnable() {
        @Override
        public void run() {
            try{
                updateOrientationAngles();
            }finally {
                mHandler.postDelayed(startReadOrientation, intervalRead);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager =  (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        azimuth = (TextView) findViewById(R.id.azimuth);
        pitch = (TextView) findViewById(R.id.pitch);
        hasilPengukuran = (LinearLayout) findViewById(R.id.hasilPengukuran);
        waktuPengamatan = findViewById(R.id.waktuPengamatan);
        timer = findViewById(R.id.timer);
        Date currentTime = Calendar.getInstance().getTime();
        waktuPengamatan.setText(hariTanggal.format(currentTime));
        mHandler = new Handler();
        mHandler1 = new Handler();
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonCondition == 0){
                    buttonCondition = 1;
                    startButton.setText("STOP");
                    Toast.makeText(MainActivity.this, "Pengamatan Dimulai", Toast.LENGTH_SHORT).show();
                    nomor = 0;
                    hasilPengukuran.removeAllViews();
                    startPengukuran.run();
                }
                else{
                    buttonCondition = 0;
                    startButton.setText("START");
                    mHandler1.removeCallbacks(startPengukuran);
                    intervalPengukuran = 0;
                    cancelTimer();
                    Toast.makeText(MainActivity.this, "Pengamatan Berakhir", Toast.LENGTH_SHORT).show();
                }
            }
        });
    startReadOrientation.run();
    }



    public void pengukuran(){
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magnetometerData);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        for (int i = 0; i < orientationAngles.length; i++){
            orientationAngles[i] = orientationAngles[i] * (float) (180/Math.PI);
            if (i == 0 && orientationAngles[i] < 0){
                orientationAngles[i] = 360 + orientationAngles[i];
            }
            else if (i == 1 && orientationAngles[i] < 0 ){
                orientationAngles[i] = orientationAngles[i] * -1;
            }
        }
        Date currentTime1 = Calendar.getInstance().getTime();
        decimalFormat.applyPattern("###.#");
        String format = "\t";
        String stringPengukuran = formatter.format(orientationAngles[0]) + " \u00B0, " + formatter.format(orientationAngles[1]) + "\u00B0";
        stringPengukuran = nomor + ". " + jamDetik.format(currentTime1) + " ===> " + format + " " + stringPengukuran;
        hasilPengukuran.addView(createNewTextView(stringPengukuran));
        nomor = nomor +1;
    }

    public TextView createNewTextView (String text){
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setText(text);
        return textView;
    }

    @SuppressLint("SetTextI18n")
    public void updateOrientationAngles(){
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magnetometerData);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        for (int i = 0; i < orientationAngles.length; i++){
            orientationAngles[i] = orientationAngles[i] * (float) (180/Math.PI);
            if (i == 0 && orientationAngles[i] < 0){
                orientationAngles[i] = 360 + orientationAngles[i];
            }
            else if (i == 1 && orientationAngles[i] < 0 ){
                orientationAngles[i] = orientationAngles[i] * -1;
            }
        }
        decimalFormat.applyPattern("###.#");
        azimuth.setText(String.valueOf(formatter.format(orientationAngles[0])) + "\u00B0");
        pitch.setText(String.valueOf(formatter.format(orientationAngles[1])) + "\u00B0");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(event.values, 0, accelerometerData, 0, accelerometerData.length);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            System.arraycopy(event.values, 0, magnetometerData, 0, magnetometerData.length);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null){
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL,
                    SensorManager.SENSOR_DELAY_UI);
        }
        if (magnetometer != null){
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL,
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener((SensorEventListener) this);
        cancelTimer();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
        mHandler.removeCallbacks(startReadOrientation);
    }
    void startTimer(int durasi){
        countDownTimer = new CountDownTimer(durasi, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText("" + millisUntilFinished / 1000 );
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }
    void cancelTimer(){
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}