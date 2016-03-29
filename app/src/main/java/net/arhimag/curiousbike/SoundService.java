package net.arhimag.curiousbike;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class SoundService extends Service
    implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener
{
    private final IBinder soundServiceBinder = new SoundServiceBinder();
    private MediaPlayer mediaPlayer;

    private TourTrackerService tourTrackerService;
    private Intent tourTrackerServiceIntent;
    private boolean tourTrackerServiceBound=false;

    private ServiceConnection tourTrackerConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TourTrackerService.TourTrackerServiceBinder binder = (TourTrackerService.TourTrackerServiceBinder)service;
            //get service
            tourTrackerService = binder.getService();
            //pass list
            tourTrackerServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            tourTrackerServiceBound = false;
        }
    };


    public SoundService() {
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return soundServiceBinder;
    }

    @Override
    public boolean onUnbind( Intent intent )
    {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        mediaPlayer.reset();
        if ( tourTrackerServiceBound )
            tourTrackerService.trackStopped();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public class SoundServiceBinder extends Binder {
        SoundService getService() {
            return SoundService.this;
        }
    }

    public void playTrack(int trackID){
        mediaPlayer.reset();
        try{
            mediaPlayer.setDataSource(getApplicationContext(), TrackPool.get(trackID).getFileURI() );
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mediaPlayer.prepareAsync();
    }

    public void connectToTourTracker()
    {
        if( tourTrackerServiceIntent==null) {
            tourTrackerServiceIntent = new Intent(this, TourTrackerService.class);
        }

        bindService(tourTrackerServiceIntent, tourTrackerConnection, Context.BIND_AUTO_CREATE);
    }

    public void disconnectFromTourTracker()
    {
        if( tourTrackerServiceBound )
            unbindService(tourTrackerConnection);
    }
    
}
