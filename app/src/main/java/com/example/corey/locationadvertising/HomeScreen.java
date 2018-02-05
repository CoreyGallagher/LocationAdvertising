package com.example.corey.locationadvertising;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HomeScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home_screen );


    }

    // method to open map activity
    public void openMap(View view) {
        Toast.makeText( HomeScreen.this, "Map", Toast.LENGTH_SHORT ).show();
        Intent intent = new Intent( HomeScreen.this, MapsActivity.class );
        startActivity( intent );
    }
}
