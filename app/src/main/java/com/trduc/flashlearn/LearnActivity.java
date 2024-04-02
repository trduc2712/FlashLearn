package com.trduc.flashlearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LearnActivity extends AppCompatActivity {

    ImageView ivForward, ivBackward;
    TextView tvContent, tvCurrentFlashcard;
    String flashcardSetsId;
    int currentFlashcardIndex = 0;
    int totalFlashcards = 0;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String question, answer;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        ivForward = findViewById(R.id.ivForward);
        ivBackward = findViewById(R.id.ivBackward);
        tvCurrentFlashcard = findViewById(R.id.tvCurrentFlashcard);
        tvContent = findViewById(R.id.tvContent);
        seekBar = findViewById(R.id.seekBar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            flashcardSetsId = intent.getStringExtra("flashcardSetsId");
        }

        getFlashcardCount();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentFlashcardIndex = progress;
                loadFlashcard(currentFlashcardIndex);
                updateCurrentFlashcardTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

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
                if (currentFlashcardIndex < totalFlashcards - 1) {
                    currentFlashcardIndex++;
                    seekBar.setProgress(currentFlashcardIndex);
                }
            }
        });

        ivBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToQuestion();
                if (currentFlashcardIndex > 0) {
                    currentFlashcardIndex--;
                    seekBar.setProgress(currentFlashcardIndex);
                }
            }
        });
    }

    private void getFlashcardCount() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db.collection("users")
                .document(currentUser.getEmail())
                .collection("flashcard_sets")
                .document(flashcardSetsId)
                .collection("flashcards")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    totalFlashcards = queryDocumentSnapshots.size();
                    seekBar.setMax(totalFlashcards - 1);
                    updateCurrentFlashcardTextView();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LearnActivity.this, "Failed to get flashcard count", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCurrentFlashcardTextView() {
        String currentFlashcardText = (currentFlashcardIndex + 1) + "/" + totalFlashcards;
        tvCurrentFlashcard.setText(currentFlashcardText);
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
                            }
                        } else {
                            Toast.makeText(LearnActivity.this, "End of flashcards!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LearnActivity.this, "Failed to load flashcard", Toast.LENGTH_SHORT).show();
                });
    }
}
