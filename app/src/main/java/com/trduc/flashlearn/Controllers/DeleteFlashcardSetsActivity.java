package com.trduc.flashlearn.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trduc.flashlearn.Adapters.AllFlashcardSetsAdapter;
import com.trduc.flashlearn.Models.FlashcardSets;
import com.trduc.flashlearn.R;

import java.util.ArrayList;

public class DeleteFlashcardSetsActivity extends AppCompatActivity {

    ListView lvAllFlashcardSets;
    Button bFlashcardSetsName;
    FirebaseAuth auth;
    AllFlashcardSetsAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_flashcard_sets);

        lvAllFlashcardSets = findViewById(R.id.lvAllFlashcardSets);

        ArrayList<FlashcardSets> flashcardSetsList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

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
                                Toast.makeText(DeleteFlashcardSetsActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

        adapter = new AllFlashcardSetsAdapter(flashcardSetsList);
        lvAllFlashcardSets.setAdapter(adapter);

    }
}