package ntou.cs.lab505.serenity.sound;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import ntou.cs.lab505.serenity.database.BandSettingAdapter;
import ntou.cs.lab505.serenity.database.FreqSettingAdapter;
import ntou.cs.lab505.serenity.database.IOSettingAdapter;
import ntou.cs.lab505.serenity.datastructure.BandGainSetUnit;
import ntou.cs.lab505.serenity.datastructure.IOSetUnit;
import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.stream.SoundInputPool;
import ntou.cs.lab505.serenity.stream.SoundOutputPool;
import ntou.cs.lab505.serenity.thread.SoundInputThread;

/**
 * Created by alan on 2015/7/3.
 */
public class SoundService extends Service {

    // service state.
    private boolean serviceState = false;

    // read data from database.
    IOSetUnit ioSetUnit;
    int semitoneValue;
    ArrayList<BandGainSetUnit> bandGainSetUnitArrayList;

    // sound process threads object.



    public class SoundServiceBinder extends Binder {
        public SoundService getService() {
            return SoundService.this;
        }
    }

    private final IBinder mBinder = new SoundServiceBinder();

    @Override
    public void onCreate() {
        Log.d("SoundService", "in onCreate.");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d("SoundService", "in onDestroy.");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void serviceInitParams() {

        // read IO setting data.
        IOSettingAdapter ioSettingAdapter = new IOSettingAdapter(this.getApplicationContext());
        ioSettingAdapter.open();
        ioSetUnit = ioSettingAdapter.getData();
        ioSettingAdapter.close();

        // read freqshift setting data.
        FreqSettingAdapter freqSettingAdapter = new FreqSettingAdapter(this.getApplicationContext());
        freqSettingAdapter.open();
        semitoneValue = freqSettingAdapter.getData();
        freqSettingAdapter.close();

        // read band gain setting data.
        BandSettingAdapter bandSettingAdapter = new BandSettingAdapter(this.getApplicationContext());
        bandSettingAdapter.open();
        bandGainSetUnitArrayList = bandSettingAdapter.getData();
        bandSettingAdapter.close();
    }

    public void serviceStart() {
        Log.d("SoundService", "in serviceStart. success.");
        serviceState = true;


        SoundInputPool soundInputPool = new SoundInputPool(8000, 0);
        soundInputPool.open();
        SoundOutputPool soundOutputPool = new SoundOutputPool(8000, 1, 2, 0);
        soundOutputPool.open();


        while (true) {
            //Log.d("SoundService", "debug: data: " + soundInputPool.read().getVectorLength());
            long time1 = System.nanoTime();
            SoundVectorUnit data = soundInputPool.read();
            long time2 = System.nanoTime();
            Log.d("SoundService", "debug: time1: " + (time2 - time1) / 1000000);
            soundOutputPool.write(soundInputPool.read());
            long time3 = System.nanoTime();
            Log.d("SoundService", "debug: time2: " + (time3 - time2) / 1000000);
        }

        //soundInputPool.close();
        //soundOutputPool.close();
    }

    public void serviceStop() {
        Log.d("SoundService", "in serviceStop. success.");
        serviceState = false;


    }

    public boolean getServiceState() {
        return serviceState;
    }
}