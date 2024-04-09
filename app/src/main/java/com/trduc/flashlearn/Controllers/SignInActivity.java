package com.trduc.flashlearn.Controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trduc.flashlearn.Models.User;
import com.trduc.flashlearn.R;

public class SignInActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button bSignIn, bFacebookSignIn, bGoogleSignIn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    CheckBox cbRemember;

    TextView tvForgotPassword, tvSignUp;

    SharedPreferences sharedPreferences;
    boolean isPasswordVisible = false;

    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("isChecked", false);
        cbRemember.setChecked(isRemembered);

        if (isRemembered) {
            String savedEmail = sharedPreferences.getString("email", "");
            String savedPassword = sharedPreferences.getString("password", "");
            etEmail.setText(savedEmail);
            etPassword.setText(savedPassword);
        }

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

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkEmail() | !checkPassword()) {

                } else {
                    checkUser();
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        bGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginGG();
                GoogleSignIn.getClient(SignInActivity.this,GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
            }
        });

    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            etPassword.setTransformationMethod(null);
            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_slash, 0);
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
        }
    }

    public void loginGG(){

        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1234){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseauth(account.getIdToken());
            }
            catch (Exception e){
                Toast.makeText(this,"Lỗi",Toast.LENGTH_SHORT).show();
//                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseauth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser datauser = auth.getCurrentUser();
                    String email = datauser.getEmail();
                    String name = datauser.getDisplayName();
                    User data = new User(email,name);
                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference("Registered users");
                    reference.child(datauser.getUid()).setValue(data);
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    showDiaglog();
                }
                else {
                    Toast.makeText(SignInActivity.this,"Vui lòng kiểm tra lại",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void showDiaglog(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đăng nhập");
        progressDialog.setMessage("Loadingg...");
        progressDialog.setMax(20);
        progressDialog.show();
    }
    void init() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        bSignIn = findViewById(R.id.bSignIn);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        bFacebookSignIn = findViewById(R.id.bFacebookSignIn);
        bGoogleSignIn = findViewById(R.id.bGoogleSignIn);
        cbRemember = findViewById(R.id.cbRemember);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public Boolean checkEmail() {
        if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            return false;
        }
        if (!isEmail(etEmail)) {
            etEmail.setError("Email không đúng định dạng");
            return false;
        } else {
            etEmail.setError(null);
            return true;
        }
    }

    public Boolean checkPassword(){
        String password = etPassword.getText().toString();
        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        } else {
            etPassword.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                showDiaglog();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                etPassword.setError("Sai mật khẩu");
            }
        });

        if (cbRemember.isChecked()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isChecked", true);
            editor.putString("email", email);
            editor.putString("password", password);
            editor.apply();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isChecked", false);
            editor.remove("email");
            editor.remove("password");
            editor.apply();
        }

    }
}