package com.trduc.flashlearn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateFlashcardsActivity extends AppCompatActivity {

    private ArrayList<Flashcard> flashcardList;
    private ListView lvCreateFlashcards;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String flashcardSetsId;
    private EditText etQuestion, etAnswer;
    private Button bAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flashcards);

        flashcardSetsId = getIntent().getStringExtra("flashcardSetsId");

        bAdd = findViewById(R.id.bAdd);
        etQuestion = findViewById(R.id.etQuestion);
        etAnswer = findViewById(R.id.etAnswer);

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
                                for (DocumentSnapshot document : task.getResult()) {
                                    Flashcard flashcard = document.toObject(Flashcard.class);
                                    flashcardList.add(flashcard);
                                }
                            } else {

                            }
                        });
            }
        }

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = etQuestion.getText().toString().trim();
                String answer = etAnswer.getText().toString().trim();

                if (question.isEmpty() || answer.isEmpty()) {
                    Toast.makeText(CreateFlashcardsActivity.this, "Vui lòng nhập câu hỏi và đáp án", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> flashcardData = new HashMap<>();
                flashcardData.put("question", question);
                flashcardData.put("answer", answer);

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userEmail = currentUser.getEmail();
                    if (userEmail != null) {
                        db.collection("users")
                                .document(userEmail)
                                .collection("flashcard_sets")
                                .document(flashcardSetsId)
                                .collection("flashcards")
                                .add(flashcardData)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(CreateFlashcardsActivity.this, "Đã thêm flashcard mới", Toast.LENGTH_SHORT).show();
                                        etQuestion.setText("");
                                        etAnswer.setText("");
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
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteFlashcardSets();
        Intent intent = new Intent(CreateFlashcardsActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void deleteFlashcardSets() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                db.collection("users")
                        .document(userEmail)
                        .collection("flashcard_sets")
                        .document(flashcardSetsId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                deleteAllFlashcards();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateFlashcardsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void deleteAllFlashcards() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

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
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    document.getReference().delete();
                                }
                                Toast.makeText(CreateFlashcardsActivity.this, "Đã xoá hoàn toàn flashcard_sets và các flashcards liên quan.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateFlashcardsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

}
