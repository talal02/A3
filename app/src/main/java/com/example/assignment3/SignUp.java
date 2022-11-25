package com.example.assignment3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    TextInputLayout name, email, password, phno;
    MaterialButton addPhoto;
    Button Signup;
    TextView toLogin;
    private String selectedPicture = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phno = findViewById(R.id.phno);
        addPhoto = findViewById(R.id.photo);
        toLogin = findViewById(R.id.toLogin);
        Signup = findViewById(R.id.signupBtn);

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 1);
            }
        });

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getEditText().getText().toString().isEmpty()) {
                    name.setError("Name is required");
                } else if (email.getEditText().getText().toString().isEmpty()) {
                    email.setError("Email is required");
                } else if (password.getEditText().getText().toString().isEmpty()) {
                    password.setError("Password is required");
                } else if (phno.getEditText().getText().toString().isEmpty()) {
                    phno.setError("Phone number is required");
                } else {
                    String url = getString(R.string.ip_address) + "/chat_app/signup.php";
                    RequestQueue queue = Volley.newRequestQueue(SignUp.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("PIC", response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("code").equals("1")) {
                                    Toast.makeText(SignUp.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(SignUp.this, Login.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(SignUp.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(SignUp.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @NonNull
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("fullname", name.getEditText().getText().toString());
                            params.put("email", email.getEditText().getText().toString());
                            params.put("password", password.getEditText().getText().toString());
                            params.put("phno", phno.getEditText().getText().toString());
                            params.put("photo", selectedPicture);
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageInByte = baos.toByteArray();
                selectedPicture = Base64.encodeToString(imageInByte, Base64.DEFAULT);
                Toast.makeText(getApplicationContext(), "Image Selected", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Can't Add Image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}