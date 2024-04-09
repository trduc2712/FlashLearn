package com.trduc.flashlearn.Controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trduc.flashlearn.Models.User;
import com.trduc.flashlearn.R;

import com.bumptech.glide.Glide;

public class SearchUserActivity extends AppCompatActivity {

    Button bSearch;
    TextView tvTitle, tvUsername;
    EditText etUsername;
    LinearLayout lnUser;
    FirebaseFirestore db;
    ImageView ivProfilePicture;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        initUi();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                if (username.isEmpty()) {
                    Toast.makeText(SearchUserActivity.this, "Vui lòng nhập tên người dùng", Toast.LENGTH_SHORT).show();
//                    lnUser.setVisibility(View.INVISIBLE);
                    return;
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String userId;
                            String usernameSearched = userSnapshot.child("username").getValue(String.class);
                            String email = userSnapshot.child("email").getValue(String.class);
                            String password = userSnapshot.child("password").getValue(String.class);
                            System.out.println("ID: " + userSnapshot.getKey() + ", Username: " + username + ", Email: " + email + ", Password: " + password);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    private void initUi() {
        bSearch = findViewById(R.id.bSearch);
        tvTitle = findViewById(R.id.tvTitle);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUsername = findViewById(R.id.tvUsername);
        etUsername = findViewById(R.id.etUsername);
        lnUser = findViewById(R.id.lnUser);
    }
}