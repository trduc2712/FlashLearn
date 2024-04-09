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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trduc.flashlearn.Models.Flashcard;
import com.trduc.flashlearn.R;

import java.util.ArrayList;

public class PraticeActivity extends AppCompatActivity {

    String flashcardSetsId;
    ImageView ivForward, ivBackward;
    Button bAnswer;
    TextView tvNoti, tvQuestion, tvCurrentFlashcard;
    EditText etAnswer;
    SeekBar seekBar;
    int currentFlashcardIndex = 0;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<Flashcard> flashcardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pratice);

        initUi();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        flashcardList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            flashcardSetsId = intent.getStringExtra("flashcardSetsId");
            System.out.println(flashcardSetsId);
            loadFlashcards();
        }

        tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipFlashcard();
            }
        });

        ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextFlashcard();
                seekBar.incrementProgressBy(1);
                tvNoti.setVisibility(View.INVISIBLE);
                etAnswer.setBackground(getResources().getDrawable(R.drawable.pink0_rounded_border_4sp));
                etAnswer.setText("");
            }
        });

        ivBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousFlashcard();
                seekBar.incrementProgressBy(-1);
                tvNoti.setVisibility(View.INVISIBLE);
                etAnswer.setBackground(getResources().getDrawable(R.drawable.pink0_rounded_border_4sp));
                etAnswer.setText("");
            }
        });

        bAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = etAnswer.getText().toString();
                if (answer.toLowerCase().equals(flashcardList.get(currentFlashcardIndex).getAnswer().toLowerCase())) {
                    tvNoti.setText("Chính xác");
                    tvNoti.setVisibility(View.VISIBLE);
                    tvNoti.setTextColor(getResources().getColor(R.color.green));
                    etAnswer.setBackground(getResources().getDrawable(R.drawable.green_background));
                } else {
                    tvNoti.setText("Sai rồi");
                    tvNoti.setVisibility(View.VISIBLE);
                    tvNoti.setTextColor(getResources().getColor(R.color.red));
                    etAnswer.setBackground(getResources().getDrawable(R.drawable.red_background));
                }
            }
        });

        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                etAnswer.setBackground(getResources().getDrawable(R.drawable.pink0_rounded_border_4sp));
                tvNoti.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void initUi() {
        ivForward = findViewById(R.id.ivForward);
        ivBackward = findViewById(R.id.ivBackward);
        bAnswer = findViewById(R.id.bAnswer);
        tvNoti = findViewById(R.id.tvNoti);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvCurrentFlashcard = findViewById(R.id.tvCurrentFlashcard);
        etAnswer = findViewById(R.id.etAnswer);
        seekBar = findViewById(R.id.seekBar);
    }

    private void flipFlashcard() {
        if (tvQuestion.getText().equals(flashcardList.get(currentFlashcardIndex).getQuestion())) {
            tvQuestion.setText(flashcardList.get(currentFlashcardIndex).getAnswer());
        } else {
            tvQuestion.setText(flashcardList.get(currentFlashcardIndex).getQuestion());
        }
    }

    private void showFlashcard(int index) {
        if (index >= 0 && index < flashcardList.size()) {
            Flashcard flashcard = flashcardList.get(index);
            tvQuestion.setText(flashcard.getQuestion());
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

    private void loadFlashcards() {
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
                            flashcardList.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Flashcard flashcard = document.toObject(Flashcard.class);
                                flashcardList.add(flashcard);
                            }
                            seekBar.setMax(flashcardList.size() - 1);
                            if (!flashcardList.isEmpty()) {
                                showFlashcard(currentFlashcardIndex);
                            } else {
                                Toast.makeText(PraticeActivity.this, "Không có flashcard nào", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PraticeActivity.this, "Không thể lấy dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}