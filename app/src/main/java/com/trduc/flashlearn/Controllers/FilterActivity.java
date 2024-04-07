package com.trduc.flashlearn.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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

public class FilterActivity extends AppCompatActivity {
    Spinner spinner_topic;
    String[] choices = {"Vui lòng chọn chủ đề", "Toán học", "Văn học", "Ngôn ngữ", "Vật lý", "Hoá học", "Sinh học", "Lịch sử", "Địa lý", "Nghệ thuật",
            "Thể thao", "Y học", "Công nghê", "Ca dao tục ngữ", "Chính trị", "Tài chính", "Tâm lý", "Kinh doanh", "Kỹ thuật"};
    String selectedChoice;
    ListView lvFilterFlashcardSets;
    ArrayList<FlashcardSets> flashcardSetsList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        spinner_topic=findViewById(R.id.spinner_Topic);
        ArrayAdapter<String> adapter_language = new ArrayAdapter<String>(this, R.layout.simple_spinner, choices);
        adapter_language.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_topic.setAdapter(adapter_language);
        spinner_topic.setSelection(0);

        flashcardSetsList = new ArrayList<>();
        lvFilterFlashcardSets = findViewById(R.id.lvFilterFlashcardSets);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        adapter = new SearchAdapter(flashcardSetsList);

        spinner_topic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String topic = spinner_topic.getSelectedItem().toString();
                filterFlashcardSets(topic);
                if(topic!="Vui lòng chọn chủ đề"){
                    Toast.makeText(FilterActivity.this, "Chủ đề "+topic, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void filterFlashcardSets(String keytopic) {
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
                                flashcardSetsList.clear();
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    FlashcardSets flashcardSets = document.toObject(FlashcardSets.class);
                                    if(flashcardSets.getTopic().equals(keytopic)) {
                                        flashcardSetsList.add(flashcardSets);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                lvFilterFlashcardSets.setAdapter(adapter);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FilterActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FilterActivity.this, MainActivity.class);
        startActivity(intent);
    }

}