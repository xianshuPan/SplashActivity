package com.hylg.igolf.db;

import java.io.File;

import com.hylg.igolf.utils.FileUtils;
import com.hylg.igolf.utils.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class InviteScoreNotesDatabase {
	private static final String TAG = "InviteScoreNotesDatabase";

	private static File mDbFile = null;
	private static String mDbPath = null;
	private static final String DATABASE_NAME = "invite_score_notes.db";
	private static int DB_VERSION = 1;
	private static byte[] lock = new byte[0];
	
	public InviteScoreNotesDatabase(Context context) {
		synchronized(lock) {
			if(null == mDbFile || !mDbFile.exists()) {
				mDbFile = FileUtils.createDatabaseFile(context, DATABASE_NAME);
				Utils.logh(TAG, "create new file, for not exist ");
			}
			if(null == mDbPath) {
				mDbPath = mDbFile.getAbsolutePath();
				Utils.logh(TAG, "get path " + mDbPath);
			}
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(mDbFile, null);
			int version = db.getVersion();
			Utils.logh(TAG, "ExternalDatabase version: " + version + " DB_VERSION: " + DB_VERSION);
			
			if(0 == version) { // no data file, first create
				db.beginTransaction();
				db.execSQL(inviteScoreNotesSqlCreate());
				db.setVersion(DB_VERSION);
				db.setTransactionSuccessful();
				db.endTransaction(); 
			} else if(version < DB_VERSION) { // upgrade
				db.beginTransaction();
				db.execSQL(SqlDrop(INVITE_SCORE_NOTES_ATTRS));
				db.execSQL(inviteScoreNotesSqlCreate());
				db.setVersion(DB_VERSION);
				db.setTransactionSuccessful();
				db.endTransaction(); 
			} else { // do nothing
				
			}
			db.close();
		}
	}
	
	public void deleteScoreNote(String appSn, String sn) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READWRITE);
		Cursor cursor = null;
		db.beginTransaction();
		try {
			String sql = getScoreNotesSqlPre();
			cursor = db.rawQuery(sql, new String[]{appSn, sn});
			if(cursor.moveToFirst()) {
				StringBuilder b = new StringBuilder();
				b.append("delete from ").append(INVITE_SCORE_NOTES_ATTRS).append(" where ")
					.append(ATTRS_APP_SN).append("=? and ")
					.append(ATTRS_SN).append("=?");
				sql = b.toString();
				Utils.logh(TAG, sql);
				db.execSQL(sql, new Object[]{appSn, sn});
			}
		} finally{
			db.endTransaction();
			if(null != cursor) cursor.close();
			db.close();
		}
	}
	
	public void savesScoreNote(InviteScoreNotesData data) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READWRITE);
		Cursor cursor = null;
		db.beginTransaction();
		try {
			String sql = getScoreNotesSqlPre();
			cursor = db.rawQuery(sql, new String[]{data.appSn, data.sn});
			if(cursor.moveToFirst()) {
				StringBuilder b = new StringBuilder();
				b.append("update ").append(INVITE_SCORE_NOTES_ATTRS).append(" set ");
				b.append(ATTRS_SCORE).append("=?, ")
					.append(ATTRS_LATI).append("=?, ")
					.append(ATTRS_LONGI).append("=? ")
					.append("where ")
					.append(ATTRS_APP_SN).append("=? and ")
					.append(ATTRS_SN).append("=?");
				sql = b.toString();
				Utils.logh(TAG, sql);
				// just need update the page step
				db.execSQL(sql, new Object[]{data.score, data.lati, data.longi, data.appSn, data.sn});
			} else {
				StringBuilder b = new StringBuilder();
				b.append("insert into ").append(INVITE_SCORE_NOTES_ATTRS).append("(");
				b.append(ATTRS_APP_SN).append(", ")
					.append(ATTRS_SN).append(", ")
					.append(ATTRS_SCORE).append(", ")
					.append(ATTRS_LATI).append(", ")
					.append(ATTRS_LONGI);
				b.append(") values(?,?,?,?,?)");
				sql = b.toString();
				Utils.logh(TAG, sql);
				db.execSQL(sql, new Object[]{data.appSn, data.sn, data.score, data.lati, data.longi});
			}
			db.setTransactionSuccessful();
		} finally{
			db.endTransaction();
			if(null != cursor) cursor.close();
			db.close();
		}
	}
	
	public boolean isScoreNoteExists(String appSn, String sn) {
		synchronized(lock) {
			boolean ret = true;
			SQLiteDatabase db= SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
			Utils.logh(TAG, "isScoreNoteExists appSn: " + appSn + " sn: " + sn);
			String sql = getScoreNotesSqlPre();
			Cursor cursor = db.rawQuery(sql, new String[]{appSn, sn});
			if(!cursor.moveToFirst()) {
				ret = false;
			}
			cursor.close();
			db.close();
			Utils.logh(TAG, "isScoreNoteExists ret: " + ret);
			return ret;
		}
	}
	
	public InviteScoreNotesData getScoreNote(String appSn, String sn) {
		synchronized(lock) {
			InviteScoreNotesData data = new InviteScoreNotesData();
			SQLiteDatabase db = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
			Utils.logh(TAG, "getScoreNote appSn: " + appSn + " sn: " + sn);
			String sql = getScoreNotesSqlPre();
			Cursor cursor = db.rawQuery(sql, new String[]{appSn, sn});
			if(cursor.moveToFirst()) {
				data.appSn = appSn; // cursor.getString(1);
				data.sn = sn; // cursor.getString(2);
				data.score = cursor.getInt(3);
				data.lati = cursor.getDouble(4);
				data.longi = cursor.getDouble(5);
			}
			cursor.close();
			db.close();
			return data;
		}
	}
	
	private String getScoreNotesSqlPre() {
		StringBuilder b = new StringBuilder();
		b.append("select * from ").append(INVITE_SCORE_NOTES_ATTRS);
		b.append(" where ");
		b.append(ATTRS_APP_SN).append("=? and ");
		b.append(ATTRS_SN).append("=?");
		Utils.logh(TAG, "getGuideInPageDataSqlPre " + b.toString());
		return b.toString();
	}
	
	public void clearData() {
		synchronized(lock) {
			SQLiteDatabase db = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READWRITE);
			db.beginTransaction();
			try {
				db.execSQL(SqlDelete(INVITE_SCORE_NOTES_ATTRS));
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
				db.close();
			}
		}
	}
	
	private final static String KEY_ID = "id";
	
	private final static String INVITE_SCORE_NOTES_ATTRS = "invite_score_notes";
	private final static String ATTRS_APP_SN = "appSn";
	private final static String ATTRS_SN = "sn";
	private final static String ATTRS_SCORE = "score";
	private final static String ATTRS_LATI = "lati";
	private final static String ATTRS_LONGI = "longi";
	
	private String inviteScoreNotesSqlCreate() {
		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE IF NOT EXISTS ").append(INVITE_SCORE_NOTES_ATTRS);
		b.append("  (").append(KEY_ID).append(" integer primary key autoincrement, ");
		b.append(ATTRS_APP_SN).append(" varchar(20), ");
		b.append(ATTRS_SN).append(" varchar(50), ");
		b.append(ATTRS_SCORE).append(" INTEGER, ");
		b.append(ATTRS_LATI).append(" DOUBLE, ");
		b.append(ATTRS_LONGI).append(" DOUBLE)");
		Utils.logh(TAG, "inviteScoreNotesSqlCreate: " + b.toString());
		return b.toString();
	}
	
	private String SqlDrop(String table) {
		return "DROP TABLE IF EXISTS " + table;
	}
	
	private String SqlDelete(String table) {
		return "delete from  " + table;
	}
}
