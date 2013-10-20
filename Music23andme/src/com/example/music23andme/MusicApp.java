package com.example.music23andme;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import android.app.Application;
import android.util.Log;
import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

public class MusicApp extends Application{
	
	static final String TAG= "GeneMusic";
	Hashtable<Integer, String> diseaseOrder;
	ArrayList<String> diseaselist;
	@Override
	public void onCreate() {
		Log.d("APPLICATION", "onCreate called");
		diseaselist=new ArrayList<String>();
		diseaseOrder= new Hashtable<Integer, String>();
		InputStream inputStream=getResources().openRawResource(R.raw.agedistrbution);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		Map dictionaryHash = new HashMap();
		try{
			String[] words=new String[3];
			String line;
			int primarykey=0;
			while((line = reader.readLine())!=null){
				Log.d(TAG, "File lines "+ line);
				words=line.split(",");
				diseaseOrder.put(Integer.valueOf(words[2]), words[0]);
			}
			
			Log.d(TAG, "Hashtable: "+diseaseOrder.toString());

		}
		catch (Exception ex) {
		       Log.v(ex.getMessage(), "message");
		}
	}
	
	public Hashtable valueCaompare(Hashtable unSortedTable, Hashtable sortedTable){
		
		return unSortedTable;
		
	}
	
	public ArrayList<String> orderList(ArrayList list){
		
		return list;
		
	}

}
