package com.trduc.flashlearn.Adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.trduc.flashlearn.Models.Flashcard;
import com.trduc.flashlearn.R;

import java.util.ArrayList;

public class AlreadyCreateAdapter extends BaseAdapter {

    final ArrayList<Flashcard> flashcardList;

    public AlreadyCreateAdapter(ArrayList<Flashcard> flashcardList) {
        this.flashcardList = flashcardList;
    }

    @Override
    public int getCount() {
        return flashcardList.size();
    }

    @Override
    public Object getItem(int position) {
        return flashcardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = View.inflate(parent.getContext(), R.layout.already_create, null);
        } else {
            view = convertView;
        }

        Flashcard flashcard = (Flashcard) getItem(position);
        ((EditText) view.findViewById(R.id.etQuestion)).setText(String.format("%s", flashcard.getQuestion()));
        ((EditText) view.findViewById(R.id.etAnswer)).setText(String.format("%s", flashcard.getAnswer()));

        EditText etQuestion = view.findViewById(R.id.etQuestion);
        EditText etAnswer = view.findViewById(R.id.etAnswer);

        etQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Da bam vao flashcard co ID la: " + flashcard.getId());
            }
        });

        etAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Da bam vao flashcard co ID la: " + flashcard.getId());
            }
        });

        return view;
    }

}
