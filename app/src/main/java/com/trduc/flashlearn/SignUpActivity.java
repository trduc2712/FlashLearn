    package com.trduc.flashlearn;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.text.InputType;
    import android.text.TextUtils;
    import android.text.method.PasswordTransformationMethod;
    import android.util.Patterns;
    import android.view.MotionEvent;
    import android.view.View;
    import android.widget.*;

    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.List;

    public class SignUpActivity extends AppCompatActivity {

        TextView  tvSignIn;
        EditText etEmail, etUsername, etPassword, etConfirmPassword;
        Button bSignUp;
        FirebaseDatabase database;
        DatabaseReference reference;
        FirebaseAuth auth;
        boolean isPasswordVisible = false;
        boolean isConfirmPasswordVisible = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sign_up);
            init();

            etPassword.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_RIGHT = 2;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            togglePasswordVisibility();
                            return true;
                        }
                    }
                    return false;
                }
            });

            etConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_RIGHT = 2;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (etConfirmPassword.getRight() - etConfirmPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            toggleConfirmPasswordVisibility();
                            return true;
                        }
                    }
                    return false;
                }
            });

            bSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkUser();
                }
            });

            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
            });

            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
            });

        }

        private void togglePasswordVisibility() {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(null);
                etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_slash, 0);
            }
        }

        private void toggleConfirmPasswordVisibility() {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setTransformationMethod(null);
                etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
            } else {
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_slash, 0);
            }
        }

        void init() {
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

        void checkUser(){
            String email = etEmail.getText().toString();
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (isEmpty(etEmail)) {
                etEmail.setError("Vui lòng nhập email");
                return;
            } else if (isEmail(etEmail) == false) {
                etEmail.setError("Email không đúng định dạng");
                return;
            } else if (isEmpty(etUsername)) {
                etUsername.setError("Vui lòng nhập tên người dùng");
                return;
            } else if (!isPasswordValid(etPassword)) {
                etPassword.setError("Mật khẩu phải có từ 8 ký tự trở lên");
                return;
            } else if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Mật khẩu bạn nhập lại không đúng");
                return;
            }

    //        database = FirebaseDatabase.getInstance();
    //        reference = database.getReference("users");
    //        User user = new User(email, username, password);
    //        reference.child(username).setValue(user);

            auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        database = FirebaseDatabase.getInstance();
                        reference = database.getReference("Registered users");

                        User user = new User(email, username, password);

                        reference.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    firebaseUser.sendEmailVerification();
                                    Toast.makeText(SignUpActivity.this, "Vui lòng xác minh email của bạn", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });


                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    etEmail.setError("Địa chỉ email này đã được đăng ký");
                }
            });
        }
    }