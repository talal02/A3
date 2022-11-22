package com.example.assignment3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainScreen extends AppCompatActivity {
    private RecyclerView rv;
    private ContactAdapter contactAdapter;
    private ArrayList<User> users;
    private ArrayList<String> addedUsers;
    private User currentUser;
    MaterialTextView tv;
    RoundedImageView riv;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        rv = findViewById(R.id.contacts);
        tv=findViewById(R.id.user_name);
        riv=findViewById(R.id.user_img);
        Gson gson = new Gson();
        currentUser = gson.fromJson(getIntent().getStringExtra("currentUser"), User.class);
        tv.setText(currentUser.getName());
        Glide.with(this).load(currentUser.getPhoto()).into(riv);
        riv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, ViewDp.class);
                intent.putExtra("photo", currentUser.getPhoto());
                startActivity(intent);
            }
        });
        requestContactPermission();
        requestOtherPermission();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        return true;
    }

    private void filter(String filter_text) {
        ArrayList<User> filteredList = new ArrayList<>();
        for (User user : users) {
            if (user.getName().toLowerCase().contains(filter_text.toLowerCase())) {
                filteredList.add(user);
            }
        }
        if(filteredList.isEmpty()) {
            Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show();
        } else {
            contactAdapter.filterList(filteredList);
        }
    }

    public void getContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        addedUsers = new ArrayList<>();
        if(cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                @SuppressLint("Range") int hasPhoneNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if(hasPhoneNo > 0) {
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    @SuppressLint("Range") String Name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if(phoneCursor.moveToNext()) {
                        @SuppressLint("Range") String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber = phoneNumber.replace(" ", "");
                        phoneNumber = phoneNumber.replace("-", "");
                        phoneNumber = phoneNumber.replace("+", "");
                        if(phoneNumber.charAt(0) == '9') {
                            phoneNumber = "0" + phoneNumber.substring(2);
                        }
                        String url = "http://192.168.100.2:8080/chat_app/get_via_phone.php";
                        RequestQueue queue = Volley.newRequestQueue(MainScreen.this);
                        String finalPhoneNumber = phoneNumber;
                        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
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
                                    String photoURL = "http://192.168.100.2:8080/chat_app/" + tempUser.getString("photo");
                                    if(!addedUsers.contains(tempUser.getString("phno"))) {
                                        addedUsers.add(tempUser.getString("phno"));
                                        if(tempUser.getString("email").equals(currentUser.getEmail())) {
                                            Log.d("C", "CURRENT USER");
                                        } else {
                                            users.add(new User(tempUser.getString("fullname"), tempUser.getString("email"), tempUser.getString("phno"), photoURL));
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                            Toast.makeText(MainScreen.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("phone", finalPhoneNumber);
                                return params;
                            }
                        };
                        queue.add(request);
                    }
                }
            }
        }
    }

    private void buildRecyclerView() {
        users = new ArrayList<>();
        getContacts();
        contactAdapter = new ContactAdapter(users, MainScreen.this, currentUser);
        rv.setLayoutManager(new LinearLayoutManager(MainScreen.this));
        rv.setAdapter(contactAdapter);
    }

    public void requestContactPermission() {
        if(ContextCompat.checkSelfPermission(MainScreen.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainScreen.this, Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(MainScreen.this, new String[] {Manifest.permission.READ_CONTACTS}, 1);
            } else {
                ActivityCompat.requestPermissions(MainScreen.this, new String[] {Manifest.permission.READ_CONTACTS}, 1);
            }
        } else {
            buildRecyclerView();
        }
    }

    public void requestOtherPermission() {
        if(ContextCompat.checkSelfPermission(MainScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainScreen.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            } else {
                ActivityCompat.requestPermissions(MainScreen.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buildRecyclerView();
            }
        }
    }
}