package com.codepodium.mythings.model;


public class ObjectLocationData {

    private int top = 0;
    private int bottom = 0;
    private int left = 0;
    private int right = 0;
    private int width = 0;
    private int height = 0;
    private int rule1 = 0;
    private int rule2 = 0;

    public ObjectLocationData(int left, int top, int right, int bottom, int height, int width, int rule1, int rule2)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.height = height;
        this.width = width;
        this.rule1 = rule1;
        this.rule2 = rule2;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRule1() {
        return rule1;
    }

    public void setRule1(int rule1) {
        this.rule1 = rule1;
    }

    public int getRule2() {
        return rule2;
    }

    public void setRule2(int rule2) {
        this.rule2 = rule2;
    }
}
