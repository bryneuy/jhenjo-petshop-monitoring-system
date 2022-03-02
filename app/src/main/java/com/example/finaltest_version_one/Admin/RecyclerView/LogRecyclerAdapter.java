package com.example.finaltest_version_one.Admin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaltest_version_one.Admin.Model.LogClass;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class LogRecyclerAdapter extends FirestoreRecyclerAdapter<LogClass, LogRecyclerAdapter.LogViewHolder> {

    LogListener logListener;

    public LogRecyclerAdapter(@NonNull FirestoreRecyclerOptions<LogClass> options, LogListener logListener) {
        super(options);
        this.logListener = logListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull LogViewHolder holder, int position, @NonNull LogClass logClass) {
        holder.logtext.setText(String.valueOf(logClass.getValue()));
        holder.pricetext.setText(String.valueOf(logClass.getPrice()));
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.log_layout,parent,false);
        return new LogViewHolder(view);
    }

    public class LogViewHolder extends RecyclerView.ViewHolder{
        TextView logtext,pricetext;
        public LogViewHolder(@NonNull View itemView) {
            super(itemView);

            logtext = itemView.findViewById(R.id.logtext);
            pricetext = itemView.findViewById(R.id.pricetext);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    logListener.showLog(snapshot);
                }
            });
        }
    }

    public interface LogListener{
        void showLog(DocumentSnapshot documentSnapshot);
    }
}
