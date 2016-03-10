package net.arhimag.curiousbike;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;

/**
 * Created by arhimag on 29.01.16.
 */
public class Track
{
    /**
     * URI физического файла в директории приложения
     */
    protected Uri fileURI;
    /**
     * Уникальный идетификатор трека. Он же является и ссылкой на трек в файле описания
     * 0 - не инициализированное значение
     */
    protected int trackId;
    /**
     * Hash по которому осуществляется физический доступ к файлу. Чаще всего это и есть имя файла
     */
    private String trackIdHash;
    /**
     * Степень загруженности файла
     */
    private int downloadProgress;

    public Track()
    {
        trackId = 0;
        fileURI = null;
    }

    public Track(int trackId)
    {
        setTrackId(trackId);
    }

    private String getFilePath()
    {
        File chkFile = new File ( Constants.AUDIO_FILES_LOCATION, trackIdHash + '.' + Constants.AUDIO_FILES_FORMAT);
        return chkFile.getPath();
    }

    public void setTrackId( int trackId )
    {
        this.trackId = trackId;
        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(Integer.toString(trackId).getBytes(), 0, Integer.toString(trackId).length());
            trackIdHash = new BigInteger(1, m.digest()).toString(16);
        }
        catch(Exception e)
        {
            trackIdHash = Integer.toString(trackId);
        }

        File chkFile = new File ( getFilePath() );

        if( chkFile.exists() ) {
            fileURI = Uri.fromFile(chkFile);
            downloadProgress = 100;
        }
        else {
            fileURI = null;
            downloadProgress = 0;
        }
    }

    public int getTrackId()
    {
        return trackId;
    }

    public Uri getFileURI()
    {
        return fileURI;
    }

    public boolean isDownloaded ()
    {
        return ( fileURI != null ) || ( trackId == 0 );
    }

    private String getTrackURL()
    {
        return Constants.FILE_DOWNLOAD_URL + trackIdHash + '.' +  Constants.AUDIO_FILES_FORMAT;
    }

    private class DownloadReceiver extends ResultReceiver {
        
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                downloadProgress = resultData.getInt("progress");
                if (downloadProgress == 100) {
                    setTrackId(trackId);
                }
            }
        }
    }

    public void downloadTrack(Context context)
    {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("url", getTrackURL());
        intent.putExtra("destination", getFilePath());
        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
        context.startService(intent);
    }

    public int getDownloadProgress()
    {
        return downloadProgress;
    }
}
