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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.trduc.flashlearn.Adapters.AlreadyCreateAdapter;
import com.trduc.flashlearn.Models.Flashcard;
import com.trduc.flashlearn.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddFlashcardsActivity extends AppCompatActivity {

    private ArrayList<Flashcard> flashcardList, deletedFlashcards, addedFlashcards, editedFlashcards;
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
        addedFlashcards = new ArrayList<>();
        deletedFlashcards = new ArrayList<>();
        editedFlashcards = new ArrayList<>();
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
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            flashcardList.clear();
                            for (Flashcard flashcard : queryDocumentSnapshots.toObjects(Flashcard.class)) {
                                flashcardList.add(flashcard);
                            }
                            adapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {});
            }
        }

        String choice = "Add";
        adapter = new AlreadyCreateAdapter(flashcardList, flashcardSetsId, choice, deletedFlashcards, editedFlashcards);
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
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        final String userEmail = currentUser.getEmail();

                        db.collection("users")
                                .document(userEmail)
                                .collection("flashcard_sets")
                                .document(flashcardSetsId)
                                .collection("flashcards")
                                .orderBy("id", Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    char newFlashcardId;
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                                        char lastFlashcardId = lastDocument.getString("id").charAt(0);
                                        newFlashcardId = (char) (lastFlashcardId + 1);
                                    } else {
                                        newFlashcardId = 'a';
                                    }

                                    char finalNewFlashcardId = newFlashcardId;

                                    final Map<String, Object> flashcard = new HashMap<>();
                                    flashcard.put("question", question);
                                    flashcard.put("answer", answer);
                                    flashcard.put("id", String.valueOf(finalNewFlashcardId));
                                    Flashcard flashcard1 = new Flashcard(question, answer, String.valueOf(finalNewFlashcardId));
                                    addedFlashcards.add(flashcard1);

                                    db.collection("users")
                                            .document(userEmail)
                                            .collection("flashcard_sets")
                                            .document(flashcardSetsId)
                                            .collection("flashcards")
                                            .document(String.valueOf(finalNewFlashcardId))
                                            .set(flashcard)
                                            .addOnSuccessListener(aVoid -> {
                                                etQuestion.setText("");
                                                etAnswer.setText("");
                                                flashcardList.add(new Flashcard(question, answer, String.valueOf(finalNewFlashcardId)));
                                                adapter.notifyDataSetChanged();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(AddFlashcardsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(e -> Toast.makeText(AddFlashcardsActivity.this, "Không thể lấy dữ liệu flashcard_sets", Toast.LENGTH_SHORT).show());
                    }
                }

                for (Flashcard flashcard : addedFlashcards) {
                    String ques = flashcard.getQuestion();
                    String ans = flashcard.getAnswer();
                    String id = flashcard.getId();

                    System.out.println("ID: " + id);
                    System.out.println("Question: " + ques);
                    System.out.println("Answer: " + ans);
                    System.out.println("");
                }

            }
        });


        bCancel.setOnClickListener(view -> {
            if (!addedFlashcards.isEmpty()) {
                for (Flashcard flashcard : addedFlashcards) {
                    db.collection("users")
                            .document(currentUser.getEmail())
                            .collection("flashcard_sets")
                            .document(flashcardSetsId)
                            .collection("flashcards")
                            .document(flashcard.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {})
                            .addOnFailureListener(e -> {});
                }
                addedFlashcards.clear();
                adapter.notifyDataSetChanged();
            }
            startActivity(new Intent(AddFlashcardsActivity.this, BeforeAddFlashcardsActivity.class));
        });

        bSave.setOnClickListener(view -> startActivity(new Intent(AddFlashcardsActivity.this, BeforeAddFlashcardsActivity.class)));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelAndDeleteFlashcards();
    }

    private void cancelAndDeleteFlashcards() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            for (Flashcard flashcard : addedFlashcards) {
                db.collection("users")
                        .document(currentUser.getEmail())
                        .collection("flashcard_sets")
                        .document(flashcardSetsId)
                        .collection("flashcards")
                        .document(flashcard.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {})
                        .addOnFailureListener(e -> {});
            }
        }
        startActivity(new Intent(AddFlashcardsActivity.this, BeforeAddFlashcardsActivity.class));
    }
}
