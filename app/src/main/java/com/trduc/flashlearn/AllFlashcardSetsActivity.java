package com.trduc.flashlearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllFlashcardSetsActivity extends AppCompatActivity {

    ListView lvAllFlashcardSets;
    ArrayList<FlashcardSets> flashcardSetsList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    AllFlashcardSetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_flashcard_sets);

        flashcardSetsList = new ArrayList<>();
        lvAllFlashcardSets = findViewById(R.id.lvAllFlashcardSets);
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
                                Toast.makeText(AllFlashcardSetsActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

        adapter = new AllFlashcardSetsAdapter(flashcardSetsList);
        lvAllFlashcardSets.setAdapter(adapter);

    }
}