package com.trduc.flashlearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class CreateFlashcardsActivity extends AppCompatActivity {

    ArrayList<Flashcard> flashcardList;
    ListView lvCreateFlashcards;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String flashcardSetsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flashcards);

        flashcardSetsId = getIntent().getStringExtra("flashcardSetsId");

        flashcardList = new ArrayList<>();
        lvCreateFlashcards = findViewById(R.id.lvCreateFlashcards);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                db.collection("users")
                        .document(userEmail)
                        .collection("flashcard_sets")
                        .document("default")
                        .collection("flashcards")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Flashcard flashcard = document.toObject(Flashcard.class);
                                    flashcardList.add(flashcard);
                                    System.out.println(flashcard.getQuestion());
                                    System.out.println(flashcard.getAnswer());
                                }
                                CreateFlashcardAdapter createFlashcardAdapter = new CreateFlashcardAdapter(flashcardList);
                                lvCreateFlashcards.setAdapter(createFlashcardAdapter);
                            } else {

                            }
                        });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteFlashcardSets();
        Intent intent = new Intent(CreateFlashcardsActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void deleteFlashcardSets() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                db.collection("users")
                        .document(userEmail)
                        .collection("flashcard_sets")
                        .document(flashcardSetsId) // Sử dụng flashcardSetsId từ Intent
                        .delete()
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


}