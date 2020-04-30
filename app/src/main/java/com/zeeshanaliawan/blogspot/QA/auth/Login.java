package com.zeeshanaliawan.blogspot.QA.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.activities.DashBoard;
import com.zeeshanaliawan.blogspot.QA.extra.Controls;
import com.zeeshanaliawan.blogspot.QA.extra.MyDatabase;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private Button signUp, login;
    private EditText email, password;
    private TextView forgetPass;
    private ScrollView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        view = findViewById(R.id.scroll_login);
        signUp = findViewById(R.id.go_to_signup);
        signUp.setOnClickListener(this);
        login = findViewById(R.id.sign_in);
        forgetPass = findViewById(R.id.forget_pass);
        forgetPass.setOnClickListener(this);
        login.setOnClickListener(this);
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
    }

    private void forgetPaswword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        builder.setMessage("Forget Password No Worries.");
        builder.setTitle("Enter Your Mail And Get The link to verify");
        builder.setView(edittext);
        builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String mail = edittext.getText().toString();
                if (!mail.isEmpty()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Send an email to your inbox.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    Toast.makeText(Login.this, "Mail was empty!", Toast.LENGTH_SHORT).show();
                }


            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = 100;   //x position
        wmlp.y = 100;   //y position
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_to_signup:
                startActivity(new Intent(Login.this, SignUp.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case R.id.sign_in:
                EditText[] texts = {email, password};
                if (Controls.validation(texts, view)) {
                    MyDatabase db = new MyDatabase(this);
                    db.signIn(email.getText().toString(), password.getText().toString());
                }
                break;
            case R.id.forget_pass:
                forgetPaswword();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, DashBoard.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
        }
    }
}
