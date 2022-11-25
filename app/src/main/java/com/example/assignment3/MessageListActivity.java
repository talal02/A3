package com.example.assignment3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MessageListActivity extends AppCompatActivity{
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    MaterialTextView name;
    MaterialTextView lastSeen;
    ShotWatch mShotWatch;
    EditText sendMsg;
    View online_view;
    RoundedImageView img;
    private ArrayList<Message> mMessageList;
    private User youUser;
    private User currentUser;
    FloatingActionButton sendBtn;
    FloatingActionButton addImage;
    ArrayList<String> images;
    private String lastMessage = "";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list);
        name=findViewById(R.id.curr_name);
        img=findViewById(R.id.curr_img);
        lastSeen=findViewById(R.id.last_seen);
        sendBtn = findViewById(R.id.button_gchat_send);
        sendMsg = findViewById(R.id.edit_gchat_message);
        addImage = findViewById(R.id.button_gchat_img_send);
        online_view = findViewById(R.id.online_status);
        mMessageRecycler = (RecyclerView) findViewById(R.id.recycler_gchat);
        images = new ArrayList<>();
        Gson gson = new Gson();
        currentUser = gson.fromJson(getIntent().getStringExtra("current_user"), User.class);
        youUser = gson.fromJson(getIntent().getStringExtra("you_user"), User.class);
        Date date = new Date(youUser.getLastSeen()*1000);
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("MMM dd | HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormatted = formatter.format(date);
        lastSeen.setText(dateFormatted);
        name.setText(youUser.getName());
        Glide.with(MessageListActivity.this).load(youUser.getPhoto()).into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageListActivity.this, ViewDp.class);
                intent.putExtra("photo", youUser.getPhoto());
                startActivity(intent);
            }
        });
        mShotWatch = new ShotWatch(getContentResolver(), new ShotWatch.Listener() {
            @Override
            public void onScreenShotTaken(ScreenshotData screenshotData) {
                if(!Objects.equals(lastMessage, "Your Friend Took Screenshot of Chat :)")) {
                    sendMessage("Your Friend Took Screenshot of Chat :)", false, false);
                    lastMessage = "Your Friend Took Screenshot of Chat :)";
                }
            }
        });
        getChats();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sendMsg.getText().toString().length()>0){
                    for(int i = 0; i < images.size(); i++){
                        sendMessage(images.get(i), true, false);
                    }
                    sendMessage(sendMsg.getText().toString(), false, false);
                    lastMessage = "";
                    images.clear();
                    sendMsg.setText("");
                } else {
                    Toast.makeText(MessageListActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 99);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShotWatch.register();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                RequestQueue queue = Volley.newRequestQueue(MessageListActivity.this);
                String url = getString(R.string.ip_address) + "/chat_app/get_via_phone.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getInt("code") == 1) {
                                JSONObject tempUser;
                                try {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    tempUser = jsonArray.getJSONObject(0);
                                } catch (Exception e) {
                                    tempUser = jsonObject.getJSONObject("data");
                                }
                                Long lastSeen = tempUser.getLong("lastSeen");
                                Timestamp timestampAfter = new Timestamp(System.currentTimeMillis() + 60000);
                                Timestamp timestampBefore = new Timestamp(System.currentTimeMillis() - 120000);
                                Timestamp seen = new Timestamp(lastSeen*1000);
                                if(seen.after(timestampBefore) && seen.before(timestampAfter)) {
                                    online_view.setBackgroundColor(Color.argb(255, 0, 255, 0));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                    Log.d("error", error.toString());
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("phone", youUser.getPhno());
                        return params;
                    }
                };
                queue.add(stringRequest);
            }
        }, 0, 10000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShotWatch.unregister();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 99 && resultCode == RESULT_OK && data != null){
            images = new ArrayList<>();
            if(data.getClipData() != null){
                int count = data.getClipData().getItemCount();
                images = new ArrayList<>();
                for(int i = 0; i < count; i++){
                    try {
                        Uri filePath = data.getClipData().getItemAt(i).getUri();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageInByte = baos.toByteArray();
                        images.add(Base64.encodeToString(imageInByte, Base64.DEFAULT));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                Toast.makeText(this, "Images added", Toast.LENGTH_SHORT).show();
            } else if(data.getData() != null){
                try {
                    Uri filePath = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageInByte = baos.toByteArray();
                    images.add(Base64.encodeToString(imageInByte, Base64.DEFAULT));
                    Toast.makeText(this, "Images added", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMessage(String message, boolean isPhoto, boolean isVoice) {
        String url = getString(R.string.ip_address) + "/chat_app/send_chats.php";
        RequestQueue queue = Volley.newRequestQueue(MessageListActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getInt("code") == 1) {
                            Toast.makeText(MessageListActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            if(jsonObject.getInt("photo") == 0) {
                                getChats();
                            }
                        } else {
                            Toast.makeText(MessageListActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MessageListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.d("error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("e1", currentUser.getEmail());
                params.put("e2", youUser.getEmail());
                params.put("msg", message);
                params.put("sender", currentUser.getEmail());
                if(isPhoto) {
                    params.put("isPhoto", "1");
                } else {
                    params.put("isPhoto", "0");
                }
                if(isVoice) {
                    params.put("isVoice", "1");
                } else {
                    params.put("isVoice", "0");
                }
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void getChats() {
        mMessageList = new ArrayList<>();
        String url = getString(R.string.ip_address) + "/chat_app/get_chats.php";
        RequestQueue queue = Volley.newRequestQueue(MessageListActivity.this);
        @SuppressLint("NotifyDataSetChanged") StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject= new JSONObject(response);
                if(jsonObject.getInt("code")==1){
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int x = 0; x < jsonArray.length(); x++) {
                        JSONObject object = jsonArray.getJSONObject(x);
                        if(object.getString("sender").equals(currentUser.getEmail())) {
                            mMessageList.add(new Message(object.getString("message"), currentUser, object.getLong("createdAt"), object.getInt("isPhoto") != 0, object.getInt("isVoice") != 0));
                        } else {
                            mMessageList.add(new Message(object.getString("message"), youUser, object.getLong("createdAt"), object.getInt("isPhoto") != 0, object.getInt("isVoice") != 0));
                        }
                    }
                    mMessageAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MessageListActivity.this, "No chats found", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("error", error.toString());
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("e1", currentUser.getEmail());
                params.put("e2", youUser.getEmail());
                return params;
            }
        };
        queue.add(request);
        mMessageAdapter = new MessageListAdapter(MessageListActivity.this, mMessageList, currentUser, youUser);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(MessageListActivity.this));
        mMessageRecycler.setAdapter(mMessageAdapter);
    }

}
