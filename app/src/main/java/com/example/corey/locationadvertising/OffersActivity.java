package com.example.corey.locationadvertising;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class OffersActivity extends AppCompatActivity {

    DynamoDBMapper dynamoDBMapper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_offers );
        AWSMobileClient.getInstance().initialize(this).execute();

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration( AWSMobileClient.getInstance().getConfiguration())
                .build();
    }

    public void readRecord() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                LocationsDO newsItem = dynamoDBMapper.load(
                        LocationsDO.class,
                        "5",
                        "144"

                        );
              //  String result = newsItem.getCategory()

                // Item read
                 Log.d("News Item:", newsItem.toString());
            }
        }).start();
    }

    


   /* public void createNews() {
        final LocationsDO Shop = new LocationsDO();

        Shop.setUserId("5");

        Shop.setItemId("144");
        Shop.setCategory("Cafe");
        Shop.setLatitude(89.00);
        Shop.setLongitude(98.00);



        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(Shop);
                // Item saved
            }
        }).start();
    }*/

    public void readDatabase(View view){
        Toast.makeText(OffersActivity.this, "Database", Toast.LENGTH_SHORT).show();
        readRecord();
        //createNews();


    }
}
