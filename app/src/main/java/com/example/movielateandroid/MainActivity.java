package com.example.movielateandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements PLListener, UserAnswer {
    ProgressBar progressBar;
    TextView currentFlashcard, toLearn;
    RatingBar ratingBar;
    int numberOfCurrentFlashcard = 0;
    int progressbarNow;
    ArrayList<Flashcard> done = new ArrayList<>();
    ArrayList<Flashcard> wrong = new ArrayList<>();
    ArrayList<Flashcard> flashcards;
    double size;
    boolean doubleBackToExitPressedOnce = false;
    LeitnerSystem leitnerSystem;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate (Bundle savedInstanceStats){
        super.onCreate(savedInstanceStats);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        currentFlashcard = findViewById(R.id.currentTv);
        toLearn = findViewById(R.id.toLearnTv);
        ratingBar = findViewById(R.id.rating);

        Bundle bundle = getIntent().getExtras();
        int option = bundle.getInt("option");
        leitnerSystem = new LeitnerSystem(new LearningSystem() {
            @Override
            public void lastLogDate(String date) {

            }

            @Override
            public void consecutiveDays(int days) {

            }

            @Override
            public void flashcards(ArrayList arrayList) {
                flashcards = arrayList;
                size = (double) flashcards.size();
                if (arrayList.size()>0){
                    updateProgressbar();
                    toLearn.setText(String.valueOf(flashcards.size()));
                    executeEngFlashcard();
                } else {
                    Toast.makeText(getApplicationContext(), "Brak fiszek na dzisiaj", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), WelcomeScreen.class);
                    startActivity(intent);
                    finish();
                }
            }
        },option);
    }

    @Override
    public void onClickAnswer() {
        executePLFlashcard();
    }

    @Override
    public void answer(Boolean knew) {
        if (knew==true && numberOfCurrentFlashcard<flashcards.size()) {
            updateProgressbar();
            done.add(flashcards.get(numberOfCurrentFlashcard));
            checkBeforeCallingNextFlashcard();
            checkBeforeBackToWelcomeScreen();
        }
        else if (knew==false && numberOfCurrentFlashcard<flashcards.size()) {
            updateProgressbar();
            wrong.add(flashcards.get(numberOfCurrentFlashcard));
            checkBeforeCallingNextFlashcard();
            checkBeforeBackToWelcomeScreen();
        }
    }

    private void checkBeforeBackToWelcomeScreen() {
        if (numberOfCurrentFlashcard == flashcards.size()) {
            updateDataAndExit();
        }
    }

    private void updateDataAndExit() {
        leitnerSystem.updateFlashcards(done, wrong);
        Intent intent = new Intent(this, WelcomeScreen.class);
        startActivity(intent);
        this.finish();
    }

    private void checkBeforeCallingNextFlashcard() {
        if (numberOfCurrentFlashcard < flashcards.size() - 1) {
            numberOfCurrentFlashcard++;
            updateProgressbar();
            executeEngFlashcard();
        } else numberOfCurrentFlashcard++;
    }

    private void updateProgressbar() {
        progressbarNow = numberOfCurrentFlashcard+1;
        currentFlashcard.setText(String.valueOf(progressbarNow));
        progressBar.setProgress((int) ((progressbarNow)/size*100));
        ratingBar.setRating(Float.valueOf(flashcards.get(numberOfCurrentFlashcard).getCategory()));
    }

    private void executeEngFlashcard() {
        EngFragment engFragment = new EngFragment(this, flashcards.get(numberOfCurrentFlashcard).getEng());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_place, engFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void executePLFlashcard() {
        PLFragment fr = new PLFragment(this, flashcards.get(numberOfCurrentFlashcard).getPl());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_place,fr);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            leitnerSystem.updateFlashcards(done, wrong);
            this.finish();
            Intent intent = new Intent(getApplicationContext(), WelcomeScreen.class);
            startActivity(intent);
        }
        executeEngFlashcard();
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit",
                Toast.LENGTH_SHORT).show();

    }
}
