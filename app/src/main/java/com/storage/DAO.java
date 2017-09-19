package com.storage;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public abstract class DAO {
        /**
         * Database helper
         */
        protected DatabaseHelper dbHelper = null;
        /**
         * Table name
         */
        protected String tableName;
        /**
         * Application Context
         */
        protected Context appCtx;
        private boolean canUpgrade = true;
        /**
         * Abstract function called to create the table
         */
        protected abstract void createTable();

        public DAO(Context appCtx, String tablename, DatabaseHelper helper ) {
                this.appCtx = appCtx;
                this.tableName = tablename;
                this.dbHelper = helper;
                this.initialiseDatabase();
        }

        public DAO(Context appCtx, String tablename, DatabaseHelper helper, boolean canUpgrade) {
                this.appCtx = appCtx;
                this.tableName = tablename;
                this.dbHelper = helper;
                this.canUpgrade = canUpgrade;
                this.initialiseDatabase();
        }

        /**
         * Initialise the database
         * @param createString
         */
        protected void initialiseDatabase() {
                if( this.dbHelper == null )
                        this.dbHelper = new DatabaseHelper(appCtx);

                if( this.dbHelper.requireUpgrade() && canUpgrade ) {
                        String query = "DROP TABLE " + this.tableName + ";";
                        SQLiteDatabase db = null;
                        try {
                                db = this.getDatabase();
                                db.execSQL(query);
                        } catch( Exception ex ) {
                                ex.printStackTrace();
                        }
                }

                this.createTable();
        }
        /**
         * Get the database instance
         * @return
         */
        protected SQLiteDatabase getDatabase() {
                return this.dbHelper.getDatabase();
        }
        /**
         * Check if the cursor has rows
         * @param cursor
         * @return
         */
        protected boolean cursorHasRows(Cursor cursor) {
                return (cursor!=null && cursor.getCount()>0);
        }

        public void clearTable() {
                SQLiteDatabase db = null;
                try {
                        db = this.getDatabase();
                        db.delete(this.tableName, null, null);
                } catch( Exception ex ) {
                }
        }


        public boolean insertTable(ContentValues values, String whereClause, String[] whereArgs){

        	Log.d("insertTable", tableName);
                SQLiteDatabase db = null;
                try {
                        db = this.getDatabase();
                        if ( db.update(tableName, values, whereClause, whereArgs)<=0 )
                                db.insertOrThrow(tableName, null, values);
                        db.close();
                        return true;
                } catch( Exception ex ) {
                }
                return false;
                //DBInsertHandler.getInstance(dbHelper).addQueue(tableName, values, whereClause, whereArgs);
        }
        
        Cursor getAllEntriesPerTable(){
        	SQLiteDatabase db=this.getDatabase();
        	Cursor cur=db.rawQuery("SELECT * from "+tableName,new String [] {});
			return cur;
         
        }
        

}