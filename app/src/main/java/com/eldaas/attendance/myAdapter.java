package com.eldaas.attendance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.List;

public class myAdapter extends FirebaseRecyclerAdapter<ModelClass, myAdapter.myViewHolder> {

    public myAdapter(@NonNull FirebaseRecyclerOptions<ModelClass> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ModelClass model) {
        holder.Name.setText("Name: "+model.getName());
        holder.ID.setText("ID: "+model.getID());
        holder.Time.setText("InTime: "+model.getInTime());
        holder.OutTime.setText("OutTime: "+model.getOutTime());
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new myViewHolder(view);
    }

    static class myViewHolder extends RecyclerView.ViewHolder {

        TextView Name, ID, Time,OutTime;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.nameDisplay);
            ID = itemView.findViewById(R.id.collegeIdDisplay);
            Time = itemView.findViewById(R.id.timeDisplay);
            OutTime = itemView.findViewById(R.id.outtimeDisplay);

        }
    }

}