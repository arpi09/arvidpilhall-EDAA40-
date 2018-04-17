package com.example.coordinates;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by arpi on 2018-03-22.
 */

public class Accelerometer extends AppCompatActivity implements SensorEventListener {

    static final float ALPHA = 0.25f; // if ALPHA = 1 OR 0, no filter applies.

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float lastX, lastY, lastZ;
    private float[] gravSensorVals;
    private float[] currGravSensorVals;
    private SensorManager sm;
    private Sensor accelerometer;
    private float vibrateThreshold = 0;
    public Vibrator v;
    private TextView x_value;
    private TextView y_value;
    private TextView z_value;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayed);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 40;
        } else {
            // fai! we dont have an accelerometer!
        }

        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        x_value = findViewById(R.id.xvalue);
        y_value = findViewById(R.id.yvalue);
        z_value = findViewById(R.id.zvalue);

    }

    public void onSensorChanged(SensorEvent event) {
        x_value.setText("X-acceleration: " + Float.toString(Math.round(deltaX)));
        y_value.setText("Y-acceleration: " + Float.toString(Math.round(deltaY)));
        z_value.setText("Z-acceleration: " + Float.toString(Math.round(deltaZ)));

        //Adds a lowpass filter to current values of accelerometer
        currGravSensorVals = lowPass(event.values.clone(), currGravSensorVals);

        //Calculates the change of the values of the accelerometer
        deltaX = Math.abs((lastX - currGravSensorVals[0]));
        deltaY = Math.abs((lastY - currGravSensorVals[1]));
        deltaZ = Math.abs((lastZ - currGravSensorVals[2]));

        //Adds low pass filter to values of accelerometer
        gravSensorVals = lowPass(event.values.clone(), gravSensorVals);

        //Sets values of x,y and z that we get from accelerometer
        lastX = gravSensorVals[0];
        lastY = gravSensorVals[1];
        lastZ = gravSensorVals[2];

        vibrate();


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void noSensorsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    // if the change in the accelerometer value is big enough, then vibrate!
// our threshold is MaxValue/2
    public void vibrate() {
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
        }
    }

    protected float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;

    }
}

