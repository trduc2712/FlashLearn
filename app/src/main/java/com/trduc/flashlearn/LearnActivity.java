package com.trduc.flashlearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trduc.flashlearn.R;

public class LearnActivity extends AppCompatActivity {

    ImageView ivForward, ivBackward;
    TextView tvContent;
    String flashcardSetsId;
    int currentFlashcardIndex = 0;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String question, answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        ivForward = findViewById(R.id.ivForward);
        ivBackward = findViewById(R.id.ivBackward);
        tvContent = findViewById(R.id.tvContent);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            flashcardSetsId = intent.getStringExtra("flashcardSetsId");
        }

        loadFlashcard(currentFlashcardIndex);

        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvContent.getText().toString().equals(answer)) {
                    switchToQuestion();
                } else {
                    switchToAnswer();
                }
            }
        });

        ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToQuestion();
                currentFlashcardIndex++;
                loadFlashcard(currentFlashcardIndex);
            }
        });

        ivBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToQuestion();
                if (currentFlashcardIndex > 0) {
                    currentFlashcardIndex--;
                    loadFlashcard(currentFlashcardIndex);
                }
            }
        });
    }

    private void switchToQuestion() {
        // Chuyển sang câu hỏi
        tvContent.setText(question);
        tvContent.setTextColor(getResources().getColor(R.color.pink0));
        tvContent.setBackgroundResource(R.drawable.white_background_question);
    }

    private void switchToAnswer() {
        // Chuyển sang câu trả lời
        tvContent.setText(answer);
        tvContent.setTextColor(getResources().getColor(android.R.color.white));
        tvContent.setBackgroundResource(R.drawable.pink0_background_answer);
    }

    private void loadFlashcard(int index) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db.collection("users")
                .document(currentUser.getEmail())
                .collection("flashcard_sets")
                .document(flashcardSetsId)
                .collection("flashcards")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        if (index >= 0 && index < queryDocumentSnapshots.size()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(index);
                            if (documentSnapshot != null) {
                                question = documentSnapshot.getString("question");
                                answer = documentSnapshot.getString("answer");
                                tvContent.setText(question);
                            } else {

                            }
                        } else {
                            Toast.makeText(LearnActivity.this, "Hết flashcard rồi!", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                    }
                })
                .addOnFailureListener(e -> {

                });
    }

}
