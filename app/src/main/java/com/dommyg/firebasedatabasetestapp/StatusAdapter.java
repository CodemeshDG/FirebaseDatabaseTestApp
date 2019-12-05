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

    static class StatusViewHolder extends RecyclerView.ViewHolder {
        TextView textViewStatus;

        StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }

    StatusAdapter(ArrayList<StatusItem> statusItemList) {
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
        StatusItem currentItem = statusItemList.get(position);
        holder.textViewStatus.setText(currentItem.getStatus());
    }

    @Override
    public int getItemCount() {
        return statusItemList.size();
    }

}
