package com.trduc.flashlearn.Adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trduc.flashlearn.Controllers.BeforeDeleteFlashcardsActivity;
import com.trduc.flashlearn.Controllers.DeleteFlashcardsActivity;
import com.trduc.flashlearn.Controllers.MainActivity;
import com.trduc.flashlearn.Models.Flashcard;
import com.trduc.flashlearn.R;

import java.util.ArrayList;

import com.google.firebase.auth.FirebaseAuth;



public class AlreadyCreateAdapter extends BaseAdapter {

    final ArrayList<Flashcard> flashcardList;
    private String flashcardSetsId;
    private String choice;

    public AlreadyCreateAdapter(ArrayList<Flashcard> flashcardList, String flashcardSetsId, String choice) {
        this.flashcardList = flashcardList;
        this.flashcardSetsId = flashcardSetsId;
        this.choice = choice;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = View.inflate(parent.getContext(), R.layout.already_create, null);
        } else {
            view = convertView;
        }

        Flashcard flashcard = flashcardList.get(position);
        EditText etQuestion = view.findViewById(R.id.etQuestion);
        EditText etAnswer = view.findViewById(R.id.etAnswer);

        etQuestion.setText(String.valueOf(flashcard.getQuestion()));
        etAnswer.setText(String.valueOf(flashcard.getAnswer()));
        System.out.println("Choice: " + choice);

        etQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choice.equals("Delete")) showDeleteConfirmationDialog(position, flashcard.getId(), v.getContext());
            }
        });

        etAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choice.equals("Delete")) showDeleteConfirmationDialog(position, flashcard.getId(), v.getContext());
            }
        });

        return view;
    }

    private void showDeleteConfirmationDialog(final int position, final String flashcardId, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.dialog_delete_flashcards);

        final AlertDialog dialog = builder.create();
        dialog.show();

        Button bYes = dialog.findViewById(R.id.bYes);
        Button bNo = dialog.findViewById(R.id.bNo);

        bYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFlashcard(position, flashcardId);
                dialog.dismiss();
            }
        });

        bNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void deleteFlashcard(int position, String flashcardId) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) {
            db.collection("users")
                    .document(mAuth.getCurrentUser().getEmail())
                    .collection("flashcard_sets")
                    .document(flashcardSetsId)
                    .collection("flashcards")
                    .document(flashcardId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            flashcardList.remove(position);
                            notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }


}