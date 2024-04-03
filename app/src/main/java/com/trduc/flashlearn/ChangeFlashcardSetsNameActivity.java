package com.trduc.flashlearn;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class ChangeFlashcardSetsNameActivity extends AppCompatActivity {

    TextView tvTitle;
    EditText etFlashcardSetsNewName;
    Button bChange;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_flashcard_sets_name);

        Intent intent = getIntent();
        String flashcardSetsName = intent.getStringExtra("flashcardSetsName");
        String flashcardSetsId = intent.getStringExtra("flashcardSetsId");

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        tvTitle = findViewById(R.id.tvTitle);
        etFlashcardSetsNewName = findViewById(R.id.etFlashcardSetsNewName);
        bChange = findViewById(R.id.bChange);

        String newName = etFlashcardSetsNewName.getText().toString();
        tvTitle.setText("Bạn đang đổi tên cho bộ flashcard " + flashcardSetsName);

        bChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etFlashcardSetsNewName.getText().toString();

                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser != null) {
                    String userEmail = currentUser.getEmail();
                    if (userEmail != null) {
                        CollectionReference flashcardSetsRef = db.collection("users").document(userEmail).collection("flashcard_sets");
                        DocumentReference flashcardSetsDocRef = flashcardSetsRef.document(flashcardSetsId);

                        Map<String, Object> newData = new HashMap<>();
                        newData.put("name", newName);

                        Task<Void> voidTask = flashcardSetsDocRef.update(newData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(ChangeFlashcardSetsNameActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                });
                    }
                }
            }
        });

    }
}