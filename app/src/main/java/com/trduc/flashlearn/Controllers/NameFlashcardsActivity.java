package com.trduc.flashlearn.Controllers;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.trduc.flashlearn.R;

import java.util.HashMap;
import java.util.Map;

public class NameFlashcardsActivity extends AppCompatActivity {

    Button bContinue;
    EditText etFlashcardSetsName;
    FirebaseFirestore db;
    Spinner sTopic;
    String[] choices = {"Chủ đề", "Toán học", "Văn học", "Ngôn ngữ", "Vật lý", "Hoá học", "Sinh học", "Lịch sử", "Địa lý", "Nghệ thuật",
    "Thể thao", "Y học", "Công nghê", "Ca dao tục ngữ", "Chính trị", "Tài chính", "Tâm lý", "Kinh doanh", "Kỹ thuật"};
    String selectedChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_flashcards);

        sTopic = findViewById(R.id.sTopic);
        sTopic.setSelection(0, false);
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
                if (flashcardSetsTopic.equals("Chủ đề")) {
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

//        if (currentUser != null) {
//            final String userEmail = currentUser.getEmail();
//
//            db.collection("users")
//                    .document(userEmail)
//                    .collection("flashcard_sets")
//                    .get()
//                    .addOnSuccessListener(queryDocumentSnapshots -> {
//                        int numFlashcardSets = queryDocumentSnapshots.size();
//                        char newFlashcardSetId = (char) ('a' + numFlashcardSets);
//
//                        char finalNewFlashcardSetId = newFlashcardSetId;
//
//                        boolean idExists = false;
//                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                            String flashcardSetId = document.getString("id");
//                            if (flashcardSetId.equals(String.valueOf(finalNewFlashcardSetId))) {
//                                idExists = true;
//                                break;
//                            }
//                        }
//
//                        while (idExists) {
//                            finalNewFlashcardSetId = (char) ('a' + ++numFlashcardSets);
//                            idExists = false;
//                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                                String flashcardSetId = document.getString("id");
//                                if (flashcardSetId.equals(String.valueOf(finalNewFlashcardSetId))) {
//                                    idExists = true;
//                                    break;
//                                }
//                            }
//                        }
//
//                        final char finalFinalNewFlashcardSetId = finalNewFlashcardSetId;
//                        final Map<String, Object> flashcardSet = new HashMap<>();
//                        flashcardSet.put("id", String.valueOf(finalFinalNewFlashcardSetId));
//                        flashcardSet.put("name", flashcardSetsName);
//                        flashcardSet.put("topic", selectedChoice);
//
//                        db.collection("users")
//                                .document(userEmail)
//                                .collection("flashcard_sets")
//                                .document(String.valueOf(finalFinalNewFlashcardSetId))
//                                .set(flashcardSet)
//                                .addOnSuccessListener(aVoid -> {
//                                    startActivity(new Intent(NameFlashcardsActivity.this, CreateFlashcardsActivity.class)
//                                            .putExtra("flashcardSetsId", String.valueOf(finalFinalNewFlashcardSetId))
//                                    );
//                                    finish();
//                                })
//                                .addOnFailureListener(e -> Toast.makeText(NameFlashcardsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//                    })
//                    .addOnFailureListener(e -> Toast.makeText(NameFlashcardsActivity.this, "Không thể lấy dữ liệu flashcard_sets", Toast.LENGTH_SHORT).show());
//        }

        if (currentUser != null) {
            final String userEmail = currentUser.getEmail();

            db.collection("users")
                    .document(userEmail)
                    .collection("flashcard_sets")
                    .orderBy("id", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        char newFlashcardSetId;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            char lastFlashcardSetId = lastDocument.getString("id").charAt(0);
                            newFlashcardSetId = (char) (lastFlashcardSetId + 1);
                        } else {
                            newFlashcardSetId = 'a';
                        }

                        char finalNewFlashcardSetId = newFlashcardSetId;

                        final Map<String, Object> flashcardSet = new HashMap<>();
                        flashcardSet.put("id", String.valueOf(finalNewFlashcardSetId));
                        flashcardSet.put("name", flashcardSetsName);
                        flashcardSet.put("topic", selectedChoice);

                        db.collection("users")
                                .document(userEmail)
                                .collection("flashcard_sets")
                                .document(String.valueOf(finalNewFlashcardSetId))
                                .set(flashcardSet)
                                .addOnSuccessListener(aVoid -> {
                                    startActivity(new Intent(NameFlashcardsActivity.this, CreateFlashcardsActivity.class)
                                            .putExtra("flashcardSetsId", String.valueOf(finalNewFlashcardSetId))
                                    );
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(NameFlashcardsActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(NameFlashcardsActivity.this, "Không thể lấy dữ liệu flashcard_sets", Toast.LENGTH_SHORT).show());
        }


    }


}