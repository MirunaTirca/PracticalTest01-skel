package ro.pub.cs.systems.eim.practicaltest01.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import ro.pub.cs.systems.eim.practicaltest01.general.Constants;

public class PracticalTest01Service extends Service {
    private MyThread myThread = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int firstNumber = intent.getIntExtra(Constants.FIRST_NUMBER, -1);
        int secondNumber = intent.getIntExtra(Constants.SECOND_NUMBER, -1);
        Log.d(Constants.PROCESSING_THREAD_TAG, "shhsssss!!!!!!");
        myThread = new MyThread(this, firstNumber, secondNumber);
        myThread.start();
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        myThread.stopThread();
    }
}