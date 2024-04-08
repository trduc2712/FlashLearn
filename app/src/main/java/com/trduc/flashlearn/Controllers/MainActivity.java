package com.trduc.flashlearn.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trduc.flashlearn.Adapters.AllFlashcardSetsAdapter;
import com.trduc.flashlearn.Models.FlashcardSets;
import com.trduc.flashlearn.Models.User;
import com.trduc.flashlearn.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView ivBars, ivProfilePicture;
    LinearLayout lnHome, lnCreate, lnSignOut, lnEditFlashcardSets, lnAdd, lnDelete, lnEdit, lnSetting, lnSubItem;
    LinearLayout lnSecurity, lnQuestion, lnShare, lnSupport, lnChangeNameFlashcardSets, lnSearch, lnPratice,lnFilter;
    TextView tvEmail, tvUsername, tvTitle;
    FirebaseFirestore db;
    ListView lvAllFlashcardSets;
    FirebaseAuth auth;
    AllFlashcardSetsAdapter adapter;
    String choice = "Learn flashcard sets";

//    private static final String[] languages = {"EN/VI", "EN", "VI"};
//
//    Spinner spinner_language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
        showInformationUser();
        addNewUserToFirestore();

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("choice", choice);
        editor.apply();



//        ArrayAdapter<String> adapter_home = new ArrayAdapter<String>(this, R.layout.simple_spinner, languages);
//        adapter_home.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_language.setAdapter(adapter_home);
//        spinner_language.setSelection(0);
//
//        spinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selected_language = parent.getItemAtPosition(position).toString();
//                if (selected_language.equals("EN")) {
//                    setLocal(MainActivity.this, "en");
//                    Toast.makeText(getApplicationContext(), "English", Toast.LENGTH_SHORT).show();
//                    finish();
//                    startActivity(getIntent());
//                } else if (selected_language.equals("VI")) {
//                    setLocal(MainActivity.this, "vi");
//                    Toast.makeText(getApplicationContext(), "Việt Nam", Toast.LENGTH_SHORT).show();
//                    finish();
//                    startActivity(getIntent());
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        ivBars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        lnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer(drawerLayout);
            }
        });

        lnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, SettingActivity.class);
            }
        });

        lnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Hello");
                intent.setType("text/plain");

                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        lnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, SecurityActivity.class);
            }
        });

        lnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, SupportActivity.class);
            }
        });

        lnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, QuestionActivity.class);;
            }
        });

        lnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        lnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NameFlashcardsActivity.class);
                startActivity(intent);
            }
        });

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
                                lvAllFlashcardSets.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

        adapter = new AllFlashcardSetsAdapter(flashcardSetsList);
        lvAllFlashcardSets.setAdapter(adapter);

        lnEditFlashcardSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lnSubItem.getVisibility() == View.GONE) {
                    lnSubItem.setVisibility(View.VISIBLE);
                } else {
                    lnSubItem.setVisibility(View.GONE);
                }
            }
        });

        lnChangeNameFlashcardSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "Change flashcard sets's name";
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("choice", choice);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, AllFlashcardSetsActivity.class);
                startActivity(intent);
            }
        });

        lnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "Search a flashcard sets";
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("choice", choice);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        lnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "Filter a flashcard sets";
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("choice", choice);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(intent);
            }
        });

        lnEditFlashcardSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice = "Delete a flashcard sets";
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("choice", choice);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, DeleteFlashcardSetsActivity.class);
                startActivity(intent);
            }
        });

        lnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "Add flashcards";
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("choice", choice);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, BeforeAddFlashcardsActivity.class);
                startActivity(intent);
            }
        });

        lnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice = "Delete flashcards";
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("choice", choice);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, BeforeDeleteFlashcardsActivity.class);
                startActivity(intent);
            }
        });

    }
    private void initUi(){
        lnHome = findViewById(R.id.lnHome);
        lnCreate = findViewById(R.id.lnCreate);
        lvAllFlashcardSets = findViewById(R.id.lvAllFlashcardSets);
        lnSignOut = findViewById(R.id.lnSignOut);
        lnEditFlashcardSets = findViewById(R.id.lnEditFlashcardSets);
        lnAdd = findViewById(R.id.lnAdd);
        lnDelete = findViewById(R.id.lnDelete);
        lnEdit = findViewById(R.id.lnEdit);
        lnSubItem = findViewById(R.id.lnSubItem);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvEmail = findViewById(R.id.tvEmail);
        tvUsername = findViewById(R.id.tvUsername);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        tvTitle=findViewById(R.id.tvTitle);
        lnPratice = findViewById(R.id.lnPratice);
//        tvTitle.setText("Trang chủ");
        lnSetting = findViewById(R.id.lnSetting);
        drawerLayout = findViewById(R.id.drawerLayout);
        ivBars = findViewById(R.id.ivBars);
        lnSupport = findViewById(R.id.lnSupport);
        lnShare = findViewById(R.id.lnShare);
        lnQuestion = findViewById(R.id.lnQuestion);
        lnFilter=findViewById(R.id.lnFilter);
        lnSecurity = findViewById(R.id.lnSecurity);
        lnChangeNameFlashcardSets = findViewById(R.id.lnChangeNameFlashcardSets);
        lnSearch = findViewById(R.id.lnSearch);
    }

//    public void setLocal(Activity activity, String langCode) {
//        Locale locale = new Locale(langCode);
//        locale.setDefault(locale);
//        Resources resources = activity.getResources();
//        Configuration config = resources.getConfiguration();
//        config.setLocale(locale);
//        resources.updateConfiguration(config, resources.getDisplayMetrics());
//    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFlashcardSets();
        choice = "Learn flashcard sets";
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("choice", choice);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    private void showInformationUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String userID = user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userprofile = snapshot.getValue(User.class);
                String email = user.getEmail();
                String name = userprofile.getUsername();
                Glide.with(MainActivity.this).load(user.getPhotoUrl()).into(ivProfilePicture);
                tvEmail.setText(email);
                tvUsername.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNewUserToFirestore() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            DocumentReference userRef = db.collection("users").document(userEmail);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        System.out.println("Nguoi dung da ton tai");
                    } else {
                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("email", userEmail);

                        CollectionReference usersRef = db.collection("users");
                        usersRef.document(userEmail).set(newUser)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        System.out.println("Da them nguoi dung moi thanh cong");
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to add user to Firestore", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error checking user in Firestore", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadFlashcardSets() {
        ArrayList<FlashcardSets> flashcardSetsList = new ArrayList<>();
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
                                flashcardSetsList.clear();
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    FlashcardSets flashcardSets = document.toObject(FlashcardSets.class);
                                    flashcardSetsList.add(flashcardSets);
                                }
                                adapter = new AllFlashcardSetsAdapter(flashcardSetsList);
                                lvAllFlashcardSets.setAdapter(adapter);
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
    }

}
