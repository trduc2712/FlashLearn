package com.trduc.flashlearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    TextView  tvSignIn;
    EditText etEmail, etUsername, etPassword, etConfirmPassword;
    Button bSignUp;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkuser();
            }
        });
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

    }
    void init(){
        tvSignIn = findViewById(R.id.tvSignIn);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        bSignUp = findViewById(R.id.bSignUp);
    }
    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
    boolean isPasswordValid(EditText passwordField) {
        CharSequence password = passwordField.getText().toString();
        // Thêm các điều kiện kiểm tra mật khẩu ở đây, ví dụ: mật khẩu phải có ít nhất 8 ký tự
        return password.length() >= 8;
    }
    void checkuser(){
        String email = etEmail.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (isEmail(etEmail) == false) {
            Toast.makeText(SignUpActivity.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            return;
        } else if (isEmpty(etUsername)) {
            Toast.makeText(SignUpActivity.this, "Vui lòng nhập tên người dùng!", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isPasswordValid(etPassword)) {
            Toast.makeText(SignUpActivity.this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isPasswordValid(etConfirmPassword)) {
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
}