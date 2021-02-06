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
    private List<ImageTag> tagList;

    public TagsPopup(Context context) {
        super(context);
    }

    public void setTags(List<ImageTag> tagList) {
        this.tagList = tagList;
        ArrayList<String> tags = new ArrayList<>();
        for (ImageTag tag: tagList)
            tags.add(tag.getName());
        viewTag.setTags(tags);
    }

    public List<Integer> getSelectTags() {
        return viewTag.getSelectedTagPosition();
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