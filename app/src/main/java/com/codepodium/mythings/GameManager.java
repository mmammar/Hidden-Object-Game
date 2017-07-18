package com.codepodium.mythings;


import com.codepodium.mythings.model.ObjectFrame;
import com.codepodium.mythings.model.StageResources;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManager {

    /**
     *  Private constructor. Prevents instantiation from other classes.
     */
    private GameManager() {

    }

    /**
     * Initializes singleton.
     *
     * GameManagerHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class GameManagerHolder {
        private static final GameManager INSTANCE = new GameManager();
    }

    public static GameManager getInstance() {
        return  GameManagerHolder.INSTANCE;
    }



    private HashMap<String, ObjectFrame> objectList = new HashMap<>();
    private ArrayList<String> objectKeys = new ArrayList<>(10);
    private int IV_WIDTH = 0;
    private int CURRENT_STAGE = 1;
    private int CURRENT_STAGE_SCORE = 0;
    private int CURRENT_HINTS = 0;
    private int CURRENT_REMAINING_TIME = 0;
    private HashMap<String, String> objectLocations = new HashMap<>();
    private String playerName = "";

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public HashMap<String, String> getObjectLocations() {
        return objectLocations;
    }

    public void addObjectToLocationHolder(String key, String json)
    {
        objectLocations.put(key,json);
    }

    public void setObjectLocations(HashMap<String, String> objectLocations) {
        this.objectLocations = objectLocations;
    }

    public int getCURRENT_REMAINING_TIME() {
        return CURRENT_REMAINING_TIME;
    }

    public void setCURRENT_REMAINING_TIME(int CURRENT_REMAINING_TIME) {
        this.CURRENT_REMAINING_TIME = CURRENT_REMAINING_TIME;
    }

    public int getCURRENT_HINTS() {
        return CURRENT_HINTS;
    }

    public void setCURRENT_HINTS(int CURRENT_HINTS) {
        this.CURRENT_HINTS = CURRENT_HINTS;
    }

    public HashMap<String, ObjectFrame> getFinalObjectList() {
        return this.objectList;
    }

    public ArrayList<String> getObjectKeys() {
        return this.objectKeys;
    }

    public int getIV_WIDTH(){
        return this.IV_WIDTH;
    }

    public  int getCURRENT_STAGE() {
        return this.CURRENT_STAGE;
    }

    public int getCURRENT_STAGE_SCORE() {
        return this.CURRENT_STAGE_SCORE;
    }

    public void setFinalObjectList(HashMap<String, ObjectFrame> finalList) {
        this.objectList = finalList;
    }

    public void setObjectKeys(ArrayList<String> keys) {
        this.objectKeys = keys;
    }

    public void setIV_WIDTH(int iv_width){
        this.IV_WIDTH = iv_width;
    }

    public void setCURRENT_STAGE(int current_stage) {
        if(current_stage == 10)
            this.CURRENT_STAGE = 1;
        else
            this.CURRENT_STAGE = current_stage;
    }

    public void setCURRENT_STAGE_SCORE(int current_stage_score) {
        this.CURRENT_STAGE_SCORE = current_stage_score;
    }

    /**
     * At Index '0' SpriteSheet resource id. Which is an Integer value.
     * At Index '1' SpriteSheet points in json file. Which is an String value.
     * At Index '2' Stage Background
     * At Index '3' Stage Time is defined
     * At Index '4' Stage BGM
     * @return
     */
    public StageResources getStageResources()
    {

        switch (this.CURRENT_STAGE)
        {
            case 1:
                return new StageResources(R.drawable.sheet1, "stage1.json", R.drawable.stage1, 90000, R.raw.stage1);
            case 2:
                return new StageResources(R.drawable.sheet2, "stage2.json", R.drawable.stage2, 90000, R.raw.stage1);
            case 3:
                return new StageResources(R.drawable.sheet3, "stage3.json", R.drawable.stage3, 90000, R.raw.stage3);
            case 4:
                return new StageResources(R.drawable.sheet4, "stage4.json", R.drawable.stage4, 90000, R.raw.stage4);
            case 5:
                return new StageResources(R.drawable.sheet5, "stage5.json", R.drawable.stage5, 90000, R.raw.stage5);
            case 6:
                return new StageResources(R.drawable.sheet6, "stage6.json", R.drawable.stage6, 90000, R.raw.stage6);
            case 7:
                return new StageResources(R.drawable.sheet7, "stage7.json", R.drawable.stage7, 90000, R.raw.stage7);
            case 8:
                return new StageResources(R.drawable.sheet8, "stage8.json", R.drawable.stage8, 90000, R.raw.stage8);
            case 9:
                return new StageResources(R.drawable.sheet9, "stage9.json", R.drawable.stage9, 90000, R.raw.stage9);
            //case 10:
              //  return new StageResources(R.drawable.sheet1, "stage1.json", R.drawable.stage1, 30000, R.raw.stage1};
            default:
                this.CURRENT_STAGE = 1;
                this.CURRENT_STAGE_SCORE = 0;
                return new StageResources(R.drawable.sheet1, "stage1.json", R.drawable.stage1, 90000, R.raw.stage1);
        }
    }


    public int removeObjectFromList(String objName)
    {
        objectList.remove(objName);
        objectKeys.remove(objName);
        objectLocations.remove(objName);
        return objectList.size();
    }

    public void resetGame()
    {
        this.CURRENT_STAGE_SCORE = 0;
        this.CURRENT_STAGE = 1;
        this.objectKeys = new ArrayList<>(10);
        this.objectLocations = new HashMap<>();
        this.objectList = new HashMap<>();
        this.CURRENT_HINTS = 0;
        this.CURRENT_REMAINING_TIME = 0;
    }

}
