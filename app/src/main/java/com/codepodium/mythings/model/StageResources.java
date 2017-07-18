package com.codepodium.mythings.model;

public class StageResources {

    int spriteSheet;
    String SpritePoints;
    int stageBackground;
    int stageTime;
    int StageBGM;


    public  StageResources(int spriteSheet, String SpritePoints, int stageBackground, int stageTime, int stageBGM)
    {
        this.spriteSheet = spriteSheet;
        this.SpritePoints = SpritePoints;
        this.stageBackground = stageBackground;
        this.stageTime = stageTime;
        this.StageBGM = stageBGM;
    }



    public int getSpriteSheet() {
        return spriteSheet;
    }

    public void setSpriteSheet(int spriteSheet) {
        this.spriteSheet = spriteSheet;
    }

    public String getSpritePoints() {
        return SpritePoints;
    }

    public void setSpritePoints(String spritePoints) {
        SpritePoints = spritePoints;
    }

    public int getStageBackground() {
        return stageBackground;
    }

    public void setStageBackground(int stageBackground) {
        this.stageBackground = stageBackground;
    }

    public int getStageTime() {
        return stageTime;
    }

    public void setStageTime(int stageTime) {
        this.stageTime = stageTime;
    }

    public int getStageBGM() {
        return StageBGM;
    }

    public void setStageBGM(int stageBGM) {
        StageBGM = stageBGM;
    }
}
