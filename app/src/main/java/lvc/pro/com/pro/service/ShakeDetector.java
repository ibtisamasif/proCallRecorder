package lvc.pro.com.pro.service;

import android.content.Context;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.util.Log;

import lvc.pro.com.pro.DialogActivity;

/**
 * Created by chicmic on 3/13/18.
 */

public class ShakeDetector implements SensorEventListener {
    private long lastUpdate;
    private OnShakeListener mListener;
    private Context context;
    private static int count = 0;


    public ShakeDetector(Context context) {
        // vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.context = context;
    }

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    public interface OnShakeListener {
        public void onShake(int count);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void getAccelerometer(SensorEvent event) {


        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];
        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 10) {
            if (actualTime - lastUpdate < 500) {
                return;
            }
            lastUpdate = actualTime;
            Log.d(ShakeDetector.class.getSimpleName(), " shake detected");
            count++;
            if (count == 3) {
                CallDetectionService.mShaked = true;
                count = 0;
            }
            // Log.d(ShakeDetector.class.getSimpleName()," shaked");

            // vibrator.vibrate(vibrationTime);
          /*  if((parameters!=null)&&(parameters.getFlashMode()== Camera.Parameters.FLASH_MODE_TORCH))
            {

            }
            else {
                camera = Camera.open();
                parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();

            }*/
        }
    }
}
