package com.example.corey.locationadvertising;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    public static double Adminlatitude =0;
    public static double Adminlongitude =0;
    public static String Title;
    public static String Body;

    public static final String BODY_KEY = "Body";
    public static final String TITLE_KEY= "Title";
    public static final String LAT_LNG= "LAT_LNG";
    public static final String BUSINESS_KEY ="Business";

    private CollectionReference mColRef = FirebaseFirestore.getInstance().collection( "NotificationData" );

    Button Create;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin );


        Create = (Button)findViewById( R.id.geofenceBtn );
        Create.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openAdminMap();
            }
        } );
    }

    private void showToast(String text) {
        Toast.makeText( AdminActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    public void openAdminMap() {

        Toast.makeText( AdminActivity.this, "Admin Map",Toast.LENGTH_SHORT ).show();
        Intent intent = new Intent( AdminActivity.this, AdminMap.class );
        startActivity( intent );


        EditText TitleView = (EditText) findViewById( R.id.TitleText );
        EditText BodyView = (EditText) findViewById( R.id.BodyText );
        EditText BusinessName = (EditText) findViewById( R.id.BusinessName );
        String BusinessText = BusinessName.getText().toString();
        String TitleText = TitleView.getText().toString();
        String BodyText = BodyView.getText().toString();

        EditText LatView = (EditText) findViewById( R.id.latitudeText );
        EditText LngView = (EditText) findViewById( R.id.longitudeText );
        Double LatText = Double.valueOf( LatView.getText().toString() );
        Double LngText = Double.valueOf( LngView.getText().toString() );

        GeoPoint geoPoint = new GeoPoint( LatText,LngText );


        if (TitleText.isEmpty() || BodyText.isEmpty() || LatText == null || LngText == null) {
            return;
        }


        Map<String, Object> data1 = new HashMap<>();
        data1.put(TITLE_KEY, BusinessText);
        data1.put(BODY_KEY, BodyText);
        data1.put(LAT_LNG, geoPoint);
        data1.put(BUSINESS_KEY, BusinessText);
        mColRef.document(BusinessText).set(data1);
        Log.d("SUCCESS", "DATA WAS LOGGED SUCCESSFULLY");


    }

}
