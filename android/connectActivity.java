package com.example.sun.myapplication;

import android.content.Intent;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class connectActivity extends AppCompatActivity {
    EditText edt_ip;
    EditText edt_port;
    Button btn_connect;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        imageView = findViewById(R.id.img_farm);
        imageView.setImageResource(R.drawable.farmer);

        Intent intent = getIntent();
        String aa = intent.getStringExtra("return");
        if(aa != null)Toast.makeText(getApplicationContext(),aa,Toast.LENGTH_SHORT).show();

        edt_ip = findViewById(R.id.edt_ip);
        edt_port = findViewById(R.id.edt_port);
        btn_connect = findViewById(R.id.btn_connect);
    }

    public void mOnClick(View view){
        switch (view.getId()) {
            case R.id.btn_connect:
                connect();
                break;
        }
    }

    public void connect() {
        Intent intent = new Intent(getApplicationContext(), mainActivity.class);
        intent.putExtra("ip", edt_ip.getText().toString());
        intent.putExtra("port", Integer.parseInt(edt_port.getText().toString()));
        startActivity(intent);
        finish();
    }
}