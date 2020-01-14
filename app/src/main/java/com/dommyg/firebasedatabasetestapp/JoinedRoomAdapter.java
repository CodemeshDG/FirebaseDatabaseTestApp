package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class JoinedRoomAdapter extends FirestoreRecyclerAdapter<JoinedRoomItem,
        JoinedRoomAdapter.JoinedRoomViewHolder> {
    private Resources resources;
    private MainMenuFragment mainMenuFragment;

    static class JoinedRoomViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRoomName;
        ImageView imageViewRoomStatus;
        ImageView imageViewLeaveRoom;

        JoinedRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewRoomName = itemView.findViewById(R.id.textViewRoomName);
            this.imageViewRoomStatus = itemView.findViewById(R.id.imageViewRoomStatus);
            this.imageViewLeaveRoom = itemView.findViewById(R.id.imageViewLeaveRoom);
        }
    }

    JoinedRoomAdapter(@NonNull FirestoreRecyclerOptions<JoinedRoomItem> options, Resources resources,
                      MainMenuFragment mainMenuFragment) {
        super(options);
        this.resources = resources;
        this.mainMenuFragment = mainMenuFragment;
    }

    @Override
    protected void onBindViewHolder(@NonNull JoinedRoomViewHolder holder, int position,
                                    @NonNull JoinedRoomItem model) {
        holder.textViewRoomName.setText(model.getRoomName());
        if (model.getIsOwner()) {
            holder.imageViewRoomStatus.setImageDrawable(resources.getDrawable(R.drawable.ic_owner_black_24dp));
        }

        final String roomName = model.getRoomName();
        final String password = model.getPassword();

        holder.textViewRoomName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new RoomController(mainMenuFragment.getUsername(), password, roomName,
                        mainMenuFragment.getContext()).joinRoom();
            }
        });
    }

    @NonNull
    @Override
    public JoinedRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_joined_room, parent, false);
        return new JoinedRoomViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }
}
