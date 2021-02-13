package com.github.trackexpenses.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.trackexpenses.R;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TitleViewHolder extends RecyclerView.ViewHolder {

    private TextView title;

    public TitleViewHolder(View v) {
        super(v);
        title = v.findViewById(R.id.title_row);
    }

}
