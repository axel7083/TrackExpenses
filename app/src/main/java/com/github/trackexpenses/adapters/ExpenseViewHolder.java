package com.github.trackexpenses.adapters;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.trackexpenses.R;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView title_expense_row, category_expense_row, smiley_expense_row, value_expense_row;
    private LinearLayout layout_category_row;
    private ExpenseClickListener mClickListener;

    public ExpenseViewHolder(View v) {
        super(v);
        title_expense_row =  v.findViewById(R.id.title_expense_row);
        smiley_expense_row = v.findViewById(R.id.smiley_expense_row);
        category_expense_row = v.findViewById(R.id.category_expense_row);
        value_expense_row = v.findViewById(R.id.value_expense_row);
        layout_category_row = v.findViewById(R.id.layout_category_row);

        layout_category_row.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mClickListener.onItemClick(view,getAdapterPosition());
    }

    // parent activity will implement this method to respond to click events
    public interface ExpenseClickListener {
        void onItemClick(View view, int position);
    }
}
