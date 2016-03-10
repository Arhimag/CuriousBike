package net.arhimag.curiousbike;

import android.app.Service;
import android.content.Intent;
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
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.reset();
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

    public void playTrack(){
        mediaPlayer.reset();
        try{
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse("android.resource://net.arhimag.curiousbike/" + R.raw.test) );
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mediaPlayer.prepareAsync();
    }

    
}
