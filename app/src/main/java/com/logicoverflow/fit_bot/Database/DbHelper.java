package com.logicoverflow.fit_bot.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.logicoverflow.fit_bot.Model.ChatMessage;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    static SQLiteDatabase sqLiteDatabase;
    static DbHelper dbHelper;


    private DbHelper(Context context) {
        super(context, ChatMessage.DB_NAME, null, 2);
        sqLiteDatabase = getWritableDatabase();
    }

    public static DbHelper getInstance(Context context) {
        if (sqLiteDatabase == null) {
            dbHelper = new DbHelper(context);
        }
        return dbHelper;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ChatMessage.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ChatMessage.DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    public boolean insertMessage(ChatMessage message) {
        ContentValues contentValues = new ContentValues();

        Gson gson = new Gson();
        String json = gson.toJson(message);


        contentValues.put(ChatMessage.MESSAGE_COL, json);
        return sqLiteDatabase.insert(ChatMessage.TABLE_NAME, null, contentValues) > 0;
    }

    public boolean deleteMessage(String id){
        return sqLiteDatabase.delete(ChatMessage.TABLE_NAME,ChatMessage.MESSAGE_COL_ID+"= ?",new String[]{id})>0;
    }

    public ArrayList<ChatMessage> getAllMessages() {
        ArrayList<ChatMessage> messagesArrayList = new ArrayList<>();

        String query = "SELECT * FROM " + ChatMessage.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ChatMessage message;

                Gson gson = new Gson();

                message = gson.fromJson(cursor.getString(cursor.getColumnIndex(ChatMessage.MESSAGE_COL)), ChatMessage.class);

                messagesArrayList.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messagesArrayList;
    }

    public boolean saveLastNMessages(){
        String query = "SELECT "+ChatMessage.MESSAGE_COL_ID+" FROM " + ChatMessage.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        Log.e("rmy","cursor.getCount(): "+cursor.getCount());


        if(cursor.getCount()>50){
            cursor.moveToFirst();
            int firstID = cursor.getInt(cursor.getColumnIndex(ChatMessage.MESSAGE_COL_ID));

            Log.e("rmy","firstID: "+firstID);

            cursor.moveToLast();
            int lastID = cursor.getInt(cursor.getColumnIndex(ChatMessage.MESSAGE_COL_ID));
            Log.e("rmy","lastID: "+lastID);

            int numberOfMessages = lastID - firstID;
            Log.e("rmy","numberOfMessages: "+numberOfMessages);

            int deleteUntil = numberOfMessages - 50;
            Log.e("rmy","deleteUntil: "+deleteUntil);


            int deleteBelow = firstID+deleteUntil;
            Log.e("rmy","deleteBelow: "+deleteBelow);


            return sqLiteDatabase.delete(ChatMessage.TABLE_NAME,ChatMessage.MESSAGE_COL_ID+"< ?",new String[]{String.valueOf(deleteBelow)})>0;


        }else{
            return false;
        }
    }
}