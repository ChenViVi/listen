package com.yellowzero.listen.view;

import android.content.Context;
import android.view.View;

import com.yellowzero.listen.R;
import com.yellowzero.listen.model.ImageTag;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class TagsPopup extends BasePopupWindow {

    private TagCloudView viewTag;

    public TagsPopup(Context context) {
        super(context);
    }

    public void setTags(List<String> tags) {
        viewTag.setTags(tags);
    }

    public void setOnTagClickListener(TagCloudView.OnTagClickListener onTagClickListener) {
        viewTag.setOnTagClickListener(onTagClickListener);
    }

    @Override
    public View onCreateContentView() {
        View view = createPopupById(R.layout.popup_tags);
        viewTag = view.findViewById(R.id.viewTag);
        viewTag.setEnableSelect(true);
        return view;
    }
}