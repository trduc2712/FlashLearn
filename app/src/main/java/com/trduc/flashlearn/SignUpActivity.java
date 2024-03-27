package com.trduc.flashlearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    TextView tvTitle, tvSignIn;
    EditText etEmail, etUsername, etPassword, etConfirmPassword;
    Button bSignUp;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tvTitle = findViewById(R.id.tvTitle);
        tvSignIn = findViewById(R.id.tvSignIn);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        bSignUp = findViewById(R.id.bSignUp);

        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (email.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập tên người dùng!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (confirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập lại mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Mật khẩu bạn nhập lại không đúng!", Toast.LENGTH_SHORT).show();
                    return;
                }

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                User user = new User(email, username, password);
                reference.child(username).setValue(user);

                Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

    }
}