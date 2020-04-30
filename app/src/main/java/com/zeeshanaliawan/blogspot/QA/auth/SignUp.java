package com.zeeshanaliawan.blogspot.QA.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.extra.Controls;
import com.zeeshanaliawan.blogspot.QA.extra.MyDatabase;
import com.zeeshanaliawan.blogspot.QA.obj.MyUser;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private Button signUp, login;
    private EditText name, email, password, skill;
    private ScrollView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
    }

    private void init() {
        view = findViewById(R.id.scroll_signup);
        login = findViewById(R.id.go_to_signIn);
        login.setOnClickListener(this);
        signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(this);
        name = findViewById(R.id.name_signup);
        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);
        skill = findViewById(R.id.skill_signup);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_to_signIn:
                finish();
                break;
            case R.id.signUp:
                EditText[] texts = {name, email, password, skill};
                if (Controls.validation(texts, view)) {
                    MyUser user = new MyUser(name.getText().toString(),
                            email.getText().toString(), skill.getText().toString());
                    MyDatabase db = new MyDatabase(SignUp.this);
                    db.addNewUser(user, password.getText().toString());
                }
                break;
        }
    }


}
