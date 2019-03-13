package com.example.trancuong.testuserpage;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CallbackManager callbackManager;
    private AccessToken accessToken = AccessToken.getCurrentAccessToken();
    private Button btnGetPost,btnPostStatus, btnShareLink, btnShareImage, btnShareVideo, btnPickVideo;
    private ImageView imgShare;
    private VideoView videoView;
    private LoginButton loginButton;
    private ShareDialog shareDialog;
    private static final int SELECT_PICTURE = 1;
    private static final int SELECT_VIDEO = 2;
    Bitmap bitmap;
    Uri selectVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setKeyHash();

        btnGetPost = findViewById(R.id.btnGetPost);
        loginButton = findViewById(R.id.login_button);
        btnPostStatus = findViewById(R.id.btnPostStatus);
        btnShareLink = findViewById(R.id.btnShareLink);
        btnShareImage = findViewById(R.id.btnShareImage);
        btnShareVideo = findViewById(R.id.btnShareVideo);
        imgShare = findViewById(R.id.imgShare);
        videoView = findViewById(R.id.vidView);
        btnPickVideo = findViewById(R.id.btnPickVideo);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(MainActivity.this);

        loginButton.setReadPermissions("user_posts","user_photos","user_status","user_videos");

        btnGetPost.setOnClickListener(this);
        btnPostStatus.setOnClickListener(this);
        btnShareLink.setOnClickListener(this);
        btnShareImage.setOnClickListener(this);
        btnShareVideo.setOnClickListener(this);
        imgShare.setOnClickListener(this);
        btnPickVideo.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetPost:
                getPost();
                break;
            case R.id.btnPostStatus:
                postStatus();
                break;
            case R.id.btnShareLink:
                shareLink();
                break;
            case R.id.btnShareImage:
                shareImage();
                break;
            case R.id.btnShareVideo:
                shareVideo();
                break;
            case R.id.imgShare:
                getImage();
                break;
            case R.id.btnPickVideo:
                getVideo();
                break;
        }

    }

    private void getPost() {
        GraphRequest request = new GraphRequest(
                accessToken,
                "/me/posts",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Log.i("Trave", response.getJSONObject() + "");
                    }
                }
        );
        request.executeAsync();
    }

    private void postStatus() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder().build();
            ShareDialog.show(MainActivity.this,shareLinkContent);
        }
    }

    private void shareLink() {
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setQuote("My Status")
                .setContentUrl(Uri.parse("https://www.youtube.com/watch?v=YSqtv-3ijk8"))
                .build();
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(linkContent);
        }
    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,SELECT_PICTURE);
    }

    private void shareImage() {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            shareDialog.show(content);
        }
    }

    private void getVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent,SELECT_VIDEO);
    }

    private void shareVideo() {
        ShareVideo shareVideo = null;
        shareVideo = new ShareVideo.Builder()
                .setLocalUrl(selectVideo)
                .build();
        ShareVideoContent content = new ShareVideoContent.Builder()
                .setVideo(shareVideo)
                .build();
        shareDialog.show(content);
        videoView.stopPlayback();
    }


    private void setKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.trancuong.testuserpage",
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                imgShare.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == SELECT_VIDEO && resultCode == RESULT_OK) {
            selectVideo = data.getData();
            videoView.setVideoURI(selectVideo);
            videoView.start();
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }
}
