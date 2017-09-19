package com.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HAPIforkDAO extends DAO{


	public HAPIforkDAO(Context appCtx, DatabaseHelper helper) {
		super(appCtx, "hapiforkdevice", helper);
		// TODO Auto-generated constructor stub
	}

	protected static final String createSQL = "create table if not exists hapiforkdevice "+
			"(id INTEGER PRIMARY KEY, forkName TEXT, forkAddress TEXT);";


	/**
	 * Create the table
	 */
	public void createTable() {
		SQLiteDatabase db = null;
		try {
			db = this.getDatabase();
			db.execSQL(createSQL);
		} catch( Exception ex ) {
		}
	}

	public void deleteRecord(int userID){
		SQLiteDatabase db = null;
		try {
			db = this.getDatabase();
			db.delete(this.tableName, "id = ?", new String [] {String.valueOf(userID)} );
		} catch( Exception ex ) {
		}

	}

	public void updateRecord(String name, String address) {
    	
        SQLiteDatabase db = null;
        try {
                db = this.getDatabase();
                
        ContentValues  values = new ContentValues();
        values.put("forkName", name);
        values.put("forkAddress", address);
        
        db.update(this.tableName, values, "id=?", new String[] {String.valueOf(1)});
        
        System.out.println("updateRecord status" + values);
        
        }catch(Exception e){
 		}
	}
	
	
	 public boolean insertRecord(String name, String address){
 		
 		boolean status = true;
 		
 		try{
 			ContentValues values = new ContentValues();
 			values.put("id", 1);
 			values.put("forkName", name);
 			values.put("forkAddress", address);
            
             //need ID
             status = this.insertTable(values, "id=?", null);
             System.out.println("insertRecord status"+status + values);
             
 		}catch(Exception e){
 			status = false;
 		}
 		
 		return status;
 	}
	 
	 public String getForkName(){
     	
     	Cursor cur = this.getAllEntriesPerTable();
     	String forkName = "";

 		if (this.cursorHasRows(cur)) {
 			if (cur.moveToFirst()) {

 				forkName = cur.getString(cur.getColumnIndex("forkName"));
 				
 			}
 		}
 		cur.close();
 		System.out.println("getForkName@HAPIforkDAO: " + forkName);
 		return forkName;
     	
     }
	 
	 public String getForkAddress(){
	     	
	     	Cursor cur = this.getAllEntriesPerTable();
	     	String forkAddress = "";

	 		if (this.cursorHasRows(cur)) {
	 			if (cur.moveToFirst()) {

	 				forkAddress = cur.getString(cur.getColumnIndex("forkAddress"));
	 				
	 			}
	 		}
	 		cur.close();
	 		System.out.println("getForkAddress@HAPIforkDAO: " + forkAddress);
	 		return forkAddress;
	     	
	     }

}
