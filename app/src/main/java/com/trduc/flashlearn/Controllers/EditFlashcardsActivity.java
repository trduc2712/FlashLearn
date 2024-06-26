package com.trduc.flashlearn.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trduc.flashlearn.Models.Flashcard;
import com.trduc.flashlearn.R;

import java.util.*;

public class EditFlashcardsActivity extends AppCompatActivity {

    ImageView ivBackward, ivForward;
    EditText etQuestion, etAnswer;
    SeekBar seekBar;
    Button bSave;
    TextView tvCurrentFlashcard;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    ArrayList<Flashcard> flashcardList;
    ArrayList<Flashcard> originalFlashcardList;
    String flashcardSetsId;
    int currentFlashcardIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flashcards);
        initUi();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();
        flashcardList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            flashcardSetsId = intent.getStringExtra("flashcardSetsId");
            System.out.println(flashcardSetsId);
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

        etQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateFirestoreQuestion(s.toString());
            }
        });

        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateFirestoreAnswer(s.toString());
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditFlashcardsActivity.this, BeforeEditFlashcardsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initUi() {
        bSave = findViewById(R.id.bSave);
        ivBackward = findViewById(R.id.ivBackward);
        ivForward = findViewById(R.id.ivForward);
        etQuestion = findViewById(R.id.etQuestion);
        etAnswer = findViewById(R.id.etAnswer);
        tvCurrentFlashcard = findViewById(R.id.tvCurrentFlashcard);
        seekBar = findViewById(R.id.seekBar);
    }

    private void showFlashcard(int index) {
        if (index >= 0 && index < flashcardList.size()) {
            Flashcard flashcard = flashcardList.get(index);
            etQuestion.setText(flashcard.getQuestion());
            etAnswer.setText(flashcard.getAnswer());
            tvCurrentFlashcard.setText((index + 1) + " / " + flashcardList.size());
            currentFlashcardIndex = index;
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
                            originalFlashcardList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Flashcard flashcard = document.toObject(Flashcard.class);
                                flashcardList.add(flashcard);
                                originalFlashcardList.add(new Flashcard(
                                        flashcard.getQuestion(),
                                        flashcard.getAnswer(),
                                        flashcard.getId()
                                ));
                            }
                            seekBar.setMax(flashcardList.size() - 1);
                            if (!flashcardList.isEmpty()) {
                                showFlashcard(currentFlashcardIndex);
                            } else {
                                Toast.makeText(EditFlashcardsActivity.this, "Không có flashcard nào", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditFlashcardsActivity.this, "Không thể lấy dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateFirestoreQuestion(String question) {
        if (currentUser != null) {
            Flashcard currentFlashcard = flashcardList.get(currentFlashcardIndex);
            if (currentFlashcard != null && currentFlashcard.getId() != null) {
                currentFlashcard.setQuestion(question);
                db.collection("users")
                        .document(currentUser.getEmail())
                        .collection("flashcard_sets")
                        .document(flashcardSetsId)
                        .collection("flashcards")
                        .document(currentFlashcard.getId())
                        .update("question", question)
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
            } else {

            }
        }
    }

    private void updateFirestoreAnswer(String answer) {
        if (currentUser != null) {
            Flashcard currentFlashcard = flashcardList.get(currentFlashcardIndex);
            if (currentFlashcard != null && currentFlashcard.getId() != null) {
                currentFlashcard.setAnswer(answer);
                db.collection("users")
                        .document(currentUser.getEmail())
                        .collection("flashcard_sets")
                        .document(flashcardSetsId)
                        .collection("flashcards")
                        .document(currentFlashcard.getId())
                        .update("answer", answer)
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
            } else {

            }
        }
    }

    private void restoreFlashcardsInFirestore() {
        if (currentUser != null) {
            for (Flashcard flashcard : originalFlashcardList) {
                db.collection("users")
                        .document(auth.getCurrentUser().getEmail())
                        .collection("flashcard_sets")
                        .document(flashcardSetsId)
                        .collection("flashcards")
                        .document(flashcard.getId())
                        .update(
                                "question", flashcard.getQuestion(),
                                "answer", flashcard.getAnswer()
                        )
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


    @Override
    public void onBackPressed() {
        restoreFlashcardsInFirestore();
        super.onBackPressed();
    }

}