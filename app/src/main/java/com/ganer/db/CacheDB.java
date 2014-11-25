package com.ganer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class CacheDB{
	private DBHelper helper = null;
    private static final String TABLE_NAME = "cache_data";
    private String keyName;
    
    
	public CacheDB(Context context, String keyName) {
		helper = new DBHelper(context);
		this.keyName = keyName;
	}
	
	public void set(String value){
		int ts = (int) (System.currentTimeMillis()/1000);

		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
			values.put("key", keyName );
            values.put("value", value );
			values.put("ts", ts );
			
		db.insert(TABLE_NAME, null, values); 
		db.close();
	}

	public boolean isValid() {
		int ts = (int) (System.currentTimeMillis()/1000);
		
		Bundle b = get();
		if(ts > b.getInt("ts")+60*30){
			return false;
		}
		return true;
	}
	
	public Bundle get(){
		Bundle b = new Bundle();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String sql		= "select * from "+TABLE_NAME+" where key = ?";
		Cursor cursor	= db.rawQuery(sql, new String[] {keyName});
		
		if (cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			b.putString("value", cursor.getString(1) );
            b.putInt("ts", Integer.parseInt(cursor.getString(2)) );
		}else{
            b.putString("value", "" );
			b.putInt("ts", 0);
		}
		        
		return b;
	}

}
