package com.trduc.flashlearn;

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

import java.util.ArrayList;

public class CreateFlashcardsActivity extends AppCompatActivity {

    private ArrayList<Flashcard> flashcardList;
    private ListView lvAlreadyCreate;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String flashcardSetsId;
    private EditText etQuestion, etAnswer;
    private Button bAdd, bCancel, bCreate;
    private AlreadyCreateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flashcards);

        flashcardSetsId = getIntent().getStringExtra("flashcardSetsId");

        bCreate = findViewById(R.id.bCreate);
        bCancel = findViewById(R.id.bCancel);
        bAdd = findViewById(R.id.bAdd);
        etQuestion = findViewById(R.id.etQuestion);
        etAnswer = findViewById(R.id.etAnswer);

        flashcardList = new ArrayList<>();
        lvAlreadyCreate = findViewById(R.id.lvAlreadyCreate);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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
                                Toast.makeText(CreateFlashcardsActivity.this, "Không thể lấy dữ liệu", Toast.LENGTH_SHORT).show();
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
                    if (question.isEmpty()) {
                        Toast.makeText(CreateFlashcardsActivity.this, "Vui lòng nhập câu hỏi", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(CreateFlashcardsActivity.this, "Vui lòng nhập đáp án", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                FirebaseUser currentUser = mAuth.getCurrentUser();
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
                                    int numFlashcards = queryDocumentSnapshots.size();
                                    String newFlashcardId = String.valueOf(numFlashcards + 1); // Tạo ID mới dựa trên số lượng flashcards hiện có

                                    Flashcard newFlashcard = new Flashcard(question, answer);

                                    db.collection("users")
                                            .document(currentUser.getEmail())
                                            .collection("flashcard_sets")
                                            .document(flashcardSetsId)
                                            .collection("flashcards")
                                            .document(newFlashcardId) // Gán ID mới cho flashcard
                                            .set(newFlashcard)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    // Xử lý khi thêm flashcard thành công
                                                    etQuestion.setText("");
                                                    etAnswer.setText("");
                                                    flashcardList.add(newFlashcard);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CreateFlashcardsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreateFlashcardsActivity.this, "Không thể cập nhật danh sách flashcards", Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    Toast.makeText(CreateFlashcardsActivity.this, "Không thể lấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFlashcardSet(flashcardSetsId);
                deleteAllFlashcards();
                Intent intent = new Intent(CreateFlashcardsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateFlashcardsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void deleteFlashcardSet(String flashcardSetsId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getEmail())
                    .collection("flashcard_sets")
                    .document(flashcardSetsId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(CreateFlashcardsActivity.this, "Đã xoá hoàn toàn flashcard set vừa tạo", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateFlashcardsActivity.this, MainActivity.class);
                            startActivity(intent);
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

    private void deleteAllFlashcards() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

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
//                            Toast.makeText(CreateFlashcardsActivity.this, "Đã xoá hoàn toàn các flashcard", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteFlashcardSet(flashcardSetsId);
        deleteAllFlashcards();
        Intent intent = new Intent(CreateFlashcardsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}