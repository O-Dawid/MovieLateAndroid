package com.example.movielateandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PLFragment extends Fragment {
    Button knewBtn, iDidntKnowBtn;
    Boolean knew;
    TextView plTV;
    UserAnswer UserAnswer;
    String pl;

    public PLFragment(){}

    public PLFragment(UserAnswer UserAnswer, String pl) {
        this.UserAnswer = UserAnswer;
        this.pl = pl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_answer,container,false);
        plTV = (TextView)v.findViewById(R.id.secondSideOfFlashcard);
        knewBtn = (Button)v.findViewById(R.id.iKnewBtn);
        iDidntKnowBtn = (Button)v.findViewById(R.id.iDidntKnewBtn);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        plTV.setText(pl);

        knewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knew = true;
                UserAnswer.answer(knew);
            }
        });
        iDidntKnowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knew = false;
                UserAnswer.answer(knew);
            }
        });
    }
}
