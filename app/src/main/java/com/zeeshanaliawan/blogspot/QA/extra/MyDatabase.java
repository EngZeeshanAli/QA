package com.zeeshanaliawan.blogspot.QA.extra;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.activities.DashBoard;
import com.zeeshanaliawan.blogspot.QA.obj.MyUser;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class MyDatabase {

    Activity c;

    public MyDatabase(Activity c) {
        this.c = c;
    }

    private Dialog processing() {
        Dialog d = new Dialog(c);
        d.setContentView(R.layout.processing);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.setCancelable(false);
        return d;
    }

    public void addNewUser(final MyUser user, String pass) {
        final Dialog d = processing();
        d.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();
                    MyUser dbUser = new MyUser(user.getName(), user.getEmail(), user.getSkill(), cUser.getUid());
                    FirebaseAuth.getInstance().signOut();
                    saveUserData(dbUser, d);
                } else {
                    Toast.makeText(c, "Something Wrong", Toast.LENGTH_SHORT).show();
                    d.dismiss();
                }

            }
        }).

                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void saveUserData(MyUser user, final Dialog d) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.USERS_TABLE).child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    d.dismiss();
                    c.finish();
                    Toast.makeText(c, "User Created Successfully SignIn now !", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
                d.dismiss();
            }
        });
    }


    public void signIn(String email, String password) {
        final Dialog d = processing();
        d.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    d.dismiss();
                    c.startActivity(new Intent(c, DashBoard.class));
                    c.overridePendingTransition(R.anim.enter, R.anim.exit);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
                d.dismiss();
            }
        });
    }


}
