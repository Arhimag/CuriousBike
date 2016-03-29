package net.arhimag.curiousbike;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.multiplayer.InvitationEntity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Date;
import java.util.jar.Manifest;

public class InvitationActivity extends FragmentActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    private GoogleApiClient googleApiClient;
    private static final int GOOGLE_API_REQUEST_RESOLVE_ERROR = 1001;
    private static final String GOOGLE_API_DIALOG_ERROR = "dialog_error";
    private boolean resolvingGoogleApiError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_gapi_error";
    private static final int DEFAULT_LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private static final int DEFAULT_LOCATION_INTERVAL = 1000;
    private static final int DEFAULT_LOCATION_FASTEST_INTERVAL = 500;

    private TextView latitudeView;
    private TextView longitudeView;

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


    private BackgroundLocationService locationService;
    private Intent locationServiceIntent;
    private boolean locationServiceBound=false;

    private ServiceConnection locationConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundLocationService.BackgroundLocationServiceBinder binder = (BackgroundLocationService.BackgroundLocationServiceBinder)service;
            //get service
            locationService = binder.getService();
            //pass list
            locationServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationServiceBound = false;
        }
    };


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resolvingGoogleApiError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        setContentView(R.layout.activity_invitation);

        latitudeView = (TextView) findViewById( R.id.latitude);
        longitudeView = (TextView) findViewById( R.id.longitude);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(  locationServiceBound ) {
                    locationService.connectToTourTracker();
                    Location lastLocation = locationService.getLastLocation();
                    if (lastLocation != null) {
                        latitudeView.setText(String.valueOf(lastLocation.getLatitude()));
                        longitudeView.setText(String.valueOf(lastLocation.getLongitude()));
                    }
                }
                if( soundServiceBound);
                {
                    soundService.connectToTourTracker();
                    soundService.playTrack(temp_track_id);
                    temp_track_id = temp_track_id % 2 + 1;
                }

            }
        });
    }

   @Override
    public void onConnected(Bundle connectionHint)
    {
    }

    @Override
    public void onConnectionSuspended( int cause )
    {

    }

    @Override
    public void onConnectionFailed( ConnectionResult result )
    {
        if( resolvingGoogleApiError )
            return;
        else if ( result.hasResolution() )
        {
            try
            {
                resolvingGoogleApiError = true;
                result.startResolutionForResult(this, GOOGLE_API_REQUEST_RESOLVE_ERROR);
            }
            catch (IntentSender.SendIntentException e )
            {
                googleApiClient.connect();
            }
        }
        else
        {
            showGAPIErrorDialog(result.getErrorCode());
            resolvingGoogleApiError = true;
        }
    }

    private void showGAPIErrorDialog( int errorCode )
    {
        GAPIErrorDialogFragment dialogFragment = new GAPIErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(GOOGLE_API_DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    public void onGAPIDialogDismissed()
    {
        resolvingGoogleApiError = false;
    }

    public static class GAPIErrorDialogFragment extends DialogFragment
    {
        public GAPIErrorDialogFragment() {}

        @Override
        public Dialog onCreateDialog( Bundle savedInstanceState )
        {
            int errorCode = this.getArguments().getInt(GOOGLE_API_DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), errorCode, GOOGLE_API_REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog)
        {
            ( (InvitationActivity) getActivity()).onGAPIDialogDismissed();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GOOGLE_API_REQUEST_RESOLVE_ERROR) {
            resolvingGoogleApiError = false;
            if (resultCode == RESULT_OK) {
                if (!googleApiClient.isConnecting() &&
                        !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invitation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if( !TrackPool.get(1).isDownloaded())
            TrackPool.get(1).downloadTrack(this);
        if( !TrackPool.get(2).isDownloaded())
            TrackPool.get(2).downloadTrack(this);

        if(soundServiceIntent==null){
            soundServiceIntent = new Intent(this, SoundService.class);
            bindService(soundServiceIntent, soundConnection, Context.BIND_AUTO_CREATE);
            startService(soundServiceIntent);
        }

        if( locationServiceIntent==null){
            locationServiceIntent = new Intent(this, BackgroundLocationService.class);
            bindService(locationServiceIntent, locationConnection, Context.BIND_AUTO_CREATE);
            startService(locationServiceIntent);
        }

        if( tourTrackerServiceIntent==null){
            tourTrackerServiceIntent = new Intent(this, TourTrackerService.class);
            bindService(tourTrackerServiceIntent, tourTrackerConnection, Context.BIND_AUTO_CREATE);
            startService(tourTrackerServiceIntent);
        }

        if( !resolvingGoogleApiError)
            googleApiClient.connect();
        googleApiClient.disconnect();
    }


    @Override
    protected  void onResume()
    {
        super.onResume();
    }

    @Override
    protected  void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        stopService(soundServiceIntent);
        soundService=null;
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, resolvingGoogleApiError);
    }


}
