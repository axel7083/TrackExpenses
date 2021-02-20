package com.github.trackexpenses.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.trackexpenses.R;
import com.github.trackexpenses.models.Library;

import java.util.List;

import kotlin.Pair;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {

    private List<Pair<String,String>> mData;
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    public OverviewAdapter(Context context, List<Pair<String,String>> mData) {
        this.context = context;
        updateData(mData);
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateData(List<Pair<String,String>> mData)
    {
        this.mData = mData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_overview, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(mData.get(position).getFirst());
        holder.content.setText(mData.get(position).getSecond());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return (mData == null)?0:mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_overview);
            content = itemView.findViewById(R.id.content_overview);
        }
    }
}
