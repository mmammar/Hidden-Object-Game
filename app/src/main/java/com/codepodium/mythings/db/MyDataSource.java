package com.codepodium.mythings.db;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.codepodium.mythings.GameManager;
import com.codepodium.mythings.model.ObjectFrame;
import com.codepodium.mythings.model.Scores;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;




public class MyDataSource {

	
	SQLiteOpenHelper dbhelper;
	SQLiteDatabase database;
    Context context;
	
	private static final String[] allColumns = {
            MyOpenHelper.col_id,
            MyOpenHelper.col_StageData
	};

    private  static final String[] allColumnsScore = {
            MyOpenHelper.col_id,
            MyOpenHelper.col_playername,
            MyOpenHelper.col_score
    };
	
	public MyDataSource(Context context) {
		dbhelper = new MyOpenHelper(context);
        this.context = context;
	}
	
	private void open()
	{
		database = dbhelper.getWritableDatabase();
	}
	
	private void close()
	{
		dbhelper.close();
	}

    public void insertGameData()
    {
        new SaveGameData().execute("");
    }

    public boolean loadGameData()
    {
        open();
        Cursor c;
        boolean flag = false;
        c = database.query(MyOpenHelper.table_SaveStage, allColumns, " id = 1 ", null, null, null, null);
        if(c != null)
        {
            if(c.getCount() > 0)
            {
                while (c.moveToNext())
                {
                    String s = c.getString(c.getColumnIndex(MyOpenHelper.col_StageData));
                    if(s.length() < 10)
                        flag = false;
                    else {
                        try {

                            JSONArray ja = new JSONArray(s);
                            GsonBuilder builder = new GsonBuilder();
                            Gson gson = builder.create();

                            GameManager gm = GameManager.getInstance();
                            gm.setObjectKeys(gson.fromJson(ja.get(1).toString(), gm.getObjectKeys().getClass()));

/*
                            HashMap<String, ObjectFrame> map = new HashMap<>();
                            ArrayList<ObjectFrame> arrayList = gson.fromJson(ja.get(0).toString(), new ArrayList<ObjectFrame>().getClass());

                                for (int i = 0; i < arrayList.size(); i++) {
                                    String key = gm.getObjectKeys().get(i);
                                    //ObjectFrame of = gson.fromJson(arrayList.get(i), ObjectFrame.class);
                                    map.put(key, arrayList.get(i));
                                }

                            gm.setFinalObjectList(map);
*/

                            HashMap<String, ObjectFrame> ma =  gson.fromJson(ja.get(0).toString(), new TypeToken<HashMap<String, ObjectFrame>>(){}.getType());
                            gm.setFinalObjectList(ma);
                            gm.setIV_WIDTH(gson.fromJson(ja.get(2).toString(), int.class));
                            gm.setCURRENT_STAGE(gson.fromJson(ja.get(3).toString(), int.class));
                            gm.setCURRENT_STAGE_SCORE(gson.fromJson(ja.get(4).toString(), int.class));
                            gm.setCURRENT_HINTS(gson.fromJson(ja.get(5).toString(), int.class));
                            gm.setCURRENT_REMAINING_TIME(gson.fromJson(ja.get(6).toString(), int.class));
                            gm.setObjectLocations(gson.fromJson(ja.get(7).toString(), gm.getObjectLocations().getClass()));
                            gm.setPlayerName(gson.fromJson(ja.get(8).toString(), String.class));

                            flag = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch(Exception e)
                        {
                            e.printStackTrace();
                            Log.e("GSON", e.getMessage());
                        }
                    }
                }
            }
        }
        close();
        return flag;
    }


    public long insertHighScore(int score, String playerName)
    {
        open();
        long id = -1;
        ContentValues values = new ContentValues();
        values.put(MyOpenHelper.col_playername, playerName);
        values.put(MyOpenHelper.col_score, score);
        id = database.insert(MyOpenHelper.table_score, null, values);
        close();
        return id;
    }

    public ArrayList<Scores> getHighScores()
    {
        open();
        ArrayList<Scores> scoreList = null;
        Cursor c = database.query(MyOpenHelper.table_score, allColumnsScore, null, null, null, null, null);
        if(c != null) {
            if(c.getCount() > 0) {
                scoreList = new ArrayList<>(c.getCount());
                while (c.moveToNext()) {
                    Scores s = new Scores();
                    s.setId(c.getInt(c.getColumnIndex(MyOpenHelper.col_id)));
                    s.setPlayerName(c.getString(c.getColumnIndex(MyOpenHelper.col_playername)));
                    s.setScores(c.getInt(c.getColumnIndex(MyOpenHelper.col_score)));
                    scoreList.add(s);
                }
            }
        }
        close();
        return scoreList;
    }

	

 private class SaveGameData extends AsyncTask<String, String, Long>{

     ProgressDialog pd;

     @Override
     protected void onPreExecute() {
         super.onPreExecute();
         open();
         pd = new ProgressDialog(context);
         pd.setTitle("Saving Game Data");
         pd.setMessage("Please Wait");
         pd.setCanceledOnTouchOutside(false);
         pd.setCancelable(false);
         pd.setIndeterminate(false);
         pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
         pd.show();

     }

     @Override
     protected Long doInBackground(String... params) {

         JSONArray ja = new JSONArray();
         GsonBuilder builder = new GsonBuilder();
         Gson gson = builder.create();

         GameManager gm = GameManager.getInstance();
         /*HashMap<String, String> tmap = new HashMap<>();
         ArrayList<String> tarray = new ArrayList<>(gm.getFinalObjectList().size());
         for(String key : gm.getFinalObjectList().keySet())
         {
             String sof = gson.toJson(gm.getFinalObjectList().get(key));
             tarray.add(sof);
         }

         ja.put(gson.toJson(tarray));*/
         ja.put(gson.toJson(gm.getFinalObjectList()));
         ja.put(gson.toJson(gm.getObjectKeys()));
         ja.put(gm.getIV_WIDTH());
         ja.put(gm.getCURRENT_STAGE());
         ja.put(gm.getCURRENT_STAGE_SCORE());
         ja.put(gm.getCURRENT_HINTS());
         ja.put(gm.getCURRENT_REMAINING_TIME());
         ja.put(gson.toJson(gm.getObjectLocations()));
         ja.put(gm.getPlayerName());


         long id;
         ContentValues values = new ContentValues();
         values.put(MyOpenHelper.col_id, 1);
         values.put(MyOpenHelper.col_StageData, ja.toString());
         id = database.update(MyOpenHelper.table_SaveStage, values, " id = 1 ", null);

         return id;
     }

     @Override
     protected void onPostExecute(Long s) {
         super.onPostExecute(s);
         pd.dismiss();
         close();
     }
 }
}
