package com.trduc.flashlearn.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trduc.flashlearn.Adapters.AlreadyCreateAdapter;
import com.trduc.flashlearn.Models.Flashcard;
import com.trduc.flashlearn.R;

import java.util.ArrayList;

public class DeleteFlashcardsActivity extends AppCompatActivity {

    TextView tvTitle;
    Button bSave, bCancel;
    ListView lvAlreadyCreate;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    ArrayList<Flashcard> flashcardList, deletedFlashcards, editedFlashcards;
    String flashcardSetsId;
    AlreadyCreateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_flashcards);
        initUi();

        flashcardList = new ArrayList<>();
        deletedFlashcards = new ArrayList<>();
        editedFlashcards = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        Intent intent = getIntent();
        if (intent != null) {
            flashcardSetsId = intent.getStringExtra("flashcardSetsId");
            System.out.println(flashcardSetsId);
        }

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                db.collection("users")
                        .document(userEmail)
                        .collection("flashcard_sets")
                        .document(flashcardSetsId)
                        .collection("flashcards")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                flashcardList.clear();
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Flashcard flashcard = document.toObject(Flashcard.class);
                                    flashcardList.add(flashcard);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        }

        String choice = "Delete";
        adapter = new AlreadyCreateAdapter(flashcardList, flashcardSetsId, choice, deletedFlashcards, editedFlashcards);
        lvAlreadyCreate.setAdapter(adapter);

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Flashcard flashcard : deletedFlashcards) {
                    System.out.println(flashcard.getId() + ": " + "Ques: " + flashcard.getQuestion() + ", Ans: " + flashcard.getAnswer());
                }
                Intent intent = new Intent(DeleteFlashcardsActivity.this, BeforeDeleteFlashcardsActivity.class);
                startActivity(intent);
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDeletedFlashcardsToFirestore();
                Intent intent = new Intent(DeleteFlashcardsActivity.this, BeforeDeleteFlashcardsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initUi() {
        bSave = findViewById(R.id.bSave);
        bCancel = findViewById(R.id.bCancel);
        tvTitle = findViewById(R.id.tvTitle);
        lvAlreadyCreate = findViewById(R.id.lvAlreadyCreate);
    }

    private void addDeletedFlashcardsToFirestore() {
        if (auth.getCurrentUser() != null) {
            for (Flashcard flashcard : deletedFlashcards) {
                db.collection("users")
                        .document(auth.getCurrentUser().getEmail())
                        .collection("flashcard_sets")
                        .document(flashcardSetsId)
                        .collection("flashcards")
                        .document(flashcard.getId())
                        .set(flashcard)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        addDeletedFlashcardsToFirestore();
    }

}