package com.example.movielateandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class WelcomeScreen extends AppCompatActivity implements LearningSystem  {
    Button setOfNewFlashcardsBtn, makeReviewOfFlashcardsBtn;
    TextView streakDaysTv, lastLogDayTv, flashcardsToRepeatTV;
    boolean doubleBackToExitPressedOnce = false;
    LeitnerSystem leitnerSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        setOfNewFlashcardsBtn = findViewById(R.id.newBtn);
        makeReviewOfFlashcardsBtn = findViewById(R.id.revisionBtn);
        streakDaysTv = findViewById(R.id.dayTV);
        lastLogDayTv = findViewById(R.id.todayTV);
        flashcardsToRepeatTV = findViewById(R.id.flashcardsToRepeatTV);

        leitnerSystem = new LeitnerSystem(this);
        setOfNewFlashcardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("option", 0);
                startActivity(intent);
                finish();
            }
        });
        makeReviewOfFlashcardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("option", 1);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.finish();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit",
                Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1500);
    }


    @Override
    public void lastLogDate(String date) {
        lastLogDayTv.setText(String.valueOf(date));
    }

    @Override
    public void consecutiveDays(int days) {
        streakDaysTv.setText(String.valueOf(days));
    }

    @Override
    public void flashcards(ArrayList arrayList) {
        flashcardsToRepeatTV.setText(String.valueOf(arrayList.size()));
        makeReviewOfFlashcardsBtn.setVisibility(View.VISIBLE);
    }
}
