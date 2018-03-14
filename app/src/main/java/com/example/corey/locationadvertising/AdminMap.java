package com.example.corey.locationadvertising;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.corey.locationadvertising.AdminActivity.BODY_KEY;
import static com.example.corey.locationadvertising.AdminActivity.LAT_LNG;
import static com.example.corey.locationadvertising.AdminActivity.TITLE_KEY;

public class AdminMap extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    private GoogleMap mMap;

    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    //play services location
    private static final int MY_PERMISSION_REQUEST_CODE = 2897;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1997;
    //Firestore db = FirestoreClient.getFirestore();


    //access to geofencing api
    private GeofencingClient mGeofencingClient;

    //Set up firestore connection
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document( "NotificationData/Voodoo" );

    //Strings for firestore data
    String TitleText;
    String BodyText;
    GeoPoint geoPoint;
    Double Lat;
    Double Long;

    private Geofence testGeofence;
    private static int UPDATE_INTERVAL = 3000; // 5 SECONDS
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 3000;

    private ArrayList<Geofence> mGeofenceList;
    DatabaseReference ref;
    GeoFire geoFire;
    Marker CurrentLocation;

    private PendingIntent mGeofencePendingIntent;
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

    @Override
    protected void onStart() {
        super.onStart();
        mDocRef.addSnapshotListener( new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    TitleText = documentSnapshot.getString( TITLE_KEY );
                    BodyText = documentSnapshot.getString( BODY_KEY );
                    geoPoint = documentSnapshot.getGeoPoint( LAT_LNG );
                    Lat = geoPoint.getLongitude();
                    Long = geoPoint.getLongitude();
                    LatLng geofence;

                    //create a geofence
                    geofence = new LatLng( Lat, Long );
                    mMap.addCircle( new CircleOptions()
                            .center( geofence )
                            .radius( 200 )
                            .strokeColor( Color.BLUE )
                            .fillColor( 0x220000FF )
                            .strokeWidth( 5.0f )
                    );

                    //move camera to show geofence
                    mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng( Lat,Long), 15f));

                    //add GeoQuery
                    //0.5f = 0.5km

                    // getInformation();
                    GeoQuery geoQuery = geoFire.queryAtLocation( new GeoLocation( geofence.latitude, geofence.longitude ), 0.2f );

                    geoQuery.addGeoQueryEventListener( new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            //sendNotification( "" + TitleText, String.format( "" + BodyText, key ) );
                        }

                        @Override
                        public void onKeyExited(String key) {
                            //sendNotification( "MAPS", String.format( "%s Left Zone", key ) );

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {

                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            Log.e( "ERROR", "" + error );
                        }
                    } );


                } else if (e != null) {
                    Log.w( "ERROR", "Got an exception", e );
                }
            }

        } );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin_map );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );


        //firebase data store
        ref = FirebaseDatabase.getInstance().getReference( "Location" );
        geoFire = new GeoFire( ref );
        setUpLocation();

        mGeofencingClient = LocationServices.getGeofencingClient( this );

    }


    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            //Request runtime permission
            ActivityCompat.requestPermissions( this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE );
        } else {
            if (checkPlayServices()) {

            }
        }
    }

   /* private void displayLocation() {
        if(ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED &&
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
                            mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng( latitude,longitude), 8f));

                        }
                    } );

        }
        else
            Log.d("MAPS", "CAN NOT GET LOCATION");
    }*/

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable( this );
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError( resultCode ))
                GooglePlayServicesUtil.getErrorDialog( resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST ).show();
            else {
                Toast.makeText( this, "Device is not supported", Toast.LENGTH_SHORT ).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


    }
}

