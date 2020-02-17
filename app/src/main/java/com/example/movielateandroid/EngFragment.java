package com.example.movielateandroid;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EngFragment extends Fragment {
    Button answerBtn;
    TextView engText;
    PLListener PLListener;
    String eng;

    public EngFragment(){}

    public EngFragment(PLListener PLListener, String eng) {
        this.PLListener = PLListener;
        this.eng = eng;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question,container,false);
        answerBtn = (Button)v.findViewById(R.id.answer_btn);
        engText = (TextView)v.findViewById(R.id.firstSideOfFlashcardTV);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        engText.setText(eng);
        answerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PLListener.onClickAnswer();

            }
        });
    }
}
