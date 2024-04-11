package com.trduc.flashlearn.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trduc.flashlearn.Models.Flashcard;
import com.trduc.flashlearn.R;

import java.util.ArrayList;

public class LearnActivity extends AppCompatActivity {
    ImageView ivForward, ivBackward;
    TextView tvContent, tvCurrentFlashcard;
    SeekBar seekBar;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    int currentFlashcardIndex = 0;
    String flashcardSetsId;
    ArrayList<Flashcard> flashcardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        ivForward = findViewById(R.id.ivForward);
        ivBackward = findViewById(R.id.ivBackward);
        tvContent = findViewById(R.id.tvContent);
        tvCurrentFlashcard = findViewById(R.id.tvCurrentFlashcard);
        seekBar = findViewById(R.id.seekBar);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        flashcardList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            flashcardSetsId = intent.getStringExtra("flashcardSetsId");
            loadFlashcards();
        }

        ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextFlashcard();
                seekBar.incrementProgressBy(1);
            }
        });

        ivBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousFlashcard();
                seekBar.incrementProgressBy(-1);
            }
        });

        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipFlashcard();
            }
        });
    }

    private void flipFlashcard() {
        if (tvContent.getText().equals(flashcardList.get(currentFlashcardIndex).getQuestion())) {
            tvContent.setText(flashcardList.get(currentFlashcardIndex).getAnswer());
        } else {
            tvContent.setText(flashcardList.get(currentFlashcardIndex).getQuestion());
        }
    }

    private void loadFlashcards() {
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
                            flashcardList.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Flashcard flashcard = document.toObject(Flashcard.class);
                                flashcardList.add(flashcard);
                            }
                            seekBar.setMax(flashcardList.size() - 1);
                            if (!flashcardList.isEmpty()) {
                                showFlashcard(currentFlashcardIndex);
                            } else {

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

    private void showFlashcard(int index) {
        if (index >= 0 && index < flashcardList.size()) {
            Flashcard flashcard = flashcardList.get(index);
            tvContent.setText(flashcard.getQuestion());
            tvCurrentFlashcard.setText((index + 1) + " / " + flashcardList.size());
        }
    }

    private void showNextFlashcard() {
        if (currentFlashcardIndex < flashcardList.size() - 1) {
            currentFlashcardIndex++;
            showFlashcard(currentFlashcardIndex);
        } else {
            Toast.makeText(this, "Đã đến flashcard cuối cùng", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPreviousFlashcard() {
        if (currentFlashcardIndex > 0) {
            currentFlashcardIndex--;
            showFlashcard(currentFlashcardIndex);
        } else {
            Toast.makeText(this, "Đang ở flashcard đầu tiên", Toast.LENGTH_SHORT).show();
        }
    }
}
