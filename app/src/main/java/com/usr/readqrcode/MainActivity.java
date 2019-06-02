package com.usr.readqrcode;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOError;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private IntentIntegrator scanIntegrator;
    private String result; //存讀取RQCode的結果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
    }

    public void DoScan(View view) {
        View scanBtn = (View) findViewById(R.id.scanBtn);

        scanIntegrator = new IntentIntegrator(MainActivity.this);
        scanIntegrator.setPrompt("請掃描");
        scanIntegrator.setTimeout(300000);
        scanIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                String scanContent = scanningResult.getContents();
                if (!scanContent.equals("")) {
                    result = scanContent;
                    textView.setText(result);
                    new AlertDialog.Builder(MainActivity.this)
                            .setCancelable(false)
                            .setTitle("確認前往此連結?")
                            .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri = Uri.parse(result);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    try {
                                        startActivity(intent);
                                    }catch (Exception e){
                                        textView.setText(result);
                                        Log.e("receiveDetections", "ERROR: "+e.toString());
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    textView.setText(result);

                                }
                            })
                            .show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
            textView.setText("讀取發生錯誤!");
        }
    }
}
