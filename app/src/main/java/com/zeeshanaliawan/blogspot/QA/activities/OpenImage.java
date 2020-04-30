package com.zeeshanaliawan.blogspot.QA.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;
import com.zeeshanaliawan.blogspot.QA.R;

public class OpenImage extends AppCompatActivity {
    ZoomageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);
        img = findViewById(R.id.myZoomageView);
        Intent intent = getIntent();
        String a = intent.getStringExtra("img");
        Glide.with(this).load(a).into(img);
    }
}
