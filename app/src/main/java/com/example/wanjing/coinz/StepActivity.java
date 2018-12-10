package com.example.wanjing.coinz;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.Activity;
import android.widget.TextView;

public class StepActivity extends AppCompatActivity implements SensorEventListener{
    SensorManager sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    Sensor stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        float d = getDistanceRun(steps);

        setContentView(R.layout.activity_step);
        TextView textView = (TextView) findViewById(R.id.distance);
        textView.setText(Float.toString(d));
    }

    @Override
    protected void onResume() {

        super.onResume();

        sManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onStop() {
        super.onStop();
        sManager.unregisterListener(this, stepSensor);
    }

    private long steps = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps++;
        }
    }

    //function to determine the distance run in kilometers using average step length for men and number of steps
    public float getDistanceRun(long steps){
        float distance = (float)(steps*78)/(float)100000;
        return distance;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

}
