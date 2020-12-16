package com.yellowzero.Dang.view;

public class Tab {
    private int iconNormalResId;
    private int iconPressedResId;
    private int normalColor;
    private int selectColor;
    private String text;

    public int getIconNormalResId() {
        return iconNormalResId;
    }

    public Tab setIconNormalResId(int iconNormalResId) {
        this.iconNormalResId = iconNormalResId;
        return this;
    }

    public int getIconPressedResId() {
        return iconPressedResId;
    }

    public Tab setIconPressedResId(int iconPressedResId) {
        this.iconPressedResId = iconPressedResId;
        return this;
    }

    public int getNormalColor() {
        return normalColor;
    }

    public Tab setNormalColor(int normalColor) {
        this.normalColor = normalColor;
        return this;
    }

    public int getSelectColor() {
        return selectColor;
    }

    public Tab setSelectColor(int selectColor) {
        this.selectColor = selectColor;
        return this;
    }

    public String getText() {
        return text;
    }

    public Tab setText(String text) {
        this.text = text;
        return this;
    }
}
