package com.yellowzero.Dang.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import com.yellowzero.Dang.R;

import java.util.ArrayList;
import java.util.List;

public class AndTabManager implements View.OnClickListener {

    private int selectPosition = 0;
    private Context context;
    private ViewGroup layout;
    private List<View> tabViews = new ArrayList<>();
    private List<Tab> tabs = new ArrayList<>();;
    private OnTabCheckListener onTabCheckListener;

    public AndTabManager(Context context, LinearLayout layout) {
        this.context = context;
        this.layout = layout;
        layout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                // 调整每个Tab的大小
                for(int i = 0; i < tabViews.size(); i++){
                    View tabView = tabViews.get(i);
                    int width = AndTabManager.this.context.getResources().getDisplayMetrics().widthPixels / (tabs.size());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
                    tabView.setLayoutParams(params);
                }
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                tabViews.clear();
                tabs.clear();
            }
        });
    }

    /**
     * 添加Tab
     * @param tab
     */
    public void addTab(Tab tab) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_tab_item,null);
        TextView tvTab = (TextView) view.findViewById(R.id.tvTab);
        ImageView ivTab = (ImageView) view.findViewById(R.id.ivTab);
        ivTab.setImageResource(tab.getIconNormalResId());
        tvTab.setText(tab.getText());
        tvTab.setTextColor(tab.getNormalColor());
        view.setTag(tabViews.size());
        view.setOnClickListener(this);
        tabViews.add(view);
        tabs.add(tab);
        layout.addView(view);
    }

    /**
     * 添加Tab
     * @param tab
     */
    public void addTab(Tab tab, @LayoutRes int itemId, @IdRes int iconId, @IdRes int textId) {
        View view = LayoutInflater.from(context).inflate(itemId,null);
        TextView textView = (TextView) view.findViewById(textId);
        ImageView imageView = (ImageView) view.findViewById(iconId);
        imageView.setImageResource(tab.getIconNormalResId());
        textView.setText(tab.getText());
        textView.setTextColor(tab.getNormalColor());
        view.setTag(tabViews.size());
        view.setOnClickListener(this);
        tabViews.add(view);
        tabs.add(tab);
        layout.addView(view);
    }

    /**
     * 设置选中Tab
     * @param position
     */
    public void setCurrentItem(int position) {
        if(position >= tabs.size() || position < 0) {
            position = 0;
        }
        tabViews.get(position).performClick();
        updateState(position);
    }

    /**
     * 更新状态
     * @param position
     */
    private void updateState(int position) {
        for(int i = 0; i < tabViews.size(); i++) {
            View view = tabViews.get(i);
            TextView textView = (TextView) view.findViewById(R.id.tvTab);
            ImageView imageView = (ImageView) view.findViewById(R.id.ivTab);
            if(i == position) {
                imageView.setImageResource(tabs.get(i).getIconPressedResId());
                textView.setTextColor(tabs.get(i).getSelectColor());
            } else {
                imageView.setImageResource(tabs.get(i).getIconNormalResId());
                textView.setTextColor(tabs.get(i).getNormalColor());
            }
        }
    }

    public void setOnTabCheckListener(OnTabCheckListener onTabCheckListener) {
        this.onTabCheckListener = onTabCheckListener;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        selectPosition = position;
        if(onTabCheckListener !=null) {
            onTabCheckListener.onTabSelected(view, position);
        }
        updateState(position);
    }

    public int getSelectPosition() {
        return selectPosition;
    }
}
