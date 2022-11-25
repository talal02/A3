package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
    TextView tsu, forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginBtn);
        tsu = findViewById(R.id.toSignup);
        forgot = findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getEditText().getText().toString().isEmpty()) {
                    email.setError("Enter Email");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setTitle("Forgot Password");
                    RequestQueue queue = Volley.newRequestQueue(Login.this);
                    String url = getString(R.string.ip_address) + "/chat_app/forgot_password.php";
                    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.getInt("code") == 1) {
                                    builder.setMessage(object.getString("msg")).setCancelable(true).show();
                                } else {
                                    Toast.makeText(Login.this, "Email not found", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(Login.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("email", email.getEditText().getText().toString());
                            return params;
                        }
                    };
                    queue.add(request);
                }
            }
        });

        tsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = email.getEditText().getText().toString();
                String pass = password.getEditText().getText().toString();
                if (mail.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    String url = getString(R.string.ip_address) + "/chat_app/login.php";
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
                                String photoURL = getString(R.string.ip_address) + "/chat_app/" + tempUser.getString("photo");
                                String userJson = gson.toJson(new User(tempUser.getString("fullname"), tempUser.getString("email"), tempUser.getString("phno"), photoURL, tempUser.getLong("lastSeen")));
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