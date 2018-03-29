package com.borealos.chathead;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import cz.msebera.android.httpclient.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 123;
    private final String URL = "https://us-central1-abi-chat-test.cloudfunctions.net/authenticate?token=";
    private final String KEY = "SfahFQ_veN__HGvkbOMIqCyRsWllUISYuytiPbRbeNy8hlA4TCUmTVkxTibZTpn7hAblSdTXpKd_z" +
            "o2VMu4KjJMzNwRjxX2w6CG0DPD9BryhgBMBljge0ZAGoCIeKiQXPK-tzkqk2ERp7w5Wsk2ZjDJml1_HVczD" +
            "Vf305gpO3uSl1I02gBxhzeABWVczd4RE3dnJCW5bCFThTrituEX5szss--9ohTwx7VLrJVY3Nx1SNTQkwRt" +
            "t8G13ByeWDtcrhFWi_dENkyYz7zIJvIRjb9t3rTBBv1AuZbMEjSN1-nbdZBLZsgkSIAbNSU5OxX2OGA0PZ" +
            "sWO-eii8v5X2tQcmQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, PERMISSION_REQUEST);

        } else {
            createView();
            getData();
        }

    }

    private void createView() {

        Button mBtnService = findViewById(R.id.buttonService);
        mBtnService.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, ChatService.class));
                finish();
            }
        });

    }

    private void getData() {

        Button mBtnGetData = findViewById(R.id.buttonAPI);
        mBtnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*String jwt = generateJWT();
                String url = URL + jwt;
                Log.d("ChatHead", url);
                apiCall(url);*/
                apiCall(URL + generateJWT());
                Log.d("ChatHead", URL + generateJWT());
            }
        });

    }

    private String generateJWT() {


        byte[] data = new byte[0];
        try {
            data = KEY.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String key64 = Base64.encodeToString(data, Base64.DEFAULT);

        String jwt = Jwts.builder()
                .claim("gender", "Male")
                .claim("firstName", "Pepe")
                .claim("lastName", "Perez")
                .claim("partnerName", "molexplore")
                .claim("partnerUserId", "primaryKey")
                .claim("phone", "+34111222333")
                .claim("dateOfBirth", "1970-01-01")
                .claim("origin", "ES")
                .claim("language", "es")
                .claim("location", "ES")
                .claim("physicianCountry", "es")
                .setIssuer("molexplore")
                .signWith(SignatureAlgorithm.HS256, key64)
                .compact();


        return jwt;

    }

    private void apiCall(String url) {

        AsyncHttpClient client = new AsyncHttpClient(true,80,443);
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {


                try {
                    Log.d("ChatHead", response.getString("token"));
                    Toast.makeText(getApplicationContext(), response.getString("token"), Toast.LENGTH_SHORT).show();

                    String decodedJWT = JWTDecode.decodeJWT(response.getString("token"));
                    Toast.makeText(getApplicationContext(), decodedJWT, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("ChatHead", statusCode + ": API Call Failed");
                Toast.makeText(getApplicationContext(), "API Call Failed", Toast.LENGTH_SHORT).show();
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PERMISSION_REQUEST && resultCode == RESULT_OK) {
            createView();
        }
        else {
            Toast.makeText(this, "Draw over other app permission not enabled", Toast.LENGTH_SHORT).show();
        }
    }

}
