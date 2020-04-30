package com.zeeshanaliawan.blogspot.QA.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.auth.Login;
import com.zeeshanaliawan.blogspot.QA.extra.Constants;
import com.zeeshanaliawan.blogspot.QA.extra.Controls;
import com.zeeshanaliawan.blogspot.QA.fragments.Home;
import com.zeeshanaliawan.blogspot.QA.fragments.MyQuestions;
import com.zeeshanaliawan.blogspot.QA.fragments.Users;
import com.zeeshanaliawan.blogspot.QA.obj.MyUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashBoard extends AppCompatActivity implements View.OnClickListener {
    boolean doubleBackToExitPressedOnce = false;
    private SlidingRootNav slidingRootNav;
    private Toolbar toolbar;
    private TextView home, myQues, users, profile, help, logout, email, name;
    private CircleImageView img;
    private FirebaseUser user;
    private Button feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        setSlidingRootNav(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new Home()).commit();
        }
        init();
    }

    private void setSlidingRootNav(Bundle savedInstanceState) {
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withToolbarMenuToggle(toolbar)
                .withMenuLayout(R.layout.menu_drawer)
                .inject();
    }

    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        home = findViewById(R.id.go_to_home);
        home.setOnClickListener(this);
        myQues = findViewById(R.id.go_to_myQuestions);
        myQues.setOnClickListener(this);
        users = findViewById(R.id.go_to_users);
        users.setOnClickListener(this);
        help = findViewById(R.id.go_to_help);
        help.setOnClickListener(this);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);
        profile = findViewById(R.id.go_to_profile);
        profile.setOnClickListener(this);
        feedback = findViewById(R.id.feedback);
        feedback.setOnClickListener(this);
        name = findViewById(R.id.name_dashboard);
        email = findViewById(R.id.email_dashboard);
        img = findViewById(R.id.profile_image_dash_board);
        user();
    }

    private void user() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Constants.USERS_TABLE).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyUser user = dataSnapshot.getValue(MyUser.class);
                Glide.with(DashBoard.this).load(user.getImg()).placeholder(R.drawable.avatar).into(img);
                name.setText(user.getName());
                email.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.go_to_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new Home()).commit();
                if (slidingRootNav.isMenuOpened())
                    slidingRootNav.closeMenu();
                break;

            case R.id.go_to_myQuestions:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new MyQuestions()).commit();
                if (slidingRootNav.isMenuOpened())
                    slidingRootNav.closeMenu();
                break;

            case R.id.go_to_profile:
                startActivity(new Intent(DashBoard.this, Profile.class));
                if (slidingRootNav.isMenuOpened())
                    slidingRootNav.closeMenu();
                break;

            case R.id.go_to_users:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new Users()).commit();
                if (slidingRootNav.isMenuOpened())
                    slidingRootNav.closeMenu();
                break;

            case R.id.go_to_help:
                startActivity(new Intent(DashBoard.this, Help.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                if (slidingRootNav.isMenuOpened())
                    slidingRootNav.closeMenu();
                break;

            case R.id.feedback:
                openFeedbackDialog();
                break;

            case R.id.logout:
                if (slidingRootNav.isMenuOpened())
                    slidingRootNav.closeMenu();
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(DashBoard.this, Login.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
        }
    }

    private void openFeedbackDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.feedback);
        final EditText feedback = dialog.findViewById(R.id.feedback);
        Button send = dialog.findViewById(R.id.send_feedback);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!feedback.getText().toString().equals("")) {
                    dialog.dismiss();
                    Dialog processing = Controls.processing(DashBoard.this);
                    processing.show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child(Constants.FEEDBACKS_TABLE).child(user.getUid()).push().setValue(feedback.getText().toString());
                    Toast.makeText(DashBoard.this, "Submitted successfully. Thanks! ", Toast.LENGTH_SHORT).show();
                    processing.dismiss();
                } else {
                    Toast.makeText(DashBoard.this, "Empty field!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (slidingRootNav.isMenuOpened()) {
            slidingRootNav.closeMenu();
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
