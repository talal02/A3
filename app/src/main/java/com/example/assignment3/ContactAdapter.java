package com.example.assignment3;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private ArrayList<User> users;
    private Context c;
    private User currentUser;

    public ContactAdapter(ArrayList<User> users, Context c, User currentUser) {
        this.users = users;
        this.c = c;
        this.currentUser = currentUser;
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
                Intent i = new Intent(c, MessageListActivity.class);
                Gson gson = new Gson();
                String userJson = gson.toJson(user);
                String currentUserJson = gson.toJson(currentUser);
                i.putExtra("you_user", userJson);
                i.putExtra("current_user", currentUserJson);
                c.startActivity(i);
            }
        });
    }

    private void checkIfChatExists() {

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
