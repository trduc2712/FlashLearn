package com.trduc.flashlearn.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trduc.flashlearn.Adapters.AllFlashcardSetsAdapter;
import com.trduc.flashlearn.Models.User;
import com.trduc.flashlearn.R;

public class QuestionActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView ivBars, ivProfilePicture;
    LinearLayout lnHome, lnCreate, lnSignOut, lnEditFlashcardSets, lnAdd, lnDelete, lnEdit, lnSetting, lnSubItem;
    LinearLayout lnSecurity, lnQuestion, lnShare, lnSupport,lnFilter;
    TextView tvEmail, tvUsername, tvTitle;
    FirebaseFirestore db;
    ListView lvAllFlashcardSets;
    FirebaseAuth auth;
    AllFlashcardSetsAdapter adapter;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        initUi();
        showInformationUser();


        ivBars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        lnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(QuestionActivity.this, MainActivity.class);
            }
        });

        lnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(QuestionActivity.this, SettingActivity.class);
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

        lnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(QuestionActivity.this, SupportActivity.class);
//                recreate();
            }
        });

        lnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(QuestionActivity.this, SecurityActivity.class);;
            }
        });

        lnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer(drawerLayout);
            }
        });
        builder = new AlertDialog.Builder(this);
        lnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Thông báo")
                        .setMessage("Bạn chắc chắn muốn đăng xuất ?")
                        .setCancelable(true)
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                finish();
                                redirectActivity(QuestionActivity.this, SignInActivity.class);;
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
        lnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String choice = "Filter a flashcard sets";
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("choice", choice);
                editor.apply();
                Intent intent = new Intent(QuestionActivity.this, FilterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initUi() {
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
        lnSetting = findViewById(R.id.lnSetting);
        drawerLayout = findViewById(R.id.drawerLayout);
        ivBars = findViewById(R.id.ivBars);
        lnSupport = findViewById(R.id.lnSupport);
        lnShare = findViewById(R.id.lnShare);
        lnQuestion = findViewById(R.id.lnQuestion);
        lnSecurity = findViewById(R.id.lnSecurity);
        lnFilter=findViewById(R.id.lnFilter);
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
                Glide.with(QuestionActivity.this).load(user.getPhotoUrl()).into(ivProfilePicture);
                tvEmail.setText(email);
                tvUsername.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}