package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    TextInputLayout email, password;
    Button login;
    TextView tsu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginBtn);
        tsu = findViewById(R.id.toSignup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = email.getEditText().getText().toString();
                String pass = password.getEditText().getText().toString();
                if (mail.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    String url = "http://192.168.100.2:8080/chat_app/login.php";
                    RequestQueue queue = Volley.newRequestQueue(Login.this);
                    StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getInt("code") == 1) {
                                JSONObject tempUser;
                                try {
                                    JSONArray jsonArray = object.getJSONArray("data");
                                    tempUser = jsonArray.getJSONObject(0);
                                } catch (Exception e) {
                                    tempUser = object.getJSONObject("data");
                                }
                                Intent i = new Intent(Login.this, MainScreen.class);
                                Gson gson = new Gson();
                                String photoURL = "http://192.168.100.2:8080/chat_app/" + tempUser.getString("photo");
                                String userJson = gson.toJson(new User(tempUser.getString("fullname"), tempUser.getString("email"), tempUser.getString("phno"), photoURL));
                                i.putExtra("currentUser", userJson);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(Login.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("email", mail);
                            params.put("password", pass);
                            return params;
                        }
                    };
                    queue.add(request);
                }
            }
        });

    }
}