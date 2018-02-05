package com.example.corey.locationadvertising;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;

public class SignUpActivity extends AppCompatActivity {

    private EditText Username;
    private EditText Password;
    private EditText Email;

    private String usernameInput;
    private String userPassword;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

    }


    public void signUp(View view) {
        Toast.makeText( SignUpActivity.this, "SIGN UP", Toast.LENGTH_SHORT ).show();

        // Create a CognitoUserAttributes object and add user attributes
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        usernameInput = Username.getText().toString();

        //Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        //startActivity(intent);

        SignUpHandler signupCallback = new SignUpHandler() {

            @Override
            public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                // Sign-up was successful

                // Check if this user (cognitoUser) needs to be confirmed
                if (!userConfirmed) {
                    // This user must be confirmed and a confirmation code was sent to the user
                    // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                    // Get the confirmation code from user
                } else {
                    // The user has already been confirmed
                }
            }

            @Override
            public void onFailure(Exception exception) {
                // Sign-up failed, check exception for the cause
            }
        };

    }
}



