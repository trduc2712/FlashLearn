package com.trduc.flashlearn.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class SupportActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView ivBars, ivProfilePicture,ivMap;
    LinearLayout lnHome, lnCreate, lnSignOut, lnEditFlashcardSets, lnAdd, lnDelete, lnEdit, lnSetting, lnSubItem;
    LinearLayout lnSecurity, lnQuestion, lnShare, lnSupport, lnSearch, lnChangeNameFlashcardSets, lnPratice, lnFilter, lnDeleteFlashcardSets;
    TextView tvEmail, tvUsername, tvTitle,tvphone,tvcall;
    String choice = "Learn flashcard sets";
    FirebaseFirestore db;
    ListView lvAllFlashcardSets;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        initUi();
        showInformationUser();

        tvcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sdt = tvphone.getText().toString();
                Intent intent_call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+sdt));
                startActivity(intent_call);
            }
        });
        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.google.com/maps/place/%C4%90%E1%BA%A1i+H%E1%BB%8Dc+Thu%E1%BB%B7+L%E1%BB%A3i+-+175+T%C3%A2y+S%C6%A1n+(C%E1%BB%99t+Sau)/@21.007651,105.8238196,15z/data=!4m6!3m5!1s0x3135ac81847527d9:0x608eb25e26856d92!8m2!3d21.007651!4d105.8238196!16s%2Fg%2F1hhx6qmkx?entry=ttu");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
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
                closeDrawer(drawerLayout);
                Intent intent = new Intent(SupportActivity.this, MainActivity.class);
                startActivity(intent);;
            }
        });


        lnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SupportActivity.this, NameFlashcardsActivity.class);
                startActivity(intent);
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

        lnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "Add flashcards";
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("choice", choice);
                editor.apply();
                Intent intent = new Intent(SupportActivity.this, BeforeAddFlashcardsActivity.class);
                startActivity(intent);
            }
        });

        lnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice = "Edit flashcards";
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("choice", choice);
                editor.apply();
                Intent intent = new Intent(SupportActivity.this, BeforeEditFlashcardsActivity.class);
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
                Intent intent = new Intent(SupportActivity.this, BeforeDeleteFlashcardsActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(SupportActivity.this, AllFlashcardSetsActivity.class);
                startActivity(intent);
            }
        });

        lnDeleteFlashcardSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice = "Delete a flashcard sets";
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("choice", choice);
                editor.apply();
                Intent intent = new Intent(SupportActivity.this, AllFlashcardSetsActivity.class);
                startActivity(intent);
            }
        });

        lnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(SupportActivity.this, SettingActivity.class);
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
                Intent intent = new Intent(SupportActivity.this, SearchActivity.class);
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
                Intent intent = new Intent(SupportActivity.this, FilterActivity.class);
                startActivity(intent);
            }
        });

        lnPratice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice = "Pratice flashcards";
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("choice", choice);
                editor.apply();
                Intent intent = new Intent(SupportActivity.this, BeforePraticeActivity.class);
                startActivity(intent);
            }
        });

        lnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Hello");
                intent.setType("text/plain");

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        lnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(SupportActivity.this, SupportActivity.class);
            }
        });

//        lnSecurity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                redirectActivity(SupportActivity.this, SecurityActivity.class);
//            }
//        });
//
//        lnQuestion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                redirectActivity(SupportActivity.this, QuestionActivity.class);
//            }
//        });

        lnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.dialog_sign_out);

                final AlertDialog dialog = builder.create();
                dialog.show();

                Button bYes = dialog.findViewById(R.id.bYes);
                Button bNo = dialog.findViewById(R.id.bNo);

                bYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SupportActivity.this, SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                });

                bNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
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
//        tvTitle.setText("Hỗ trợ");
        lnSetting = findViewById(R.id.lnSetting);
        drawerLayout = findViewById(R.id.drawerLayout);
        ivBars = findViewById(R.id.ivBars);
        lnSupport = findViewById(R.id.lnSupport);
        lnShare = findViewById(R.id.lnShare);
//        lnQuestion = findViewById(R.id.lnQuestion);
//        lnSecurity = findViewById(R.id.lnSecurity);
        lnFilter=findViewById(R.id.lnFilter);
        lnDeleteFlashcardSets = findViewById(R.id.lnDeleteFlashcardSets);
        lnPratice = findViewById(R.id.lnPratice);
        lnChangeNameFlashcardSets = findViewById(R.id.lnChangeNameFlashcardSets);
        lnSearch = findViewById(R.id.lnSearch);
        ivMap=findViewById(R.id.ivmap);
        tvphone=findViewById(R.id.tvphone);
        tvcall=findViewById(R.id.tvcall);
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
                Glide.with(SupportActivity.this).load(user.getPhotoUrl()).into(ivProfilePicture);
                tvEmail.setText(email);
                tvUsername.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}