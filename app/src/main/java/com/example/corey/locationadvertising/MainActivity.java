package com.example.corey.locationadvertising;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.auth.ui.*;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;




public class MainActivity extends AppCompatActivity {

    public void LogIn(View view){
        Toast.makeText(MainActivity.this, "LOG IN", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
        startActivity(intent);

    }
    // TODO: 31/01/2018 : add amazon cognito log-in

    public void signUp(View view){
        Toast.makeText(MainActivity.this, "SIGN UP", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(final AWSStartupResult awsStartupResult) {
                AuthUIConfiguration config =
                        new AuthUIConfiguration.Builder()
                                .userPools(true)  // true? show the Email and Password UI

                                .backgroundColor( Color.BLUE) // Change the backgroundColor
                                .isBackgroundColorFullScreen(true) // Full screen backgroundColor the backgroundColor full screenff
                                .fontFamily("sans-serif-light") // Apply sans-serif-light as the global font
                                .canCancel(true)
                                .build();
                SignInUI signinUI = (SignInUI) AWSMobileClient.getInstance().getClient(MainActivity.this, SignInUI.class);
                signinUI.login(MainActivity.this, HomeScreen.class).authUIConfiguration(config).execute();
            }
        }).execute();
        */



        //Cognito default config
      //  ClientConfiguration clientConfiguration = new ClientConfiguration( );





        // userPool.signUpInBackground(  );

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "eu-west-1_DUJQNMfua", //id identity pool
                Regions.EU_WEST_1 //Region
        );
    }
}

