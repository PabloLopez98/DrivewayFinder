package pablo.myexample.drivewayfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class CardDetailsRecyclerView extends RecyclerView.Adapter<CardDetailsRecyclerView.ViewHolder> {

    private ArrayList<CardDetailsRecyclerViewObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    CardDetailsRecyclerView(Context context, ArrayList<CardDetailsRecyclerViewObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.datedetailscard, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String time = mData.get(position).getTime();
        String name = mData.get(position).getName();
        String status = mData.get(position).getStatus();
        holder.time.setText(time);
        holder.name.setText(name);
        holder.status.setText(status);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView time, name, status;

        ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.datedetailstime);
            name = itemView.findViewById(R.id.datedetailsname);
            status = itemView.findViewById(R.id.datedetailsstatus);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).getName();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}