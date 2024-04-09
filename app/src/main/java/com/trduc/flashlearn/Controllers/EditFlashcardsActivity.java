package com.trduc.flashlearn.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trduc.flashlearn.Adapters.AlreadyCreateAdapter;
import com.trduc.flashlearn.Models.Flashcard;
import com.trduc.flashlearn.R;

import java.util.ArrayList;

public class EditFlashcardsActivity extends AppCompatActivity {

    ArrayList<Flashcard> flashcardList, deletedFlashcards, editedFlashcards;
    ListView lvAlreadyCreate;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String flashcardSetsId;
    Button bSave, bCancel;
    AlreadyCreateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flashcards);

        bSave = findViewById(R.id.bSave);
        bCancel = findViewById(R.id.bCancel);
        flashcardList = new ArrayList<>();
        deletedFlashcards = new ArrayList<>();
        editedFlashcards = new ArrayList<>();
        lvAlreadyCreate = findViewById(R.id.lvAlreadyCreate);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        if (intent != null) {
            flashcardSetsId = intent.getStringExtra("flashcardSetsId");
            System.out.println(flashcardSetsId);
        }

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                db.collection("users")
                        .document(userEmail)
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
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        }

        String choice = "Edit";
        adapter = new AlreadyCreateAdapter(flashcardList, flashcardSetsId, choice, deletedFlashcards, editedFlashcards);
        lvAlreadyCreate.setAdapter(adapter);

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditFlashcardsActivity.this, BeforeEditFlashcardsActivity.class);
                startActivity(intent);
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditFlashcardsActivity.this, BeforeEditFlashcardsActivity.class);
                startActivity(intent);
            }
        });

    }
}