package com.example.trancuong.testliveapi2;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private Button btnLive, btnGetInfo, btnEndLive, btnDelete;
    String name, firstName, email;
    private AccessToken accessToken = AccessToken.getCurrentAccessToken();
    JSONObject jsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setKeyHash();

        btnLive = findViewById(R.id.btnLive);
        btnGetInfo = findViewById(R.id.btnGetLive);
        btnEndLive = findViewById(R.id.btnEndLive);
        btnDelete = findViewById(R.id.btnDelete);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithPublishPermissions(
                LoginActivity.this,
                Arrays.asList("publish_video"));

        final JSONObject object2 = new JSONObject();
        try {
            object2.put("fields", "ingest_streams");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject object3 = new JSONObject();
        try {
            object3.put("end_live_video", "true");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        btnLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphRequest request = GraphRequest.newPostRequest(
                        accessToken,
                        "/me/live_videos",
                        null,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                Log.i("Trave", response.getJSONObject() + "");
                            }
                        });
                request.executeAsync();
            }
        });

        btnGetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphRequest request = GraphRequest.newGraphPathRequest(
                        accessToken,
                        "/846681245672426",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                Log.i("Trave", response.getJSONObject() + "");
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "ingest_streams");
                request.setParameters(parameters);
                request.executeAsync();
            }
        });

        btnEndLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphRequest request = GraphRequest.newPostRequest(
                        accessToken,
                        "/847238035616747",
                        object3,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                Log.i("Trave", response.getJSONObject() + "");
                            }
                        });
                request.executeAsync();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle parameters = new Bundle();

                GraphRequest request = new GraphRequest(
                        accessToken,
                        "/847238035616747",
                        parameters,
                        HttpMethod.DELETE,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                Log.i("Trave", response.getJSONObject() + "");
                            }
                        });

                request.executeAsync();
            }
        });

    }

    private void setKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.trancuong.testapifacebook",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


}
