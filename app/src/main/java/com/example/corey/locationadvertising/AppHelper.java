package com.example.corey.locationadvertising;

/**
 * Created by corey on 01/02/2018.
 */
import android.*;
import android.content.Context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.*;
import com.amazonaws.regions.Regions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppHelper {

    private static List<String> attributeDisplaySeq;
    private static Map<String, String> signUpFieldsC2O;
    private static Map<String, String> signUpFieldsO2C;

    private static AppHelper appHelper;
    private static CognitoUserPool userPool;
    private static String user;
    private static CognitoDevice newDevice;

    //User Pool ID
    private static final String poolId = "eu-west-1_DUJQNMfua";

    // Client ID Info
    private static final String clientId = "372fskua3pgu2qng42fimg1ibm";

    // Client App Secret
    private static final String clientSecret = "53af4k9435nljh3mb1fi0qv4efld5e51chf283a86jib7dsiknk";

    // Region
    private static final Regions cognitoRegion = Regions.EU_WEST_1;


    public static void init(Context context) {
        setData();

        if (appHelper != null && userPool != null) {
            return;
        }

        if (appHelper == null) {
            appHelper = new AppHelper();
        }

        if (userPool == null) {

            // Create a user pool with default ClientConfiguration
            userPool = new CognitoUserPool( context, poolId, clientId, clientSecret, cognitoRegion );


        }
    }
    private static void setData() {
        // Set attribute display sequence
        attributeDisplaySeq = new ArrayList<String>();
        attributeDisplaySeq.add("email");

        signUpFieldsC2O = new HashMap<String, String>();
        signUpFieldsC2O.put("Email","email");


        signUpFieldsO2C = new HashMap<String, String>();
        signUpFieldsO2C.put("email", "Email");


    }
}
