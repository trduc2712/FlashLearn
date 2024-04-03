package com.trduc.flashlearn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    TextView tvTitle;
    Button bSearch;
    EditText etFlashcardSetsName;
    ListView lvSearchFlashcardSets;
    ArrayList<FlashcardSets> flashcardSetsList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tvTitle = findViewById(R.id.tvTitle);
        bSearch = findViewById(R.id.bSearch);
        etFlashcardSetsName = findViewById(R.id.etFlashcardSetsName);
        flashcardSetsList = new ArrayList<>();
        lvSearchFlashcardSets = findViewById(R.id.lvSearchFlashcardSets);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                db.collection("users")
                        .document(userEmail)
                        .collection("flashcard_sets")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    FlashcardSets flashcardSets = document.toObject(FlashcardSets.class);
                                    flashcardSetsList.add(flashcardSets);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SearchActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

        adapter = new SearchAdapter(flashcardSetsList);

        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchKeyword = etFlashcardSetsName.getText().toString().trim();
                if (!searchKeyword.isEmpty()) {
                    searchFlashcardSets(searchKeyword);
                } else {
                    Toast.makeText(SearchActivity.this, "Vui lòng nhập tên bộ flashcard cần tìm", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void searchFlashcardSets(String keyword) {
        flashcardSetsList.clear();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db.collection("users")
                .document(currentUser.getEmail())
                .collection("flashcard_sets")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            FlashcardSets flashcardSets = document.toObject(FlashcardSets.class);
                            String setName = flashcardSets.getName().toLowerCase();
                            if (setName.contains(keyword.toLowerCase())) {
                                flashcardSetsList.add(flashcardSets);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        lvSearchFlashcardSets.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SearchActivity.this, "Lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}