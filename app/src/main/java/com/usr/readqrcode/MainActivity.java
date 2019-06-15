package com.usr.readqrcode;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView driver, boxedID, link;
    private IntentIntegrator scanIntegrator;
    private String result; //存讀取RQCode的結果
    private String driverID, boxedLunchID, time, web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        driver = findViewById(R.id.driver);
        boxedID = findViewById(R.id.boxed);
        link = findViewById(R.id.link);
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
                    Log.e("result", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        driverID = jsonObject.getString("driver");
                        Log.e("driver", driverID);
                        boxedLunchID = jsonObject.getString("boxedLunchID");
                        time = jsonObject.getString("time");
                        web = jsonObject.getString("web");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    driver.setText("Arduino ID : " + driverID);
                    boxedID.setText("便當ID : " + boxedLunchID);
                    link.setMovementMethod(LinkMovementMethod.getInstance());
                    new AlertDialog.Builder(MainActivity.this)
                            .setCancelable(false)
                            .setTitle("確認前往此連結?")
                            .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri = Uri.parse(web);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    try {
                                        startActivity(intent);
                                    }catch (Exception e){
                                        Log.e("receiveDetections", "ERROR: "+e.toString());
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
            Toast.makeText(this, "讀取發生錯誤!", Toast.LENGTH_SHORT);
//            textView.setText("讀取發生錯誤!");
        }
    }

    public void doLink(View view) {
        Uri uri = Uri.parse(web);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(intent);
        }catch (Exception e){
            Log.e("receiveDetections", "ERROR: "+e.toString());
        }
    }
}
