package com.yellowzero.listen.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Tab {
    private int iconNormalResId;
    private int iconPressedResId;
    private int normalColor;
    private int selectColor;
    private String text;
    private View view;
    private TextView textView;
    private ImageView imageView;
    private Fragment fragment;

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

    public View getView() {
        return view;
    }

    public Tab setView(View view) {
        this.view = view;
        return this;
    }

    public TextView getTextView() {
        return textView;
    }

    public Tab setTextView(TextView textView) {
        this.textView = textView;
        return this;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Tab setImageView(ImageView imageView) {
        this.imageView = imageView;
        return this;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public Tab setFragment(Fragment fragment) {
        this.fragment = fragment;
        return this;
    }
}
