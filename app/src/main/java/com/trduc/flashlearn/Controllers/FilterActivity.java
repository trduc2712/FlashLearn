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
    String[] choices = {"Chủ đề", "Toán học", "Văn học", "Ngôn ngữ", "Vật lý", "Hoá học", "Sinh học", "Lịch sử", "Địa lý", "Nghệ thuật",
            "Thể thao", "Y học", "Công nghê", "Ca dao tục ngữ", "Chính trị", "Tài chính", "Tâm lý", "Kinh doanh", "Kỹ thuật"};
    String selectedChoice;
    ListView lvFilterFlashcardSets;
    ArrayList<FlashcardSets> flashcardSetsList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    SearchAdapter adapter;



}