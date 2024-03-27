package com.trduc.flashlearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {
    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView loginForgot,login_sigup;
    LinearLayout loginfb,logingg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkUsername() | !checkPassword()) {
                } else {
                    checkUser();
                }
            }
        });
        login_sigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });
    }
    void init(){
        loginUsername=findViewById(R.id.edUsername_Login);
        loginPassword=findViewById(R.id.edPassword_Login);
        loginButton=findViewById(R.id.btnLogin);
        loginForgot=findViewById(R.id.tvForgotpass);
        login_sigup=findViewById(R.id.tvSigup_login);
        loginfb=findViewById(R.id.lnLoginFB);
        logingg=findViewById(R.id.lnLoginGG);


    }
    public Boolean checkUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Người dùng không đúng định dạng");
            Toast.makeText(SignInActivity.this, "Vui lòng nhập tên người dùng", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean checkPassword(){
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Mật khẩu không đúng định dạng");
            Toast.makeText(SignInActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }
    public void checkUser(){
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    loginUsername.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userPassword)) {
                        loginUsername.setError(null);

                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        loginPassword.setError("Sai mật khẩu");
                        loginPassword.requestFocus();
                    }
                } else {
                    loginUsername.setError("Người dùng không tồn tại");
                    loginUsername.requestFocus();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}