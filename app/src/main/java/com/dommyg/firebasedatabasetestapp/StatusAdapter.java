package com.dommyg.firebasedatabasetestapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {
    private ArrayList<StatusItem> statusItemList;
    private MainPanelFragment mainPanelFragment;

    static class StatusViewHolder extends RecyclerView.ViewHolder {
        TextView textViewStatus;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }

    StatusAdapter(MainPanelFragment mainPanelFragment, ArrayList<StatusItem> statusItemList) {
        this.mainPanelFragment = mainPanelFragment;
        this.statusItemList = statusItemList;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_status, parent, false);
        return new StatusViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        // TODO: Update task from database.
    }

    @Override
    public int getItemCount() {
        return statusItemList.size();
    }

//    private String updateStatus() {
//        // TODO: Update task from database.
//    }
}
