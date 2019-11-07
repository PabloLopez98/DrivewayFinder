package pablo.myexample.drivewayfindertwo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pablo.myexample.drivewayfinder.R;
import pablo.myexample.drivewayfinder.SpotObjectClass;

class OneDriverAdapter extends RecyclerView.Adapter<pablo.myexample.drivewayfindertwo.OneDriverAdapter.ViewHolder> {

    private ArrayList<SpotObjectClass> mData;
    private LayoutInflater mInflater;
    private pablo.myexample.drivewayfindertwo.OneDriverAdapter.ItemClickListener mClickListener;

    OneDriverAdapter(Context context, ArrayList<SpotObjectClass> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public pablo.myexample.drivewayfindertwo.OneDriverAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.onedriver_cardview, parent, false);
        return new pablo.myexample.drivewayfindertwo.OneDriverAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(pablo.myexample.drivewayfindertwo.OneDriverAdapter.ViewHolder holder, int position) {
        SpotObjectClass spotObject = mData.get(position);
        holder.location.setText(spotObject.getDrivewayLocation());
        holder.rate.setText(spotObject.getRate());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView location, rate;

        ViewHolder(View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.locationAddress);
            rate = itemView.findViewById(R.id.locationRate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    SpotObjectClass getItem(int id) {
        return mData.get(id);
    }

    void setClickListener(pablo.myexample.drivewayfindertwo.OneDriverAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
