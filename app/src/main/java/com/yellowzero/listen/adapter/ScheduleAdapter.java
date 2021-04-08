package com.yellowzero.listen.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.Schedule;

import java.util.List;

public class ScheduleAdapter extends BaseQuickAdapter<Schedule, BaseViewHolder>  implements LoadMoreModule {

    private Context context;

    public ScheduleAdapter(Context context, List<Schedule> items) {
        super(R.layout.item_schedule, items);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Schedule item) {

    }
}
