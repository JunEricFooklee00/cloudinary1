package com.test.cloudinary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQ = 1;
    private static final String TAG = "Upload ###";
    private Uri imagePath;


    private ImageView imageView;
    private Button button;
    private TextView filePath;

    Map config = new HashMap();

    //for request permission
    private boolean isReadPermissionGranted = false;
    private boolean isWritePermissionGranted = false;
    private boolean isLocationPermissionGranted = false;
    ActivityResultLauncher<String[]> mPermissionResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        filePath = findViewById(R.id.filepath);
        initCongif();

        /*
         *1. user clicks the image view
         */
        Picasso.get().load("https://res.cloudinary.com/dpsarrelo/image/upload/v1676784744/Avatar/rux1i2aqi9ohgz6l9qkq.jpg").resize(50,50).centerCrop().into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });


        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {

                if (result.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) != null) {
                    isReadPermissionGranted = result.get(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (result.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                    isWritePermissionGranted = result.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (result.get(android.Manifest.permission.ACCESS_FINE_LOCATION) != null) {
                    isLocationPermissionGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                }

            }

        });

        button.setOnClickListener(v -> {

            Log.d(TAG, ": " + " button clicked");

            MediaManager.get().upload(imagePath).callback(new UploadCallback() {
                @Override
                public void onStart(String requestId) {
                    Log.d(TAG, "onStart: " + "started");
                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                    Log.d(TAG, "onStart: " + "uploading");
                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    Log.d(TAG, "onStart: " + "success");
                    filePath.setText(resultData.get("secure_url").toString());
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    Log.d(TAG, "onStart: " + error);
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {
                    Log.d(TAG, "onStart: " + error);
                }
            }).dispatch();
        });
        requestPermission();
    }// end of onCreate

    private void initCongif() {

        config.put("cloud_name", "dsuuylodx");
        config.put("api_key", "643975753735143");
        config.put("api_secret", "8lmznwULEA4x8DwBk5LHU-Hg0xw");
//        config.put("secure", true);
        MediaManager.init(this, config);
    }

    private void getImage() {
        if (isReadPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            selectImage();
        } else {
            requestPermission();
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        imagePath = data.getData();
//                        Picasso.get().load(imagePath).fit().centerCrop().into(imageView);

                    }
                }
            });

    private void requestPermission() {

        Log.v("Result", "Requesting Permission");

        boolean minSDK = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

        isReadPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        isLocationPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        isWritePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        isWritePermissionGranted = isWritePermissionGranted || minSDK;

        List<String> permissionRequest = new ArrayList<String>();

        if (!isReadPermissionGranted) {

            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        }
        if (!isWritePermissionGranted) {

            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!isLocationPermissionGranted) {

            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissionRequest.isEmpty()) {

            mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }

    }
}