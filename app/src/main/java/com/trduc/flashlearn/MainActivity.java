package com.trduc.flashlearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu, ivProfilePicture;
    LinearLayout home, setting, share, about, sign_out,allcart, security, question;
    TextView tvEmail, tvUsername,tvTittle;
    Button bCreateFlashcards, bAllFlashcardSets;
    FirebaseAuth auth;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
        showInformationUser();
        addNewUserToFirestore();
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                recreate();
                closeDrawer(drawerLayout);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, SettingActivity.class);
            }
        });

        allcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, AllCartActivity.class);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, ShareActivity.class);
            }
        });
        security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, SecurityActivity.class);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, SupportActivity.class);
            }
        });
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(MainActivity.this, QuestionActivity.class);;
            }
        });

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        bCreateFlashcards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NameFlashcardsActivity.class);
                startActivity(intent);
            }
        });

        bAllFlashcardSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AllFlashcardSetsActivity.class);
                startActivity(intent);
            }
        });

    }
    private void initUi(){
        bAllFlashcardSets = findViewById(R.id.bAllFlashcardSets);
        tvEmail = findViewById(R.id.tvEmail);
        tvUsername = findViewById(R.id.tvUsername);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        about = findViewById(R.id.support);
        sign_out = findViewById(R.id.sign_out);
        setting = findViewById(R.id.setting);
        share = findViewById(R.id.share);
        question = findViewById(R.id.question);
        security = findViewById(R.id.security);
        allcart = findViewById(R.id.allcart);
        tvTittle=findViewById(R.id.tvTittle);
        bCreateFlashcards = findViewById(R.id.bCreateFlashcards);
    }

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
//                                        addFlashcardSetsForUser(userEmail);
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

//    private void addFlashcardSetsForUser(String userEmail) {
//        CollectionReference flashcardSetsRef = db.collection("users").document(userEmail).collection("flashcard_sets");
//
//        Map<String, Object> flashcardSet = new HashMap<>();
//        flashcardSet.put("id", "default");
//        flashcardSet.put("name", "Default Flashcard Set");
//
//        flashcardSetsRef.document("default").set(flashcardSet)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        System.out.println("Da them collection flashcard_sets cho nguoi dung moi");
//                        addFlashcardsForUser(userEmail);
//                    } else {
//                        Toast.makeText(MainActivity.this, "Failed to add flashcard sets to Firestore", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void addFlashcardsForUser(String userEmail) {
        CollectionReference flashcardsRef = db.collection("users").document(userEmail)
                .collection("flashcard_sets").document("default").collection("flashcards");

        Map<String, Object> flashcard = new HashMap<>();
        flashcard.put("question", "");
        flashcard.put("answer", "");

        flashcardsRef.document().set(flashcard)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Da them collection flashcards cho nguoi dung moi");
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to add flashcards to Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
