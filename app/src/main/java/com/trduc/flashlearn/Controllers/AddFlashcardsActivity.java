package com.trduc.flashlearn.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trduc.flashlearn.Adapters.AlreadyCreateAdapter;
import com.trduc.flashlearn.Models.Flashcard;
import com.trduc.flashlearn.R;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddFlashcardsActivity extends AppCompatActivity {

    private ArrayList<Flashcard> flashcardList;
    private ArrayList<String> addedFlashcardIds;
    private ListView lvAlreadyCreate;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String flashcardSetsId;
    private EditText etQuestion, etAnswer;
    private Button bAdd, bCancel, bSave;
    private AlreadyCreateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flashcards);

        bSave = findViewById(R.id.bSave);
        bCancel = findViewById(R.id.bCancel);
        bAdd = findViewById(R.id.bAdd);
        etQuestion = findViewById(R.id.etQuestion);
        etAnswer = findViewById(R.id.etAnswer);

        flashcardList = new ArrayList<>();
        addedFlashcardIds = new ArrayList<>();
        lvAlreadyCreate = findViewById(R.id.lvAlreadyCreate);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

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

        adapter = new AlreadyCreateAdapter(flashcardList);
        lvAlreadyCreate.setAdapter(adapter);

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = etQuestion.getText().toString().trim();
                String answer = etAnswer.getText().toString().trim();

                if (question.isEmpty() || answer.isEmpty()) {
                    if (question.isEmpty() && answer.isEmpty()) {
                        Toast.makeText(AddFlashcardsActivity.this, "Vui lòng nhập câu hỏi và đáp án", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (question.isEmpty()) {
                        Toast.makeText(AddFlashcardsActivity.this, "Vui lòng nhập câu hỏi", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(AddFlashcardsActivity.this, "Vui lòng nhập đáp án", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (!question.isEmpty() && !answer.isEmpty()) {
                    if (currentUser != null) {
                        final String userEmail = currentUser.getEmail();

                        db.collection("users")
                                .document(userEmail)
                                .collection("flashcard_sets")
                                .document(flashcardSetsId)
                                .collection("flashcards")
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    int numFlashcards = queryDocumentSnapshots.size();
                                    String newFlashcardId = String.valueOf((char) ('a' + numFlashcards));
                                    addedFlashcardIds.add(newFlashcardId);

                                    String finalNewFlashcardId = newFlashcardId;

                                    boolean idExists = false;
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        String flashcardId = document.getString("id");
                                        if (flashcardId != null && flashcardId.equals(String.valueOf(finalNewFlashcardId))) {
                                            idExists = true;
                                            break;
                                        }
                                    }

                                    while (idExists) {
                                        finalNewFlashcardId = String.valueOf((char) ('a' + ++numFlashcards));
                                        idExists = false;
                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                            String flashcardSetId = document.getString("id");
                                            if (flashcardSetId.equals(String.valueOf(finalNewFlashcardId))) {
                                                idExists = true;
                                                break;
                                            }
                                        }
                                    }

                                    final String finalFinalNewFlashcardSetId = finalNewFlashcardId;
                                    final Map<String, Object> flashcard = new HashMap<>();
                                    flashcard.put("question", question);
                                    flashcard.put("answer", answer);
                                    flashcard.put("id", finalFinalNewFlashcardSetId);

                                    db.collection("users")
                                            .document(userEmail)
                                            .collection("flashcard_sets")
                                            .document(flashcardSetsId)
                                            .collection("flashcards")
                                            .document(String.valueOf(finalFinalNewFlashcardSetId))
                                            .set(flashcard)
                                            .addOnSuccessListener(aVoid -> {
                                                etQuestion.setText("");
                                                etAnswer.setText("");
                                                flashcardList.add(new Flashcard(question, answer, finalFinalNewFlashcardSetId + ""));
                                                adapter.notifyDataSetChanged();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(AddFlashcardsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(e -> Toast.makeText(AddFlashcardsActivity.this, "Không thể lấy dữ liệu flashcard_sets", Toast.LENGTH_SHORT).show());
                    }
                } else {

                }
            }
        });


        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    String userEmail = currentUser.getEmail();
                    if (userEmail != null) {
                        for (String flashcardId : addedFlashcardIds) {
                            db.collection("users")
                                    .document(userEmail)
                                    .collection("flashcard_sets")
                                    .document(flashcardSetsId)
                                    .collection("flashcards")
                                    .document(String.valueOf(flashcardId))
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            flashcardList.clear();
                                            adapter.notifyDataSetChanged();
                                            Intent intent = new Intent(AddFlashcardsActivity.this, BeforeAddFlashcardsActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Intent intent = new Intent(AddFlashcardsActivity.this, BeforeAddFlashcardsActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                        }
                    }
                }
                Intent intent = new Intent(AddFlashcardsActivity.this, BeforeAddFlashcardsActivity.class);
                startActivity(intent);
            }
        });


        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddFlashcardsActivity.this, BeforeAddFlashcardsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}