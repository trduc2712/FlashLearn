package com.trduc.flashlearn.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
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

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView ivBars, ivProfilePicture;
    LinearLayout lnHome, lnCreate, lnSignOut, lnEditFlashcardSets, lnAdd, lnDelete, lnEdit, lnSetting, lnSubItem;
    LinearLayout lnSecurity, lnQuestion, lnShare, lnSupport, lnSearch;
    TextView tvEmail, tvUsername, tvTitle;
    FirebaseFirestore db;
    ListView lvAllFlashcardSets;
    FirebaseAuth auth;
    AllFlashcardSetsAdapter adapter;
    Switch sDarkMode;

    String choice = "Learn flashcard sets";
    private static final String[] languages = {"EN/VI", "EN", "VI"};

    Spinner spinner_language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUi();
        showInformationUser();
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            sDarkMode.setChecked(true);
        } else {
            sDarkMode.setChecked(false);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_language.setAdapter(adapter);
        spinner_language.setSelection(0);
        spinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_language = parent.getItemAtPosition(position).toString();
                if (selected_language.equals("EN")) {
                    setLocal(SettingActivity.this, "en");
                    finish();
                    startActivity(getIntent());
                    Toast.makeText(getApplicationContext(), "English", Toast.LENGTH_SHORT).show();
                } else if (selected_language.equals("VI")) {
                    setLocal(SettingActivity.this, "vi");
                    finish();
                    startActivity(getIntent());
                    Toast.makeText(getApplicationContext(), "Việt Nam", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ivBars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        lnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(SettingActivity.this, MainActivity.class);
            }
        });

        lnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer(drawerLayout);
//                recreate();
            }
        });

        lnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(SettingActivity.this, ShareActivity.class);
            }
        });

        lnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(SettingActivity.this, SecurityActivity.class);
            }
        });

        lnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(SettingActivity.this, QuestionActivity.class);
            }
        });

        lnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        lnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(SettingActivity.this, SupportActivity.class);
            }
        });

        sDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else if (!isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

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

        lnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "Search a flashcard sets";
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("choice", choice);
                editor.apply();
                Intent intent = new Intent(SettingActivity.this, AllFlashcardSetsActivity.class);
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
        lnSearch = findViewById(R.id.lnSearch);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvEmail = findViewById(R.id.tvEmail);
        tvUsername = findViewById(R.id.tvUsername);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        tvTitle = findViewById(R.id.tvTitle);
        lnSetting = findViewById(R.id.lnSetting);
        drawerLayout = findViewById(R.id.drawerLayout);
        ivBars = findViewById(R.id.ivBars);
        lnSupport = findViewById(R.id.lnSupport);
        lnShare = findViewById(R.id.lnShare);
        lnQuestion = findViewById(R.id.lnQuestion);
        lnSecurity = findViewById(R.id.lnSecurity);
        sDarkMode = findViewById(R.id.sDarkMode);
        spinner_language = findViewById(R.id.spinner_language);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
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
    public void setLocal(Activity activity, String langCode) {
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
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
                Glide.with(SettingActivity.this).load(user.getPhotoUrl()).into(ivProfilePicture);
                tvEmail.setText(email);
                tvUsername.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}