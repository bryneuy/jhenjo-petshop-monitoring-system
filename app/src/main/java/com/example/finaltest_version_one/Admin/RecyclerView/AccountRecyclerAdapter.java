package com.example.finaltest_version_one.Admin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finaltest_version_one.Admin.Model.Accounts;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountRecyclerAdapter extends FirestoreRecyclerAdapter<Accounts, AccountRecyclerAdapter.AccountViewHolder> {

    private AccountListener accountListener;

    public AccountRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Accounts> options, AccountListener accountListener) {
        super(options);
        this.accountListener = accountListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull AccountViewHolder holder, int position, @NonNull Accounts accounts) {
        holder.fullName.setText(accounts.getFullName());
        holder.role.setText(accounts.getRole());

        Glide.with(holder.itemView)
                .asBitmap()
                .load(accounts.getDownloadURL())
                .into(holder.circleImageView);

    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.admin_account_row,parent,false);
        return new AccountViewHolder(v);
    }

    public class AccountViewHolder extends RecyclerView.ViewHolder {

        TextView fullName,role;
        CircleImageView circleImageView;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.fullName);
            role = itemView.findViewById(R.id.role);
            circleImageView = itemView.findViewById(R.id.aCircleImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    accountListener.editAccount(snapshot);

                }
            });



        }
        public void disableItem (){
            accountListener.deleteAccount(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }
    public interface AccountListener {
        void editAccount(DocumentSnapshot documentSnapshot);
        void deleteAccount(DocumentSnapshot documentSnapshot);
    }
}
