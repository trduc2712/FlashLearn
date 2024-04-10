package com.trduc.flashlearn.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trduc.flashlearn.Adapters.SearchAdapter;
import com.trduc.flashlearn.Models.FlashcardSets;
import com.trduc.flashlearn.R;

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
        etFlashcardSetsName = findViewById(R.id.etFlashcardSetsName);
        flashcardSetsList = new ArrayList<>();
        lvSearchFlashcardSets = findViewById(R.id.lvSearchFlashcardSets);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        adapter = new SearchAdapter(flashcardSetsList);

        etFlashcardSetsName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchKeyword = s.toString().trim();
                if (!searchKeyword.isEmpty()) {
                    searchFlashcardSets(searchKeyword);
                } else {
                    flashcardSetsList.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });


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
                                flashcardSetsList.clear();
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void searchFlashcardSets(String keyword) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db.collection("users")
                .document(currentUser.getEmail())
                .collection("flashcard_sets")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!flashcardSetsList.isEmpty()) {
                            flashcardSetsList.clear();
                        }
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            FlashcardSets flashcardSets = document.toObject(FlashcardSets.class);
                            String setName = flashcardSets.getName().toLowerCase();
                            if (setName.contains(keyword.toLowerCase())) {
                                flashcardSetsList.add(flashcardSets);
                            }
                        }
                        int i = 0;
                        for (FlashcardSets flashcardSets : flashcardSetsList) {
                            System.out.print(i + ": ");
                            System.out.println(flashcardSets.getName());
                            i++;
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
