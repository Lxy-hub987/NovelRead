package com.example.novelreading;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.snackbar.Snackbar;
import com.example.novelreading.databinding.ActivityMainBinding;

public class MainActivity extends ActivityGroup{

    TabHost tabHost;
    TabHost.TabSpec tabSpec1,tabSpec2,tabSpec3,tabSpec4;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE = 0;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            System.out.println("版本正确");
            Log.i("版本", "版本正确");
            verifyStoragePermissions();
//            requestCameraPermission();
        }else {
            System.out.println("版本过低");
            Log.i("版本", "版本过低");
        }
//        setCustomActionBar();
//        startActivity(new Intent(MainActivity.this,Home.class));
        tabHost=(TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.setup(this.getLocalActivityManager());

        tabSpec1=tabHost.newTabSpec("bookshelf");
        tabSpec1.setContent(R.id.tab1);

        tabSpec1.setIndicator("书架",null);
        Intent intent1=new Intent(this,BookShelf.class);
        tabSpec1.setContent(intent1);

        tabSpec2=tabHost.newTabSpec("Spac 1");
        tabSpec2.setContent(R.id.tab2);

        tabSpec2.setIndicator("推荐",null);
        Intent intent2=new Intent(this,Spac1.class);
        tabSpec2.setContent(intent2);

        tabSpec3=tabHost.newTabSpec("catagory");
        tabSpec3.setContent(R.id.tab3);

        tabSpec3.setIndicator("分类",null);
        Intent intent3=new Intent(this,Catagory.class);
        tabSpec3.setContent(intent3);

        tabHost.addTab(tabSpec1);
        tabHost.addTab(tabSpec2);
        tabHost.addTab(tabSpec3);
    }

    private void verifyStoragePermissions() {
        // Check if we have write permission
        Log.i("权限", "权限检查");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED){
            Log.i("权限", "申请权限");
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        }
    }

//    private void showPreview() {
//        // BEGIN_INCLUDE(startCamera)
//        // Check if the Camera permission has been granted
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED) {
//            // Permission is already available, start camera preview
//        } else {
//            // Permission is missing and must be requested.
//            requestCameraPermission();
//        }
//        // END_INCLUDE(startCamera)
//    }
//
//    private void requestCameraPermission() {
//        // Permission has not been granted and must be requested.
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            // Provide an additional rationale to the user if the permission was not granted
//            // and the user would benefit from additional context for the use of the permission.
//            // Display a SnackBar with cda button to request the missing permission.
//            Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
//
//        } else {
//            // Request the permission. The result will be received in onRequestPermissionResult().
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////        if (requestCode == REQUEST_CODE) {
////            // Request for camera permission.
////            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                // Permission has been granted. Start camera preview Activity.
////                Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
////            } else {
////                // Permission request was denied.
////                Toast.makeText(this, "授权被拒绝！", Toast.LENGTH_SHORT).show();
////            }
////        }
//    }
}