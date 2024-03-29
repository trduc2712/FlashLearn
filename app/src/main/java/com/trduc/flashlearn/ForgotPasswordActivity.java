package com.trduc.flashlearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    ProgressDialog dialog;
    FirebaseAuth auth;
    EditText etEmail;
    Button bContinue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        etEmail = findViewById(R.id.etEmail);
        bContinue = findViewById(R.id.bContinue);

        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(ForgotPasswordActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading....");

        bContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotpass();
            }
        });
    }

    private void forgotpass() {
        if(!isEmail(etEmail)){
            return;
        }
        dialog.show();
        auth.sendPasswordResetEmail(etEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if(task.isSuccessful()){
                    startActivity(new Intent(ForgotPasswordActivity.this,SignInActivity.class));
                    finish();
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng kiểm tra email", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ForgotPasswordActivity.this, "Email không đúng", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}