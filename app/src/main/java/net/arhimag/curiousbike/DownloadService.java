package net.arhimag.curiousbike;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Maxim.Statsenko on 10.03.2016.
 */
public class DownloadService extends IntentService {
    public static final int UPDATE_PROGRESS = 8344;
    public DownloadService() {
        super("DownloadService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("BikeDownload","1");
        String urlToDownload = intent.getStringExtra("url");
        String pathToOutput = intent.getStringExtra("destination");
        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        Log.d("BikeDownload","2");
        try {
            URL url = new URL(urlToDownload);
            Log.d("BikeDownload","3");
            URLConnection connection = url.openConnection();
            Log.d("BikeDownload","4");
            connection.connect();
            Log.d("BikeDownload", "5");
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();
            Log.d("BikeDownload","6");
            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());
            Log.d("BikeDownload","7");
            OutputStream output = new FileOutputStream(pathToOutput);
            Log.d("BikeDownload","8");

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                Log.d("BikeDownload","9");
                total += count;
                // publishing the progress....
                Bundle resultData = new Bundle();
                Log.d("BikeDownload","10");
                resultData.putInt("progress", (int) (total * 100 / fileLength));
                Log.d("BikeDownload", "11");
                receiver.send(UPDATE_PROGRESS, resultData);
                Log.d("BikeDownload", "12");
                output.write(data, 0, count);
                Log.d("BikeDownload", "13");
            }
            Log.d("BikeDownload","14");
            output.flush();
            Log.d("BikeDownload", "15");
            output.close();
            Log.d("BikeDownload", "16");
            input.close();
            Log.d("BikeDownload", "17");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("BikeDownload","18");
        Bundle resultData = new Bundle();
        Log.d("BikeDownload","19");
        resultData.putInt("progress", 100);
        Log.d("BikeDownload", "20");
        receiver.send(UPDATE_PROGRESS, resultData);
        Log.d("BikeDownload", "21");
    }
}