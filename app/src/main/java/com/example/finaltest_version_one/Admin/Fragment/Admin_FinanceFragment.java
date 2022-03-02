package com.example.finaltest_version_one.Admin.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finaltest_version_one.Admin.Model.LogClass;
import com.example.finaltest_version_one.Admin.Model.Updates;
import com.example.finaltest_version_one.Admin.Model.nInventory;
import com.example.finaltest_version_one.Admin.RecyclerView.FinanceRecyclerAdapter;
import com.example.finaltest_version_one.Admin.RecyclerView.LogRecyclerAdapter;
import com.example.finaltest_version_one.Admin.RecyclerView.UpdatesRecyclerAdapter;
import com.example.finaltest_version_one.Main.MainActivity;
import com.example.finaltest_version_one.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Admin_FinanceFragment extends Fragment implements LogRecyclerAdapter.LogListener{
    CollectionReference logref = FirebaseFirestore.getInstance().collection("Logss");
    UpdatesRecyclerAdapter updatesRecyclerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin__finance,container,false);
        RecyclerView recyclerViewer = v.findViewById(R.id.recyclerViewer);
        recyclerViewer.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        Query query = logref;
        FirestoreRecyclerOptions<LogClass> options = new FirestoreRecyclerOptions.Builder<LogClass>()
                .setQuery(query,LogClass.class)
                .build();
        LogRecyclerAdapter logRecyclerAdapter = new LogRecyclerAdapter(options,this);
        recyclerViewer.setAdapter(logRecyclerAdapter);
        logRecyclerAdapter.startListening();
        Button btnDaily = v.findViewById(R.id.btnDaily);
        Button btnMonthly = v.findViewById(R.id.btnMonthly);
        Button btnWeekly = v.findViewById(R.id.btnWeekly);
        List<Updates> dlogs = new ArrayList<>();
        List<String> ddate = new ArrayList<>();
        List<Updates> dlogs1 = new ArrayList<>();

        //For Logging out, a dedicated "setting" button has been placed to every dashboards
        Button settings = v.findViewById(R.id.settings);
        FrameLayout fl = v.findViewById(R.id.fl_logout);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fl.isShown()) {
                    fl.setVisibility(View.GONE);
                } else {
                    fl.setVisibility(View.VISIBLE);
                }
            }
        });

        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Logging Out")
                        .setMessage("Are you sure you want to log out?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                //Signing out currently signed in account
                                FirebaseAuth.getInstance().signOut();

                                //After signing out, redirecting user to landing page
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                                Objects.requireNonNull(getActivity()).finish();
                            }
                        }).create().show();

                fl.setVisibility(View.GONE);

            }
        });

        btnDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlogs.clear();
                ddate.clear();
                dlogs1.clear();
                logref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    double price;
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot:documentSnapshots){
                            LogClass logClass = snapshot.toObject(LogClass.class);
                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            String date = dateFormat.format(logClass.getValue());
                            price = logClass.getPrice();
                            Updates updates = new Updates(date,price);
                             if (!ddate.contains(updates.getTime())) {
                                ddate.add(updates.getTime());
                                dlogs.add(updates);
                             }
                            else{
                                 for(int i = 0; i <dlogs.size(); i++){
                                     Updates updates1 = dlogs.get(i);
                                     String araw = updates.getTime();
                                     String araw1 = updates1.getTime();
                                     if (araw1.equals(araw)){
                                         updates1.setPrice(updates.getPrice() + updates1.getPrice());
                                     }
                                 }
                            }
                        }
                    }

                });
                final LayoutInflater inflater1 = LayoutInflater.from(getContext());
                View v1 = inflater1.inflate(R.layout.custom_view_daily, null);
                RecyclerView recyclerViewTime = v1.findViewById(R.id.recyclerViewTime);
                updatesRecyclerAdapter = new UpdatesRecyclerAdapter(getContext(),dlogs);
                recyclerViewTime.setAdapter(updatesRecyclerAdapter);
                updatesRecyclerAdapter.setOnItemClickListener(new UpdatesRecyclerAdapter.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position, Updates update) {
                        Toast.makeText(getContext(), update.getTime(), Toast.LENGTH_SHORT).show();
                        logref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                                dlogs1.clear();
                                for (DocumentSnapshot snapshot:documentSnapshots) {
                                    LogClass logClass = snapshot.toObject(LogClass.class);
                                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                    String date = dateFormat.format(logClass.getValue());
                                    Updates updates = new Updates(date, logClass.getPrice());
                                    if (date.equals(update.getTime())){
                                        dlogs1.add(updates);
                                    }
                                }
                            }
                        });
                        final LayoutInflater inflater2 = LayoutInflater.from(getContext());
                        View v2 = inflater2.inflate(R.layout.custom_view_daily, null);
                        RecyclerView recyclerView = v2.findViewById(R.id.recyclerViewTime);
                        updatesRecyclerAdapter = new UpdatesRecyclerAdapter(getContext(), dlogs1);
                        recyclerView.setAdapter(updatesRecyclerAdapter);
                        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setView(v2)
                                .create();
                        alertDialog.show();
                    }
                });
                final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setView(v1)
                        .create();
                alertDialog.show();
            }
        });
        btnWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        dlogs.clear();
                        ddate.clear();
                        dlogs1.clear();
                        for (DocumentSnapshot documentSnapshot:documentSnapshots){
                            LogClass logClass = documentSnapshot.toObject(LogClass.class);
                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            String date = dateFormat.format(logClass.getValue());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(logClass.getValue());
                            int month = cal.get(Calendar.MONTH);
                            int week = cal.get(Calendar.WEEK_OF_MONTH);
                            //MONTH AND WEEK NUMBER
                            if (month == 0) {
                                date = "JANUARY " + "WEEK: " + week;
                            }
                            else if (month == 1) {
                                date = "FEBUARY " + "WEEK: " + week;
                            }
                            else if (month == 2){
                                date = "MARCH " + "WEEK: " + week;
                            }
                            else if (month == 3){
                                date = "APRIL " + "WEEK: " + week;
                            }
                            else if (month == 4){
                                date = "MAY " + "WEEK: " + week;
                            }
                            else if (month == 5){
                                date = "JUNE " + "WEEK: " + week;
                            }
                            else if (month == 6){
                                date = "JULY " + "WEEK: " + week;
                            }
                            else if (month == 7){
                                date = "AUGUST " + "WEEK: " + week;
                            }
                            else if (month == 8){
                                date = "SEPTEMBER " + "WEEK: " + week;
                            }
                            else if (month == 9){
                                date = "OCTOBER " + "WEEK: " + week;
                            }
                            else if (month == 10){
                                date = "NOVEMBER " + "WEEK: " + week;
                            }
                            else if (month == 11){
                                date = "DECEMBER " + "WEEK: " + week;
                            }
                            Updates update = new Updates(date, logClass.getPrice());
                            if (!ddate.contains(date)){
                                ddate.add(date);
                                dlogs.add(update);
                            }
                            else{
                                for(int i = 0; i <dlogs.size(); i++){
                                    Updates updates1 = dlogs.get(i);
                                    String araw = update.getTime();
                                    String araw1 = updates1.getTime();
                                    if (araw1.equals(araw)){
                                        updates1.setPrice(update.getPrice() + updates1.getPrice());
                                    }
                                }
                            }
                        }
                        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                        View v3 = layoutInflater.inflate(R.layout.custom_view_daily,null);
                        RecyclerView recyclerView = v3.findViewById(R.id.recyclerViewTime);
                        updatesRecyclerAdapter = new UpdatesRecyclerAdapter(getContext(),dlogs);
                        recyclerView.setAdapter(updatesRecyclerAdapter);
                        updatesRecyclerAdapter.setOnItemClickListener(new UpdatesRecyclerAdapter.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position, Updates update) {
                                logref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                                        dlogs1.clear();
                                        for (DocumentSnapshot snapshot:documentSnapshots) {
                                            LogClass logClass = snapshot.toObject(LogClass.class);
                                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                            String date = dateFormat.format(logClass.getValue());
                                            Updates updates = new Updates(date, logClass.getPrice());
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(logClass.getValue());
                                            int month = cal.get(Calendar.MONTH);
                                            int week = cal.get(Calendar.WEEK_OF_MONTH);
                                            String tempmonth = "";
                                            //MONTH CONVERSION
                                            if (month == 0) {
                                                tempmonth = "JANUARY " + "WEEK: " + week;
                                            }
                                            else if (month == 1) {
                                                tempmonth = "FEBUARY " + "WEEK: " + week;
                                            }
                                            else if (month == 2){
                                                tempmonth = "MARCH " + "WEEK: " + week;
                                            }
                                            else if (month == 3){
                                                tempmonth = "APRIL " + "WEEK: " + week;
                                            }
                                            else if (month == 4){
                                                tempmonth = "MAY " + "WEEK: " + week;
                                            }
                                            else if (month == 5){
                                                tempmonth = "JUNE " + "WEEK: " + week;
                                            }
                                            else if (month == 6){
                                                tempmonth = "JULY " + "WEEK: " + week;
                                            }
                                            else if (month == 7){
                                                tempmonth = "AUGUST " + "WEEK: " + week;
                                            }
                                            else if (month == 8){
                                                tempmonth = "SEPTEMBER " + "WEEK: " + week;
                                            }
                                            else if (month == 9){
                                                tempmonth = "OCTOBER " + "WEEK: " + week;
                                            }
                                            else if (month == 10){
                                                tempmonth = "NOVEMBER " + "WEEK: " + week;
                                            }
                                            else if (month == 11){
                                                tempmonth = "DECEMBER " + "WEEK: " + week;
                                            }
                                            if (tempmonth.equals(update.getTime())){
                                                dlogs1.add(updates);
                                            }
                                        }
                                    }
                                });
                                final LayoutInflater inflater2 = LayoutInflater.from(getContext());
                                View v2 = inflater2.inflate(R.layout.custom_view_daily, null);
                                RecyclerView recyclerView = v2.findViewById(R.id.recyclerViewTime);
                                updatesRecyclerAdapter = new UpdatesRecyclerAdapter(getContext(), dlogs1);
                                recyclerView.setAdapter(updatesRecyclerAdapter);
                                final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                        .setView(v2)
                                        .create();
                                alertDialog.show();
                            }
                        });
                        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setView(v3)
                                .create();
                        alertDialog.show();

                    }
                });
            }
        });
        btnMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        dlogs.clear();
                        ddate.clear();
                        dlogs1.clear();
                        for (DocumentSnapshot documentSnapshot:documentSnapshots){
                            LogClass logClass = documentSnapshot.toObject(LogClass.class);
                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            String date = dateFormat.format(logClass.getValue());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(logClass.getValue());
                            int month = cal.get(Calendar.MONTH);
                            //MONTH CONVERSION
                                if (month == 0) {
                                    date = "JANUARY";
                                }
                                else if (month == 1) {
                                    date = "FEBUARY";
                                }
                                else if (month == 2){
                                    date = "MARCH";
                                }
                                else if (month == 3){
                                    date = "APRIL";
                                }
                                else if (month == 4){
                                    date = "MAY";
                                }
                                else if (month == 5){
                                    date = "JUNE";
                                }
                                else if (month == 6){
                                    date = "JULY";
                                }
                                else if (month == 7){
                                    date = "AUGUST";
                                }
                                else if (month == 8){
                                    date = "SEPTEMBER";
                                }
                                else if (month == 9){
                                    date = "OCTOBER";
                                }
                                else if (month == 10){
                                    date = "NOVEMBER";
                                }
                                else if (month == 11){
                                    date = "DECEMBER";
                                }
                            Updates update = new Updates(date, logClass.getPrice());
                            if (!ddate.contains(date)){
                                ddate.add(date);
                                dlogs.add(update);
                            }
                            else{
                                for(int i = 0; i <dlogs.size(); i++){
                                    Updates updates1 = dlogs.get(i);
                                    String araw = update.getTime();
                                    String araw1 = updates1.getTime();
                                    if (araw1.equals(araw)){
                                        updates1.setPrice(update.getPrice() + updates1.getPrice());
                                    }
                                }
                            }
                        }
                        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                        View v3 = layoutInflater.inflate(R.layout.custom_view_daily,null);
                        RecyclerView recyclerView = v3.findViewById(R.id.recyclerViewTime);
                        updatesRecyclerAdapter = new UpdatesRecyclerAdapter(getContext(),dlogs);
                        recyclerView.setAdapter(updatesRecyclerAdapter);
                        updatesRecyclerAdapter.setOnItemClickListener(new UpdatesRecyclerAdapter.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position, Updates update) {
                                logref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                                        dlogs1.clear();
                                        for (DocumentSnapshot snapshot:documentSnapshots) {
                                            LogClass logClass = snapshot.toObject(LogClass.class);
                                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                            String date = dateFormat.format(logClass.getValue());
                                            Updates updates = new Updates(date, logClass.getPrice());
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(logClass.getValue());
                                            int month = cal.get(Calendar.MONTH);
                                            String tempmonth = "";
                                            //MONTH CONVERSION
                                            if (month == 0) {
                                                tempmonth = "JANUARY";
                                            }
                                            else if (month == 1) {
                                                tempmonth = "FEBUARY";
                                            }
                                            else if (month == 2){
                                                tempmonth = "MARCH";
                                            }
                                            else if (month == 3){
                                                tempmonth = "APRIL";
                                            }
                                            else if (month == 4){
                                                tempmonth = "MAY";
                                            }
                                            else if (month == 5){
                                                tempmonth = "JUNE";
                                            }
                                            else if (month == 6){
                                                tempmonth = "JULY";
                                            }
                                            else if (month == 7){
                                                tempmonth = "AUGUST";
                                            }
                                            else if (month == 8){
                                                tempmonth = "SEPTEMBER";
                                            }
                                            else if (month == 9){
                                                tempmonth = "OCTOBER";
                                            }
                                            else if (month == 10){
                                                tempmonth = "NOVEMBER";
                                            }
                                            else if (month == 11){
                                                tempmonth = "DECEMBER";
                                            }
                                            if (tempmonth.equals(update.getTime())){
                                                dlogs1.add(updates);
                                            }
                                        }
                                    }
                                });
                                final LayoutInflater inflater2 = LayoutInflater.from(getContext());
                                View v2 = inflater2.inflate(R.layout.custom_view_daily, null);
                                RecyclerView recyclerView = v2.findViewById(R.id.recyclerViewTime);
                                updatesRecyclerAdapter = new UpdatesRecyclerAdapter(getContext(), dlogs1);
                                recyclerView.setAdapter(updatesRecyclerAdapter);
                                final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                        .setView(v2)
                                        .create();
                                alertDialog.show();
                            }
                        });
                        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setView(v3)
                                .create();
                        alertDialog.show();

                    }
                });
            }
        });
        return v;
    }

    @Override
    public void showLog(DocumentSnapshot documentSnapshot) {
        final LogClass log = documentSnapshot.toObject(LogClass.class);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.custom_view_receipt,null);
        final TextView dateTime = v.findViewById(R.id.dateTime);
        final TextView logTotal = v.findViewById(R.id.logTotal);
        final RecyclerView logRecycler = v.findViewById(R.id.logRecycler);
        logRecycler.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        dateTime.setText(log.getValue().toString());
        logTotal.setText(String.valueOf(log.getPrice()));
        Query query = logref.document(log.getValue().toString()).collection(log.getValue().toString());
        FirestoreRecyclerOptions<nInventory> options = new FirestoreRecyclerOptions.Builder<nInventory>()
                .setQuery(query, nInventory.class)
                .build();
        FinanceRecyclerAdapter financeRecyclerAdapter = new FinanceRecyclerAdapter(options);
        logRecycler.setAdapter(financeRecyclerAdapter);
        financeRecyclerAdapter.startListening();
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .create();
        alertDialog.show();
    }

}
