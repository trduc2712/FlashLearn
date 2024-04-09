package com.trduc.flashlearn.Controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trduc.flashlearn.R;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth auth;
    SharedPreferences sharedPreferences;
    FirebaseDatabase database;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
//                boolean isRemembered = sharedPreferences.getBoolean("isChecked", false);
//
//                if (isRemembered) {
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//
//                } else {
//                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        }, 650);
        ImageSlider imageSlider = findViewById(R.id.imageslider);

        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.flashcard1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.flashcard2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.flashcard3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.flashcard4, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.flashcard5, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        Button btn_start = findViewById(R.id.bt_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}