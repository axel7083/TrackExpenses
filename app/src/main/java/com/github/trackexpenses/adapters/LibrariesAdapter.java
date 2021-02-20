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

public class LibrariesAdapter extends RecyclerView.Adapter<LibrariesAdapter.ViewHolder> {

    private List<Library> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public LibrariesAdapter(Context context, List<Library> mData) {
        this.context = context;
        updateData(mData);
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateData(List<Library> mData)
    {
        this.mData = mData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_library, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(mData.get(position).getName());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return (mData == null)?0:mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, license, website;
        LinearLayout layout;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title_library_row);
            license = itemView.findViewById(R.id.license_library_row);
            website = itemView.findViewById(R.id.website_library_row);
            layout = itemView.findViewById(R.id.layout_library_row);

            layout.setOnClickListener(this);
            license.setOnClickListener(this);
            website.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.license_library_row:
                    Intent licenseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mData.get(getAdapterPosition()).getLicense()));
                    context.startActivity(licenseIntent);
                    break;
                case R.id.website_library_row:
                    Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mData.get(getAdapterPosition()).getUrl()));
                    context.startActivity(urlIntent);
                    break;
                case R.id.layout_library_row:
                    if (mClickListener != null) mClickListener.onItemClick(view,getAdapterPosition());
                    break;
            }
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int index);
    }
}
