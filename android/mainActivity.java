package com.example.sun.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class mainActivity extends AppCompatActivity {
    SocketChannel clientSocket;
    String ip;
    int port;
    Thread thread;
    Handler mHandler;

    TextView txtClient;
    TextView txtTemp;
    TextView txtHumidity;
    TextView txting;

    ImageView imgLED;
    ImageView imgFAN;
    ImageView imgMOTER;
    ImageView imgWATER;
    ImageView imgSECURITY;
    ImageView imgTemp;
    ImageView imgHumidity;

    Switch switch1;
    Switch switch2;
    Switch switch3;

    Switch switchLED;
    Switch switchFAN;
    Switch switchMOTER;
    Switch switchWATER;

    EditText edtTEMP;
    EditText edtHUMIDITY;

    LinearLayout l1;
    LinearLayout l2;
    LinearLayout l3;

    Spinner spinner;

    Button btnexe;

    plantDBHelper mHelper;

    String autotemp;
    String autohumidity;

    SQLiteDatabase db;

    int checkedposition;
    boolean securityMode;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mian);

        ip = "192.168.0.27";
        port = 1104;
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        txtClient = findViewById(R.id.txtClient);
        txtClient.setText(ip + " 접속중");
        txtTemp = findViewById(R.id.txt_tem);
        txtHumidity = findViewById(R.id.txt_humidity);
        txting = findViewById(R.id.txt_ing);
        txting.setText(pref.getString("txting","모드 OFF"));

        spinner = findViewById(R.id.spinner);

        l1 = findViewById(R.id.now_layout);
        l2 = findViewById(R.id.edt_layout);
        l3 = findViewById(R.id.img_layout);

        imgLED = findViewById(R.id.imgLED);
        imgFAN = findViewById(R.id.imgFAN);
        imgMOTER = findViewById(R.id.imgMOTER);
        imgWATER = findViewById(R.id.imgWATER);
        imgSECURITY = findViewById(R.id.imgSecurity);
        imgTemp = findViewById(R.id.img_Temp);
        imgHumidity = findViewById(R.id.img_Humidity);
        securityMode = pref.getBoolean("securityMode",false);

        imgLED.setImageResource(R.drawable.bulb);
        imgFAN.setImageResource(R.drawable.fan);
        imgMOTER.setImageResource(R.drawable.motor);
        imgWATER.setImageResource(R.drawable.water);
        imgTemp.setImageResource(R.drawable.temp);
        imgHumidity.setImageResource(R.drawable.humidity);


        if(securityMode==false) {
            imgSECURITY.setImageResource(R.drawable.sirenoff);
        }else{
            imgSECURITY.setImageResource(R.drawable.sirenon);
        }

        switch1 = findViewById(R.id.switch1);
        switch1.setChecked(pref.getBoolean("automodeON1",false));
        switch2 = findViewById(R.id.switch2);
        switch2.setChecked(pref.getBoolean("automodeON2",false));
        switch3 = findViewById(R.id.switch3);
        switch3.setChecked(pref.getBoolean("noautomodeON",false));

        if (switch1.isChecked()) {
            l1.setVisibility(View.VISIBLE);
        } else if (switch2.isChecked()) {
            l2.setVisibility(View.VISIBLE);
        } else if (switch3.isChecked()) {
            l3.setVisibility(View.VISIBLE);
        }

        edtTEMP = findViewById(R.id.edt_tem);
        edtTEMP.setText(pref.getString("edtTemp","0"));
        edtHUMIDITY = findViewById(R.id.edt_water);
        edtHUMIDITY.setText(pref.getString("edtHumidity","0"));

        switchLED = findViewById(R.id.switchLED);
        switchFAN = findViewById(R.id.switchFAN);
        switchMOTER = findViewById(R.id.switchMOTER);
        switchWATER = findViewById(R.id.switchWATER);

        btnexe = findViewById(R.id.btn_exe);



        mHelper = new plantDBHelper(this);
        db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM plant", null);
        ArrayList<String> arrayList = new ArrayList<>();
        while(cursor.moveToNext()){
            arrayList.add(cursor.getString(0));
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        spinner.setAdapter(spinnerAdapter);//스피너 값 생성
        spinner.setSelection(pref.getInt("position",0));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor1 = db.rawQuery("SELECT tem, humidity FROM plant WHERE _id=" + (position+1), null);
                while(cursor1.moveToNext()){
                    autotemp = cursor1.getString(0);
                    autohumidity = cursor1.getString(1);
                }
                checkedposition = position;
            }

            @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
        });

        imgSECURITY.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(!securityMode) {
                    imgSECURITY.setImageResource(R.drawable.sirenon);
                    String Token = FirebaseInstanceId.getInstance().getToken();
                    send("securitymodeON"+" "+Token);
                    securityMode = true;
                    editor.putBoolean("securityMode", true);
                }else{
                    imgSECURITY.setImageResource(R.drawable.sirenoff);
                    String Token = FirebaseInstanceId.getInstance().getToken();
                    send("securitymodeOFF"+" "+Token);
                    securityMode = false;
                    editor.putBoolean("securityMode", false);
                }
                editor.commit();
            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked==true) {
                        switch2.setChecked(false);
                        switch3.setChecked(false);
                        l1.setVisibility(View.VISIBLE);
                    }else{
                        l1.setVisibility(View.INVISIBLE);
                    }
                }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true) {
                    switch1.setChecked(false);
                    switch3.setChecked(false);
                    l2.setVisibility(View.VISIBLE);
                }else{
                    l2.setVisibility(View.INVISIBLE);
                }
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true) {
                    switch1.setChecked(false);
                    switch2.setChecked(false);
                    l3.setVisibility(View.VISIBLE);
                }else{
                    l3.setVisibility(View.INVISIBLE);
                }
            }
        });

        switchLED.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(txting.getText().toString().equals("수동 모드 실행중")){
                    if(isChecked) send("ledON");
                    else send("ledOFF");
                }

            }
        });

        switchFAN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(txting.getText().toString().equals("수동 모드 실행중")) {
                    if (isChecked) send("fanON");
                    else send("fanOFF");
                }
            }
        });

        switchMOTER.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(txting.getText().toString().equals("수동 모드 실행중")) {
                    if (isChecked) send("motorON");
                    else send("motorOFF");
                }
            }
        });

        switchWATER.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(txting.getText().toString().equals("수동 모드 실행중")) {
                    if (isChecked) send("waterON");
                    else send("waterOFF");
                }
            }
        });


        btnexe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch1.isChecked()){
                    txting.setText("최적화 모드 실행중");
                    send("automodeON"+" "+autotemp+" "+autohumidity);
                    editor.putBoolean("automodeON1", true);
                    editor.putBoolean("automodeON2", false);
                    editor.putBoolean("noautomodeON", false);
                    editor.putInt("position",checkedposition);
                    editor.putString("txting", txting.getText().toString());
                }else if(switch2.isChecked()){
                    txting.setText("사용자 설정 모드 실행중");
                    send("automodeON"+" "+edtTEMP.getText().toString()+" "+edtHUMIDITY.getText().toString());
                    editor.putBoolean("automodeON1", false);
                    editor.putBoolean("automodeON2", true);
                    editor.putBoolean("noautomodeON", false);
                    editor.putString("edtTemp", edtTEMP.getText().toString());
                    editor.putString("edtHumidity", edtHUMIDITY.getText().toString());
                    editor.putString("txting", txting.getText().toString());
                }else if(switch3.isChecked()){
                    send("allOFF");

                    txting.setText("수동 모드 실행중");
                    editor.putBoolean("automodeON1", false);
                    editor.putBoolean("automodeON2", false);
                    editor.putBoolean("noautomodeON", true);
                    editor.putString("txting",txting.getText().toString());
                }else{
                    send("allOFF");
                    txting.setText("모드 OFF");

                    editor.putBoolean("automodeON1", false);
                    editor.putBoolean("automodeON2", false);
                    editor.putBoolean("noautomodeON", false);
                    editor.putString("txting",txting.getText().toString());
                }
                editor.commit();
            }
        });

        mHandler = new Handler(){
            public void handleMessage(Message msg) {}
        };

        connect();

    }

    @Override
    protected void onPause() {
        send("clientOFF");
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void connect() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    clientSocket = SocketChannel.open();
                    clientSocket.configureBlocking(true);
                    clientSocket.connect(new InetSocketAddress(ip, port));
                    receive();
                } catch (Exception e) {
                    try {
                        clientSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), connectActivity.class);
                            intent.putExtra("return", "잘못된 ip와 port를 입력하였습니다.");
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        };
        thread.start();
    }

    public void send(final String data){
        Thread thread1 = new Thread(){
            @Override
            public void run(){
                try{
                    Charset charset = Charset.forName("UTF-8");
                    ByteBuffer byteBuffer = charset.encode(data);
                    clientSocket.write(byteBuffer);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread1.start();
    }

    public void receive() {
        while (true) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                clientSocket.read(byteBuffer);

                byteBuffer.flip();
                Charset charset = Charset.forName("UTF-8");
                final String data = charset.decode(byteBuffer).toString();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        txtTemp.setText(data.split(" ")[0]+"℃");
                        txtHumidity.setText(data.split(" ")[1]+"%");
                    }
                });


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
