package com.trduc.flashlearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    Spinner sTopic;
    String[] choices = {"Không có", "Từ vựng", "Toán học", "Lịch sử", "Địa lý"};
    String selectedChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_flashcards);

        sTopic = findViewById(R.id.sTopic);
        bContinue = findViewById(R.id.bContinue);
        etFlashcardSetsName = findViewById(R.id.etFlashcardSetsName);
        db = FirebaseFirestore.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, choices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.sTopic);
        spinner.setAdapter(adapter);

        bContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String flashcardSetsTopic = sTopic.getSelectedItem().toString();
                if (flashcardSetsTopic.equals("Không có")) {
                    Toast.makeText(NameFlashcardsActivity.this, "Vui lòng chọn chủ đề", Toast.LENGTH_SHORT).show();
                    return;
                }

                String flashcardSetsName = etFlashcardSetsName.getText().toString();
                if (!flashcardSetsName.isEmpty()) {
                    createNewFlashcardSet(flashcardSetsName);
                } else {
                    Toast.makeText(NameFlashcardsActivity.this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedChoice = choices[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
            flashcardSet.put("topic", selectedChoice);

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
