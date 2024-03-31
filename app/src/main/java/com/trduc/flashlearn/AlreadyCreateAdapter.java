//package com.trduc.flashlearn;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//public class CreateFlashcardAdapter extends BaseAdapter {
//
//    final ArrayList<Flashcard> flashcardList;
//
//    public CreateFlashcardAdapter(ArrayList<Flashcard> flashcardList) {
//        this.flashcardList = flashcardList;
//    }
//
//    @Override
//    public int getCount() {
//        return flashcardList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return flashcardList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        SharedPreferences sharedPreferences = parent.getContext().getSharedPreferences("FlashcardPrefs", Context.MODE_PRIVATE);
//
//        String flashcardSetsId = sharedPreferences.getString("flashcardSetsId", "");
//
//        View view;
//
//        if (convertView == null) {
//            view = View.inflate(parent.getContext(), R.layout.create_flashcard, null);
//        } else {
//            view = convertView;
//        }
//
//        Flashcard flashcard = (Flashcard) getItem(position);
//        ((EditText) view.findViewById(R.id.etQuestion)).setText(String.format("%s", flashcard.getQuestion()));
//        ((EditText) view.findViewById(R.id.etAnswer)).setText(String.format("%s", flashcard.getAnswer()));
//
//        String question = ((EditText) view.findViewById(R.id.etQuestion)).getText().toString();
//        String answer = ((EditText) view.findViewById(R.id.etAnswer)).getText().toString();
//
//        Button bAdd = view.findViewById(R.id.bAdd);
//        bAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("Da bam vao bAdd");
//                System.out.println(flashcardSetsId);
//            }
//        });
//
//        return view;
//    }
//
//}
