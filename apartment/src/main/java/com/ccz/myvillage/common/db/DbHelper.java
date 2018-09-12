package com.ccz.myvillage.common.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ccz.myvillage.dto.WordHistory;
import com.ccz.myvillage.repository.db.Databases;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {
    private final String DATABASE_NAME = "apartment.db";
    private final int DATABASE_VERSION = 1;
    private SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;
    private static DbHelper spThis;

    public static DbHelper getInst() {
        if(spThis==null)
            spThis = new DbHelper();
        return spThis;
    }

    public static void freeInst() {
        spThis = null;
    }

    public DbHelper(){
    }

    public void init(Context context) throws SQLException {
        this.mCtx = context;
        this.open();
    }

    public DbHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        mDB.close();
    }

    public void insertSearchWord(String word, String user) {
        StringBuffer sb = new StringBuffer();
        sb.append(" INSERT INTO ").append(DATABASE_NAME).append("(search, user) VALUES (?, ?)");
        mDB.execSQL(sb.toString(), new Object[]{ word, user});;
    }

    public List getSearchWord() {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT search, user, createdAt FROM ").append(DATABASE_NAME).append(" LIMIT 0, 50"); // 읽기 전용 DB 객체를 만든다.

        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sb.toString(), null);
        List wordHistoryList = new ArrayList();
        WordHistory word = null; // moveToNext 다음에 데이터가 있으면 true 없으면 false
        while( cursor.moveToNext() ) {
            word = new WordHistory();
            word.searh = cursor.getString(0);
            word.user = cursor.getString(1);
            word.date = cursor.getString(1);
            wordHistoryList.add(word);
        }
        return wordHistoryList;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        // 생성자
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // 최초 DB를 만들때 한번만 호출된다.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Databases.BoardDB.CREATE_QUERY);

        }

        // 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //[TODO] migration first and then belows
            //db.execSQL("DROP TABLE IF EXISTS "+Databases.BoardDB.CREATE_QUERY);
            //onCreate(db);
        }
    }

}
