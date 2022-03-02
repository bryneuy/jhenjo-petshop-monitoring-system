package com.example.finaltest_version_one.Admin.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaltest_version_one.Admin.Model.Updates;
import com.example.finaltest_version_one.R;

import java.util.List;

public class UpdatesRecyclerAdapter extends RecyclerView.Adapter<UpdatesRecyclerAdapter.ViewHolder>{

    private ClickListener clickListener;
    private List<Updates> updates;
    private LayoutInflater inflater;
    public UpdatesRecyclerAdapter(Context context, List<Updates> updates){
        inflater = LayoutInflater.from(context);
        this.updates = updates;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.log_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.dtime.setText(updates.get(position).getTime());
        holder.price.setText(String.valueOf(updates.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return updates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dtime,price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dtime= itemView.findViewById(R.id.logtext);
            price = itemView.findViewById(R.id.pricetext);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null){
                clickListener.onItemClick(v, getAdapterPosition(),updates.get(getAdapterPosition()));
            }
        }
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View view, int position, Updates update);
    }
}
