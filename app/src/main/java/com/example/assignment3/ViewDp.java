package com.example.assignment3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ViewDp extends AppCompatActivity {

    ImageView iv;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dp);
        iv=findViewById(R.id.profileView);
        Glide.with(ViewDp.this).load(getIntent().getStringExtra("photo")).into(iv);
    }
}