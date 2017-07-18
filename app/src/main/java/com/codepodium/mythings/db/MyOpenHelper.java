package com.codepodium.mythings.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpenHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "myThings.db";
	private static final int DATABASE_VERSION = 2;
	
	public static final String table_SaveStage =  "saveStage";
	public static final String col_id = "id";
	public static final String col_StageData = "stage_data";

    public static final String table_score = "score";
    public static final String col_score = "totalscore";
    public static final String col_playername = "playername";
	
	private static final String TABLE_CREATE = 
			"CREATE TABLE " + table_SaveStage + " ( " +
					col_id 			+ " INTEGER PRIMARY KEY, " +
                    col_StageData 	+ " TEXT "	+
                    ")";

    private static final String TABLE_CREATE_SCORE =
            "CREATE TABLE " + table_score + " ( " +
                    col_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    col_playername + " VARCHAR(5), " +
                    col_score + " INTEGER )" ;

    private static final String INSERT_FIRST =
            "INSERT INTO " + table_SaveStage + " (" + col_id + ", " + col_StageData +") VALUES(1, '')";
	
	public MyOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_CREATE_SCORE);
        db.execSQL(INSERT_FIRST);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + table_SaveStage);
        db.execSQL("DROP TABLE IF EXISTS " + table_score);
		onCreate(db);
	}

}
