package com.example.assignment3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private ArrayList<User> users;
    private Context c;

    public ContactAdapter(ArrayList<User> users, Context c) {
        this.users = users;
        this.c = c;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<User> filterlist) {
        users = filterlist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getName());
        holder.lastMsg.setText("BEEEE");
        Glide.with(c).load(user.getPhoto()).into(holder.profile);
        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(c, "Message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView profile;
        private TextView name, lastMsg;
        LinearLayout message;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.img_dp);
            name = itemView.findViewById(R.id.display_name);
            lastMsg = itemView.findViewById(R.id.last_text);
            message = itemView.findViewById(R.id.open_message);
        }
    }
}
