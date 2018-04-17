package com.example.coordinates;

/**
 * Created by arpi on 2018-03-22.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Vibrator;

public class Compass extends AppCompatActivity implements SensorEventListener {

    ImageView compass_img;
    TextView txt_compass;
    int degreeRotation;
    private SensorManager sm;
    private Sensor mRotationV, accelerometer, magnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private Vibrator v;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        compass_img = findViewById(R.id.img_compass);
        txt_compass = findViewById(R.id.txt_azimuth);

            v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Sets the sensor type
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            degreeRotation = (int) (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, orientation, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null, orientation, mLastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);
            degreeRotation = (int) (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0]) + 360) % 360;
        }

        //Rounds the value in degreeRotation
        degreeRotation = Math.round(degreeRotation);

        //Sets the rotation of the image
        compass_img.setRotation(-degreeRotation);

        //Sets default value of direction
        String direction = "NW";

        if (degreeRotation >= 350 || degreeRotation <= 10)
            direction = "N";
        if (degreeRotation < 350 && degreeRotation > 280)
            direction = "NW";
        if (degreeRotation <= 280 && degreeRotation > 260)
            direction = "W";
        if (degreeRotation <= 260 && degreeRotation > 190)
            direction = "SW";
        if (degreeRotation <= 190 && degreeRotation > 170)
            direction = "S";
        if (degreeRotation <= 170 && degreeRotation > 100)
            direction = "SE";
        if (degreeRotation <= 100 && degreeRotation > 80)
            direction = "E";
        if (degreeRotation <= 80 && degreeRotation > 10)
            direction = "NE";

        if(degreeRotation == 0) {
            v.vibrate(500);
        }


        //Sets the text of compass text
        txt_compass.setText(degreeRotation + "° " + direction);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //Starts the sensors
    public void start() {
        if (sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                noSensorsAlert();
            }
            else {
                accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{
            mRotationV = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = sm.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }

    //Notifys the user that their device does not support the application
    public void noSensorsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    //Stops the sensor
    public void stop() {
        if (haveSensor) {
            sm.unregisterListener(this, mRotationV);
        }
        else {
            sm.unregisterListener(this, accelerometer);
            sm.unregisterListener(this, magnetometer);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

}
