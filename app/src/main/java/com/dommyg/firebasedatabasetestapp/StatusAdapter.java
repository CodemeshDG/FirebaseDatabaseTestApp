package com.dommyg.firebasedatabasetestapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Adapter for recyclerView provided by FirestoreUI. Displays user statuses.
 */
public class StatusAdapter extends FirestoreRecyclerAdapter<StatusItem, StatusAdapter.StatusViewHolder> {

    static class StatusViewHolder extends RecyclerView.ViewHolder {
        TextView textViewStatus;

        StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }

    StatusAdapter(@NonNull FirestoreRecyclerOptions<StatusItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull StatusViewHolder holder, int position,
                                    @NonNull StatusItem model) {
        holder.textViewStatus.setText(model.getStatus());
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_status, parent, false);
        return new StatusViewHolder(v);
    }

}
