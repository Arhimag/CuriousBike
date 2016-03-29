package net.arhimag.curiousbike;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by Maxim.Statsenko on 29.03.2016.
 */
public class TourTrackerService extends Service
        implements LocationAlertable
{
    private final IBinder soundServiceBinder = new TourTrackerServiceBinder();

    private SoundService soundService;
    private Intent soundServiceIntent;
    private boolean soundServiceBound=false;

    private int temp_track_id = 1;

    private ServiceConnection soundConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SoundService.SoundServiceBinder binder = (SoundService.SoundServiceBinder)service;
            //get service
            soundService = binder.getService();
            //pass list
            soundServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            soundServiceBound = false;
        }
    };

    public TourTrackerService() {
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        if(soundServiceIntent==null){
            soundServiceIntent = new Intent(this, SoundService.class);
            bindService(soundServiceIntent, soundConnection, Context.BIND_AUTO_CREATE);
            startService(soundServiceIntent);
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return soundServiceBinder;
    }

    @Override
    public boolean onUnbind( Intent intent )
    {
        return false;
    }

    public class TourTrackerServiceBinder extends Binder {
        TourTrackerService getService() {
            return TourTrackerService.this;
        }
    }

    @Override
    public void newLocationAlert( Location location )
    {
        Log.d("TourTrackerService","New location");
    }

    public void trackStopped()
    {
        Log.d("TourTrackerService", "TrackStopped");
    }
}
