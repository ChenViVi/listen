package com.yellowzero.listen.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.Schedule;
import com.yellowzero.listen.util.ScreenUtil;
import com.zzhoujay.richtext.RichText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends BaseQuickAdapter<Schedule, BaseViewHolder>  implements LoadMoreModule {

    private Context context;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd\nE\nHH:mm:ss", Locale.CHINA);

    public ScheduleAdapter(Context context, List<Schedule> items) {
        super(R.layout.item_schedule, items);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Schedule item) {
        viewHolder.setText(R.id.tvName, item.getName())
                .setText(R.id.tvTime, format.format(item.getTime()));
        TextView tvContent = viewHolder.getView(R.id.tvContent);
        RichText.fromMarkdown(item.getContent()).into(tvContent);
        ImageView ivClock = viewHolder.getView(R.id.ivClock);
        if(item.getTime().after(new Date())) {
            ivClock.setImageResource(R.drawable.ic_clock);
            ivClock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar= Calendar.getInstance();
                    calendar.setTime(item.getTime());
                    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, item.getName());
                    intent.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY));
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE));
                    //intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                    intent.putExtra(AlarmClock.EXTRA_VIBRATE, true);
                    context.startActivity(intent);
                    Toast.makeText(context, R.string.ts_clock, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ivClock.setImageResource(R.drawable.circle);
        }
    }
}
