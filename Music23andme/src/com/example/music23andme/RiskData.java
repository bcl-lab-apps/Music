package com.example.music23andme;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RiskData {
	public final String DB_NAME="risk.db";
	public final int DB_VERSION=1;
	public final String TABLE= "risk";
	public final String C_ID= "_id";
	public final String C_CREATED_AT="created_at";
	public final String C_SCORE= "risk_score";
	public final String C_DISEASE= "diseases";
	public final String C_AVERAGE= "average_risk";
			
	Context context;
	DbHelper dbHelper;
	SQLiteDatabase db;
	
	public RiskData(Context context){
		this.context=context;
		dbHelper= new DbHelper();
	}
	
	public void insert(String scores, String disease, String average){
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(C_CREATED_AT, new Date().getTime() );
		values.put(C_SCORE, scores);
		values.put(C_DISEASE, disease);
		values.put(C_AVERAGE, average);
		db.insert(TABLE, null, values);
		Log.d("DATAVASE SQL", "inserted values");
	}
	
	public String[] getFirstScores(){
		String[] risks=new String[2];
		db= dbHelper.getWritableDatabase();
		Cursor cursor= db.rawQuery("SELECT * FROM "+ TABLE + " ORDER BY "+ C_ID + " ASC", null);
		cursor.moveToFirst();
		risks[0]= cursor.getString(cursor.getColumnIndex(C_SCORE));
		risks[1]=cursor.getString(cursor.getColumnIndex(C_DISEASE));
		Log.d("ARRAY LENGTH", String.valueOf(risks.length));
		cursor.close();
		return risks;	
	}
	
	public String getFirstDiseases(){
		db= dbHelper.getWritableDatabase();
		Cursor cursor= db.rawQuery("SELECT * FROM "+ TABLE + " ORDER BY "+ C_ID + " ASC", null);
		cursor.moveToFirst();
		String result=cursor.getString(cursor.getColumnIndex(C_DISEASE));
		cursor.close();
		return result;
		
	}
	
	class DbHelper extends SQLiteOpenHelper{

		public DbHelper() {
			super(context, DB_NAME, null, DB_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql=String.format("create table %s " + 
					"(%s int primary key, %s int, %s text, %s text, %s text)", TABLE, C_ID, C_CREATED_AT, C_SCORE, C_DISEASE, C_AVERAGE);
			Log.d("SQL DATABASE", sql);
			db.execSQL(sql);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop if exists " + TABLE);
			onCreate(db);
		}
		
	}

}
