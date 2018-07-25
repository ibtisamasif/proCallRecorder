package lvc.pro.com.pro.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.callrecorder.pro.R;

import lvc.pro.com.pro.MainActivity;
import lvc.pro.com.pro.Splash_Activity;

/**
 * Created by chicmic on 3/13/18.
 */

public class ShakeDetectionService extends Service {
    private IBinder mBinder = new LocalBinder();
    private SensorManager sensorManager;
    private Sensor sensor;
    private ShakeDetector shakeDetector;
    private Notification n;
    private int codeNotification = 300;

    @Override
    public void onCreate() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = (Sensor) sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector(getApplicationContext());
        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {

                // Toast.makeText(getApplicationContext(),"SHAKE",Toast.LENGTH_SHORT).show();
            }
        });
        sensorManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public ShakeDetectionService getServiceInstance() {
            return ShakeDetectionService.this;
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, Splash_Activity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        /*Notification notification = new Notification.Builder(this)
               // .setContentTitle(getString(R.string.app_name))
                .setContentTitle("SHAKE SERVICE")
                .setContentIntent(pendingIntent)
                //.setSmallIcon(R.drawable.logo)
               // .setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo), 128, 128, false))
                .build();*/

        Notification notification = new Notification.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentTitle("Shake To Record Calls")
                //.setTicker("SHAKE SERVICE")
                .setContentText("Running in background...")
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo), 128, 128, false))
//                .setWhen(0)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        startForeground(codeNotification, notification);
        n = notification;
        return START_STICKY;
    }
}
