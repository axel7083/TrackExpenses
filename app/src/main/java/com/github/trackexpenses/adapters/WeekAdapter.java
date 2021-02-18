package com.github.trackexpenses.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.trackexpenses.R;
import com.github.trackexpenses.models.Week;
import com.github.trackexpenses.utils.TimeUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import static com.github.trackexpenses.utils.TimeUtils.SIMPLE_PATTERN;
import static com.github.trackexpenses.utils.TimeUtils.formatSimple;
import static com.github.trackexpenses.utils.TimeUtils.formatTitle;
import static com.github.trackexpenses.utils.TimeUtils.toCalendar;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {

    private List<Week> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private String currency ;

    // data is passed into the constructor
    public WeekAdapter(Context context,List<Week> mData, String currency) {
        this.context =context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.currency = currency;
    }

    public void updateData(List<Week> mData)
    {
        this.mData = mData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_week, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        if(mData.get(position).date.equals(formatSimple(TimeUtils.getFirstDayOfWeek("Europe/Paris").toInstant(),"Europe/Paris")))
        {
            holder.title_week.setText("Current Week");
        }
        else
        {
            holder.title_week.setText(formatTitle(toCalendar(mData.get(position).date, SIMPLE_PATTERN).toInstant(),"Europe/Paris",true));
        }



        holder.spent_week.setText(currency +  df.format(mData.get(position).spent)); //TODO: replace $ with currency sign
        holder.goal_week_price.setText("/" + currency + df.format(mData.get(position).goal)); //TODO: replace $ with currency sign

        holder.week_progress.setMax((int) mData.get(position).goal);

        if(mData.get(position).spent < mData.get(position).goal)
        {
            holder.week_progress.setProgress((int) mData.get(position).spent);
            holder.week_progress.setProgressTintList(ColorStateList.valueOf(context.getColor(R.color.purple)));
        }
        else
        {
            holder.week_progress.setProgress((int)  mData.get(position).goal);
            holder.week_progress.setProgressTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
        }
        //holder.myTextView.setText(mData.get(position).getDate());
        //holder.smiley.setText(mData.get(position).getSmiley());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return (mData == null)?0:mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title_week, spent_week, goal_week_price;
        LinearLayout layout_week;
        ProgressBar week_progress;

        ViewHolder(View itemView) {
            super(itemView);
            title_week = itemView.findViewById(R.id.title_week_row);
            spent_week = itemView.findViewById(R.id.spent_week_row);
            goal_week_price = itemView.findViewById(R.id.goal_week_price);
            week_progress = itemView.findViewById(R.id.week_progress);

            layout_week = itemView.findViewById(R.id.layout_week_row);
            layout_week.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view,getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface Callback {
        void onSelect(String status);
    }
}
