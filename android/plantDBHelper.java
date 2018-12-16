package com.example.sun.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class plantDBHelper extends SQLiteOpenHelper {

    public plantDBHelper(Context context){
        super(context, "Plant.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE plant (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, tem TEXT, humidity TEXT);");
        db.execSQL("INSERT INTO plant VALUES (null, '땅콩', '20', '40');");
        db.execSQL("INSERT INTO plant VALUES (null, '무순', '25', '40');");
        db.execSQL("INSERT INTO plant VALUES (null, '딸기', '15', '30');");
        db.execSQL("INSERT INTO plant VALUES (null, '무', '20', '35');");
        db.execSQL("INSERT INTO plant VALUES (null, '당근', '18', '35');");
        db.execSQL("INSERT INTO plant VALUES (null, '상추', '20', '30');");
        db.execSQL("INSERT INTO plant VALUES (null, '배추', '10', '35');");
        db.execSQL("INSERT INTO plant VALUES (null, '오이', '25', '35');");
        db.execSQL("INSERT INTO plant VALUES (null, '고구마', '20', '25');");
        db.execSQL("INSERT INTO plant VALUES (null, '토마토', '15', '30');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS plant");
        onCreate(db);
    }
}
