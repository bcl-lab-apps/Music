package com.bcl.music23andme;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bcl.music23andme.R;

import android.app.Application;
import android.util.Log;
import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

//Sorting methods
public class MusicApp extends Application{
	
	static final String TAG= "GeneMusic";
	Hashtable<Integer, String> diseaseOrder;
	ArrayList<String> diseaselist;
	Hashtable<Integer, String> listOrder;
	Hashtable<String, List<String>> SNPs=new Hashtable<String, List<String>>();
	static String bearer_token="";
	static String profile_id="";
	@Override
	public void onCreate() {
		List<String> SNP1= Arrays.asList("rs1447295","rs6983267","rs10505483","rs1859962","rs4430796","rs10993994"
				,"rs7127900","rs8102476","rs12621278","rs17021918","rs10486567","rs1512268");
		List<String> SNP2=Arrays.asList("rs1061147","rs547154","rs3750847","rs2230199","rs9621532");
		SNPs.put("Prosate Cancer", SNP1);
		SNPs.put("Age-related Macular Degeneration", SNP2);
		diseaselist=new ArrayList<String>();
		diseaseOrder= new Hashtable<Integer, String>();
		InputStream inputStream=getResources().openRawResource(R.raw.agedistrbution);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		final String CLIENT_ID = "021772bf49aa3d0b7b5623fa926eab02";
		final String CLIENT_SECRET = "7278d594495d28c17cc183ef3279be24";
		
		listOrder= new Hashtable<Integer, String>();
		
		try{
			String[] words=new String[3];
			String line;
			int primarykey=0;
			while((line = reader.readLine())!=null){
				Log.d(TAG, "File lines "+ line);
				words=line.split(",");
				diseaseOrder.put(Integer.valueOf(words[2]), words[0]);
			}
			
			//Log.d(TAG, "Hashtable: "+diseaseOrder.toString());

		}
		catch (Exception ex) {
		       Log.v(ex.getMessage(), "message");
		}
	}
	
	public void valueCompare(List<String> diseases,Hashtable sortedTable){
		Log.d(TAG, "valueCompare() is called");
		int matchCount=0;
		for(int x=0;x<diseases.size();x++){
			Iterator it=sortedTable.entrySet().iterator();
			Log.d(TAG, "currently comparing: "+ diseases.get(x));
			while(it.hasNext()){
				Map.Entry<Integer, String> entry = (Entry<Integer, String>) it.next();
				if(diseases.get(x).equals(entry.getValue()) || diseases.get(x).startsWith(entry.getValue())){
					Log.d(TAG, "match: "+ diseases.get(x) + " "+ entry.getValue());
					listOrder.put(entry.getKey(), entry.getValue());
					matchCount++;
				}
			
			} 
			
			Log.d(TAG, "Current iteration: "+ Integer.toString(x));
		}
		Log.d(TAG, "match count: " + Integer.toString(matchCount));
		Iterator it2= listOrder.entrySet().iterator();
		Integer[] keys= (Integer[]) listOrder.keySet().toArray(new Integer[0]);
		Arrays.sort(keys);
		
		for(int i=0;i<keys.length;i++){
			diseases.set(i, listOrder.get(keys[i]));
		}
		
	}
	/**
	public ArrayList<String> sortList(ArrayList list){
		for
		return list;
		
	}
	**/
	
	public ArrayList<String> orderLists(ArrayList unorderedList, ArrayList orderedList){
		
		return unorderedList;
		
	}

}
