package com.trduc.flashlearn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flashcards);

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
                                Toast.makeText(CreateFlashcardsActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}