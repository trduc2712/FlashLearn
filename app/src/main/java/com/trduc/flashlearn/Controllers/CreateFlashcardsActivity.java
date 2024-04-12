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
import com.google.firebase.firestore.QuerySnapshot;
import com.trduc.flashlearn.Adapters.AlreadyCreateAdapter;
import com.trduc.flashlearn.Models.Flashcard;
import com.trduc.flashlearn.R;

import java.util.ArrayList;

public class CreateFlashcardsActivity extends AppCompatActivity {

    private ListView lvAlreadyCreate;
    private EditText etQuestion, etAnswer;
    private Button bAdd, bCancel, bCreate;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    FirebaseUser currentUser;
    private ArrayList<Flashcard> flashcardList, deletedFlashcards, editedFlashcards;
    private String flashcardSetsId;
    private AlreadyCreateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flashcards);
        initUi();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();
        flashcardSetsId = getIntent().getStringExtra("flashcardSetsId");
        flashcardList = new ArrayList<>();
        deletedFlashcards = new ArrayList<>();
        editedFlashcards = new ArrayList<>();

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

        String choice = "Create";
        adapter = new AlreadyCreateAdapter(flashcardList, flashcardSetsId, choice, deletedFlashcards, editedFlashcards);
        lvAlreadyCreate.setAdapter(adapter);

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = etQuestion.getText().toString().trim();
                String answer = etAnswer.getText().toString().trim();

                if (question.isEmpty() || answer.isEmpty()) {
                    if (question.isEmpty() && answer.isEmpty()) {
                        Toast.makeText(CreateFlashcardsActivity.this, "Vui lòng nhập câu hỏi và đáp án", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (question.isEmpty()) {
                        Toast.makeText(CreateFlashcardsActivity.this, "Vui lòng nhập câu hỏi", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(CreateFlashcardsActivity.this, "Vui lòng nhập đáp án", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser != null) {
                    db.collection("users")
                            .document(currentUser.getEmail())
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

                                Flashcard newFlashcard = new Flashcard(question, answer, String.valueOf(newFlashcardId));

                                db.collection("users")
                                        .document(currentUser.getEmail())
                                        .collection("flashcard_sets")
                                        .document(flashcardSetsId)
                                        .collection("flashcards")
                                        .document(String.valueOf(newFlashcardId))
                                        .set(newFlashcard)
                                        .addOnSuccessListener(aVoid -> {
                                            etQuestion.setText("");
                                            etAnswer.setText("");
                                            flashcardList.add(newFlashcard);
                                            adapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(CreateFlashcardsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(CreateFlashcardsActivity.this, "Không thể cập nhật danh sách flashcards", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(CreateFlashcardsActivity.this, "Không thể lấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }


            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFlashcardSet();
                deleteAllFlashcards();
                Intent intent = new Intent(CreateFlashcardsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flashcardList.size() > 0) {
                    Intent intent = new Intent(CreateFlashcardsActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CreateFlashcardsActivity.this, "Hãy tạo ít nhất một flashcard", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initUi() {
        bCreate = findViewById(R.id.bCreate);
        bCancel = findViewById(R.id.bCancel);
        bAdd = findViewById(R.id.bAdd);
        etQuestion = findViewById(R.id.etQuestion);
        etAnswer = findViewById(R.id.etAnswer);
        lvAlreadyCreate = findViewById(R.id.lvAlreadyCreate);
    }

    private void deleteFlashcardSet() {
        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getEmail())
                    .collection("flashcard_sets")
                    .document(flashcardSetsId)
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

    private void deleteAllFlashcards() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getEmail())
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

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteFlashcardSet();
        deleteAllFlashcards();
        Intent intent = new Intent(CreateFlashcardsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}