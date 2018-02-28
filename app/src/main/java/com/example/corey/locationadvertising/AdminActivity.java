package com.example.corey.locationadvertising;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class AdminActivity extends AppCompatActivity {

    public static double Adminlatitude =0;
    public static double Adminlongitude =0;
    EditText latitudeText;
    EditText longitudeText;

    Button Create;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin );

        latitudeText = (EditText)findViewById( R.id.latitudeText );
        longitudeText = (EditText)findViewById( R.id.longitudeText );

        Create = (Button)findViewById( R.id.geofenceBtn );
        Create.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Adminlatitude = Double.valueOf( latitudeText.getText().toString());
                Adminlongitude = Double.valueOf( longitudeText.getText().toString());

                showToast( String.valueOf( Adminlatitude ) );
                showToast( String.valueOf( Adminlongitude ) );

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

    }

}
