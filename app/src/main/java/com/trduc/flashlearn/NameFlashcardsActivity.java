package com.trduc.flashlearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NameFlashcardsActivity extends AppCompatActivity {

    Button bContinue;
    EditText etFlashcardSetsName;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_flashcards);

        bContinue = findViewById(R.id.bContinue);
        etFlashcardSetsName = findViewById(R.id.etFlashcardSetsName);
        db = FirebaseFirestore.getInstance();

        bContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String flashcardSetsName = etFlashcardSetsName.getText().toString();
                if (!flashcardSetsName.isEmpty()) {
                    createNewFlashcardSet(flashcardSetsName);
                } else {
                    Toast.makeText(NameFlashcardsActivity.this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createNewFlashcardSet(final String flashcardSetsName) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            final String userEmail = currentUser.getEmail();

            final String flashcardSetsId = UUID.randomUUID().toString();

            final Map<String, Object> flashcardSet = new HashMap<>();
            flashcardSet.put("id", flashcardSetsId);
            flashcardSet.put("name", flashcardSetsName);

            db.collection("users")
                    .document(userEmail)
                    .collection("flashcard_sets")
                    .document(flashcardSetsId)
                    .set(flashcardSet)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(NameFlashcardsActivity.this, CreateFlashcardsActivity.class)
                                    .putExtra("flashcardSetsId", flashcardSetsId)
                            );
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NameFlashcardsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
