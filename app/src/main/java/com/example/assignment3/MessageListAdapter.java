package com.example.assignment3;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MessageListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<Message> mMessageList;
    private User currentUser;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private User youUser;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public MessageListAdapter(Context context, ArrayList<Message> messageList, User currentUser, User youUser_) {
        mContext = context;
        this.currentUser = currentUser;
        mMessageList = messageList;
        youUser = youUser_;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = (Message) mMessageList.get(position);
        if (message.getSender().getEmail().equals(currentUser.getEmail())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_me, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_you, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, dateText;
        ImageView iv;
        LinearLayout ll;

        SentMessageHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.layout_gchat_container_me);
            dateText = (TextView) itemView.findViewById(R.id.text_gchat_date_me);
            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_me);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_me);
            iv = (ImageView) itemView.findViewById(R.id.myImageView);
        }

        @SuppressLint("SimpleDateFormat")
        void bind(Message message) {
            Date date = new Date(message.getCreatedAt()*1000);
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateFormatted = formatter.format(date);
            timeText.setText(dateFormatted);

            formatter = new SimpleDateFormat("MMM dd");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateFormatted = formatter.format(date);
            dateText.setText(dateFormatted);

            if(message.getIsPhoto()) {
                iv.setMaxHeight(70);
                iv.setMaxWidth(70);
                Glide.with(mContext).load(mContext.getString(R.string.ip_address) + "/chat_app/" + message.getMessage()).into(iv);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ViewDp.class);
                        intent.putExtra("photo", mContext.getString(R.string.ip_address) + "/chat_app/" + message.getMessage());
                        mContext.startActivity(intent);
                    }
                });
                messageText.setText("");
            } else {
                messageText.setText(message.getMessage());
                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Edit Message");
                        final EditText input = new EditText(mContext);
                        input.setText(message.getMessage());
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);
                        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String text = input.getText().toString();
                                message.setMessage(text);
                                String url = mContext.getString(R.string.ip_address) + "/chat_app/update_chats.php";
                                RequestQueue queue = Volley.newRequestQueue(mContext);
                                @SuppressLint("NotifyDataSetChanged") StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if(jsonObject.getInt("code") == 1) {
                                            Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                            notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, error -> {
                                    Log.d("Error", error.toString());
                                }) {
                                    @Override
                                    protected java.util.Map<String, String> getParams() {
                                        java.util.Map<String, String> params = new java.util.HashMap<>();
                                        params.put("e1", currentUser.getEmail());
                                        params.put("e2", youUser.getEmail());
                                        params.put("msg", message.getMessage());
                                        params.put("time", Long.toString(message.getCreatedAt()));
                                        return params;
                                    }
                                };
                                queue.add(request);
                            }
                        });
                        builder.show();
                    }
                });
            }
            ll.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String url = mContext.getString(R.string.ip_address) + "/chat_app/delete_chats.php";
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getInt("code") == 1) {
                                Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("Error", error.toString());
                    }) {
                        @Override
                        protected java.util.Map<String, String> getParams() {
                            java.util.Map<String, String> params = new java.util.HashMap<>();
                            params.put("e1", currentUser.getEmail());
                            params.put("e2", youUser.getEmail());
                            params.put("time", Long.toString(message.getCreatedAt()));
                            return params;
                        }
                    };
                    queue.add(request);
                    return true;
                }
            });
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;
        TextView dateText;
        ImageView iv;
        LinearLayout ll;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            ll = (LinearLayout) itemView.findViewById(R.id.layout_gchat_container_other);
            dateText = (TextView) itemView.findViewById(R.id.text_gchat_date_other);
            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_other);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_other);
            nameText = (TextView) itemView.findViewById(R.id.text_gchat_user_other);
            profileImage = (ImageView) itemView.findViewById(R.id.image_gchat_profile_other);
            iv = (ImageView) itemView.findViewById(R.id.myImageView);
        }

        void bind(Message message) {
            Date date = new Date(message.getCreatedAt()*1000);
            @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateFormatted = formatter.format(date);
            timeText.setText(dateFormatted);

            formatter = new SimpleDateFormat("MMM dd");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateFormatted = formatter.format(date);
            dateText.setText(dateFormatted);
            nameText.setText(message.getSender().getName());
            Glide.with(mContext).load(message.getSender().getPhoto()).into(profileImage);

            if(message.getIsPhoto()) {
                iv.setMaxHeight(70);
                iv.setMaxWidth(70);
                Glide.with(mContext).load(mContext.getString(R.string.ip_address) + "/chat_app/" + message.getMessage()).into(iv);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ViewDp.class);
                        intent.putExtra("photo", mContext.getString(R.string.ip_address) + "/chat_app/" + message.getMessage());
                        mContext.startActivity(intent);
                    }
                });
                messageText.setText("");
            } else {
                messageText.setText(message.getMessage());
            }

        }
    }
}