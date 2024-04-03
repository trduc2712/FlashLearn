package com.trduc.flashlearn;

import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.activity.result.contract.ActivityResultContracts;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {

    final ArrayList<FlashcardSets> flashcardSetsList;

    public SearchAdapter(ArrayList<FlashcardSets> flashcardSetsList) {
        this.flashcardSetsList = flashcardSetsList;
    }

    @Override
    public int getCount() {
        return flashcardSetsList.size();
    }

    @Override
    public Object getItem(int position) {
        return flashcardSetsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = View.inflate(parent.getContext(), R.layout.flashcard_sets, null);
        } else {
            view = convertView;
        }

        FlashcardSets flashcardSets = (FlashcardSets) getItem(position);
        ((TextView) view.findViewById(R.id.bFlashcardSetsName)).setText(String.format("%s", flashcardSets.getName()));
        Button bFlashcardSetsName = view.findViewById(R.id.bFlashcardSetsName);
        bFlashcardSetsName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Da bam vao flashcard sets co ID la " + flashcardSets.getId());
                Context context = view.getContext();
                FlashcardSets flashcardSets = flashcardSetsList.get(position);
                Intent intent = new Intent(context, LearnActivity.class);
                intent.putExtra("flashcardSetsId", flashcardSets.getId());
                context.startActivity(intent);
            }
        });

        return view;
    }

}
