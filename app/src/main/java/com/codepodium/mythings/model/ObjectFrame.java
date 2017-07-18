package com.codepodium.mythings.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ObjectFrame {

    private int x = 0;
    private int y = 0;
    private int w = 0;
    private int h = 0;

    public  ObjectFrame() {}


    public ObjectFrame(int X, int Y, int W, int H)
    {
        this.x = X;
        this.y = Y;
        this.w = W;
        this.h = H;
    }

    public ObjectFrame(JSONObject ja)
    {
        try {

            this.x = ja.getInt("x");
            this.y = ja.getInt("y");
            this.w = ja.getInt("w");
            this.h = ja.getInt("h");
        }
        catch (JSONException e)
        {
            this.x = 0;
            this.y = 0;
            this.w = 0;
            this.h = 0;
            e.printStackTrace();
        }
    }

    public String toString()
    {
        return "[x = " + x + ", y = " + y + ", w = " + w + ", h = " + h + "]";
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
