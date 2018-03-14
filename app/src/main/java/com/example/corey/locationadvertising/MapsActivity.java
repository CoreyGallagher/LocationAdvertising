package com.example.corey.locationadvertising;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;
import com.google.android.gms.location.LocationListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;

    //play services location
    private static final int MY_PERMISSION_REQUEST_CODE = 2897;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST=1997;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000; // 5 SECONDS
    private static int FASTEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 5000;

    VerticalSeekBar verticalSeekBar;
    DatabaseReference ref;
    GeoFire geoFire;
    Marker CurrentLocation;
    Marker Business;




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
        case MY_PERMISSION_REQUEST_CODE:
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if(checkPlayServices())
                {
                    buildGoogleApiClient();
                    createLocationRequest();
                    displayLocation();
                }

            }
            break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

        ref = FirebaseDatabase.getInstance().getReference("Location");
        geoFire = new GeoFire(ref);
        setUpLocation();

        verticalSeekBar = (VerticalSeekBar) findViewById( R.id.vertcalSeekBar );
        verticalSeekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mMap.animateCamera( CameraUpdateFactory.zoomTo(progress),2000,null );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        } );
    }



    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED)
        {
            //Request runtime permission
            ActivityCompat.requestPermissions( this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },MY_PERMISSION_REQUEST_CODE);
            }
            else
        {
            if(checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation( mGoogleApiClient );
        if(mLastLocation != null)
        {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

            //update to firebase
            geoFire.setLocation( "You", new GeoLocation( latitude, longitude ),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            //add marker
                            if (CurrentLocation != null)
                                CurrentLocation.remove();//remove old marker
                            CurrentLocation = mMap.addMarker( new MarkerOptions()
                                                        .position( new LatLng( latitude,longitude ) )
                                                        .title( "You" )) ;

                            //move camera to this position
                            mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng( latitude,longitude ), 15f));
                        }
                    } );


            Log.d("MAPS", String.format( "Your Location Changed: %f / %f", latitude,longitude ));
        }
        else
            Log.d("MAPS", "CAN NOT GET LOCATION");
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval( UPDATE_INTERVAL );
        mLocationRequest.setFastestInterval( FASTEST_INTERVAL );
        mLocationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        mLocationRequest.setSmallestDisplacement( DISPLACEMENT );
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable( this );
        if(resultCode != ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError( resultCode ))
                GooglePlayServicesUtil.getErrorDialog( resultCode,this,PLAY_SERVICES_RESOLUTION_REQUEST ).show();
            else
            {
                Toast.makeText( this,"Device is not supported",Toast.LENGTH_SHORT ).show();
                finish();
            }
            return  false;
        }
        return true;
    }

    //Manipulates the map once available.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Read information from database
        Task<QuerySnapshot> querySnapshotTask = FirebaseFirestore.getInstance().collection( "NotificationData" )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d( "DOCUMENT", document.getId() + " => " + document.getData() );
                                Log.i("TITLE SEARCH", ""+document.getData().get( "Title" ));
                                Log.i("BODY SEARCH", ""+document.getData().get( "Body" ));
                                Log.i("GEO-POINT", ""+document.getData().get( "LAT_LNG" ));
                                final String Title = String.valueOf( document.getData().get( "Title" ) );
                                final String Body = String.valueOf( document.getData().get( "Body" ) );
                                final GeoPoint Ref = (GeoPoint) document.getData().get( "LAT_LNG" );
                                final double lat = Ref.getLatitude();
                                final double lng = Ref.getLongitude();

                                //create geoQuery based on data retrieved
                                mMap.addMarker( new MarkerOptions()
                                        .position( new LatLng( lat,lng ) )
                                        .title( document.getId() )) ;
                                GeoQuery geoQuery;
                                geoQuery = geoFire.queryAtLocation( new GeoLocation( lat,lng ),0.15f );
                                geoQuery.addGeoQueryEventListener( new GeoQueryEventListener() {
                                    @Override
                                    public void onKeyEntered(String key, GeoLocation location) {
                                        sendNotification(Title, String.format(Body, key));
                                    }

                                    @Override
                                    public void onKeyExited(String key) {
                                        //sendNotification("MAPS", String.format("%s Leaving LYIT", key));

                                    }

                                    @Override
                                    public void onKeyMoved(String key, GeoLocation location) {

                                    }

                                    @Override
                                    public void onGeoQueryReady() {

                                    }

                                    @Override
                                    public void onGeoQueryError(DatabaseError error) {
                                        Log.e("ERROR",""+error);
                                    }
                                } );

                            }
                        } else {
                            Log.d( "DOCUMENT", "Error getting documents: ", task.getException() );
                        }
                    }
                } );

    }

    private void sendNotification(String title, String content) {
        Notification.Builder builder = new Notification.Builder( this )
                .setSmallIcon( R.mipmap.ic_launcher_round )
                .setContentTitle( title )
                .setContentText(content);
        NotificationManager Manager = (NotificationManager)this.getSystemService( Context.NOTIFICATION_SERVICE );
        Intent intent = new Intent (this,MapsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent( contentIntent );
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;

        Manager.notify(new Random().nextInt(),notification);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    public void onProviderEnabled(String provider) {

    }


    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
            displayLocation();
            startLocationUpdates();

    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates( mGoogleApiClient, mLocationRequest,this );
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
