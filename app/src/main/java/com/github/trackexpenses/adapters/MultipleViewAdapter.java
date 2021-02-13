package com.github.trackexpenses.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.github.trackexpenses.R;
import com.github.trackexpenses.models.Category;
import com.github.trackexpenses.models.Expense;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

import static com.github.trackexpenses.utils.CategoryUtils.getCategory;

// Thanks https://github.com/codepath/android_guides/wiki/Heterogeneous-Layouts-inside-RecyclerView
@Setter
public class MultipleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<Object> items;
    private ArrayList<Category> categories;

    private final int EXPENSE = 0, TITLE = 1;
    private ExpenseViewHolder.ExpenseClickListener mClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MultipleViewAdapter(List<Object> items, ArrayList<Category> categories, ExpenseViewHolder.ExpenseClickListener mClickListener) {
        this.items = items;
        this.categories = categories;
        this.mClickListener = mClickListener;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items==null?0:this.items.size();
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Expense) {
            return EXPENSE;
        } else if (items.get(position) instanceof String) {
            return TITLE;
        }
        return -1;
    }


    /**
     * This method creates different RecyclerView.ViewHolder objects based on the item view type.\
     *
     * @param viewGroup ViewGroup container for the item
     * @param viewType type of view to be inflated
     * @return viewHolder to be inflated
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case EXPENSE:
                View v1 = inflater.inflate(R.layout.expense_row, viewGroup, false);
                viewHolder = new ExpenseViewHolder(v1);
                break;
            case TITLE:
                View v2 = inflater.inflate(R.layout.title_row, viewGroup, false);
                viewHolder = new TitleViewHolder(v2);
                break;
            default:

                break;
        }
        return viewHolder;
    }

    /**
     * This method internally calls onBindViewHolder(ViewHolder, int) to update the
     * RecyclerView.ViewHolder contents with the item at the given position
     * and also sets up some private fields to be used by RecyclerView.
     *
     * @param viewHolder The type of RecyclerView.ViewHolder to populate
     * @param position Item position in the viewgroup.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case EXPENSE:
                ExpenseViewHolder vh1 = (ExpenseViewHolder) viewHolder;
                vh1.setMClickListener(mClickListener);
                configureViewHolder1(vh1, position);
                break;
            case TITLE:
                TitleViewHolder vh2 = (TitleViewHolder) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            default:

                break;
        }
    }

    private void configureViewHolder1(ExpenseViewHolder eVH, int position) {
        Expense expense = (Expense) items.get(position);
        if (expense != null) {
            eVH.getTitle_expense_row().setText(expense.getTitle());

            Category cat = getCategory(expense.getCategory() + "",categories);
            if(cat != null) {
                eVH.getSmiley_expense_row().setText(cat.getSmiley());
                eVH.getCategory_expense_row().setText(cat.getName());
            }

            eVH.getValue_expense_row().setText("- $" + expense.getValue()); //TODO: get character for currency
        }
    }

    private void configureViewHolder2(TitleViewHolder vh2, int position) {
        vh2.getTitle().setText((String) items.get(position));
    }


}