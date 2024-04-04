package com.trduc.flashlearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
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

public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    DrawerLayout drawerLayout;
    ImageView ivBars, ivProfilePicture;
    LinearLayout lnHome, lnCreate, lnSignOut, lnEditFlashcardSets, lnAdd, lnDelete, lnEdit, lnSetting, lnSubItem;
    LinearLayout lnSecurity, lnQuestion, lnShare, lnSupport;
    TextView tvEmail, tvUsername, tvTitle;
    FirebaseFirestore db;
    ListView lvAllFlashcardSets;
    FirebaseAuth auth;
    AllFlashcardSetsAdapter adapter;
    Switch sDarkMode;

    Spinner spinner_language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUi();
        showInformationUser();
        if( AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            sDarkMode.setChecked(true);
        }
        else {
            sDarkMode.setChecked(false);
        }

        spinner_language = findViewById(R.id.spinner_language);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.language, R.layout.simple_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_language.setAdapter(adapter);
        spinner_language.setOnItemSelectedListener(this);
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
                redirectActivity(SettingActivity.this, QuestionActivity.class);;
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
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else if(!isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(SettingActivity.this, "Logout", Toast.LENGTH_SHORT).show();
//            }
//        });

//        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(aSwitch.isChecked()) {
//                    textView.setText("On");
//                } else {
//                    textView.setText("Off");
//                }
//            }
//        });
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
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Cài đặt");
        lnSetting = findViewById(R.id.lnSetting);
        drawerLayout = findViewById(R.id.drawerLayout);
        ivBars = findViewById(R.id.ivBars);
        lnSupport = findViewById(R.id.lnSupport);
        lnShare = findViewById(R.id.lnShare);
        lnQuestion = findViewById(R.id.lnQuestion);
        lnSecurity = findViewById(R.id.lnSecurity);
        sDarkMode=findViewById(R.id.sDarkMode);
        spinner_language=findViewById(R.id.spinner_language);
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
                Glide.with(SettingActivity.this).load(user.getPhotoUrl()).into(ivProfilePicture);
                tvEmail.setText(email);
                tvUsername.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected_language = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, selected_language, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}