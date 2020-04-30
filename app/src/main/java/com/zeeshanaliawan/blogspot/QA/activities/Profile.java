package com.zeeshanaliawan.blogspot.QA.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.auth.Login;
import com.zeeshanaliawan.blogspot.QA.extra.Constants;
import com.zeeshanaliawan.blogspot.QA.extra.Controls;
import com.zeeshanaliawan.blogspot.QA.obj.MyUser;
import com.zeeshanaliawan.blogspot.QA.obj.Post;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private FirebaseUser user;
    private CircleImageView img;
    private ImageView update;
    private TextView id, name, email, skill;
    private Button signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setToolbar();
        init();
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        img = findViewById(R.id.profile_img);
        update = findViewById(R.id.update_image);
        update.setOnClickListener(this);
        id = findViewById(R.id.id_user);
        name = findViewById(R.id.name_user);
        email = findViewById(R.id.email_user);
        skill = findViewById(R.id.skill_user);
        signout = findViewById(R.id.sigout_profile);
        signout.setOnClickListener(this);
        user();
    }

    private void user() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Constants.USERS_TABLE).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyUser user = dataSnapshot.getValue(MyUser.class);
                Glide.with(Profile.this).load(user.getImg()).placeholder(R.drawable.avatar).into(img);
                name.setText(user.getName());
                email.setText(user.getEmail());
                id.setText(user.getId());
                skill.setText(user.getSkill());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Bitmap convertToBitmap() {
        img.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
        Bitmap bmap = drawable.getBitmap();
        return bmap;
    }

    private void setUplodImg(Bitmap bitmap) {
        final Dialog processing = Controls.processing(this);
        processing.show();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        final String time = String.valueOf(Calendar.getInstance().getTime());
        String name = time + user.getUid();
        reference.child(Constants.STORE_IMAGE_PROFILE + name).putBytes(byteArray).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(Constants.USERS_TABLE).child(user.getUid()).child("img").setValue(imageUrl).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            processing.dismiss();
                                            Toast.makeText(Profile.this, "Updated Successfully !", Toast.LENGTH_SHORT).show();
                                        } else {
                                            processing.dismiss();
                                            Toast.makeText(Profile.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                processing.dismiss();
                                Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                processing.dismiss();
                Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void permissoinAlert(final Activity c) {
        final Dialog alertPermission = new Dialog(c, R.style.WideDialog);
        alertPermission.setContentView(R.layout.permissions_dialog);
        Button agree = alertPermission.findViewById(R.id.agree_dialog);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertPermission.dismiss();
                requestPermission(c);
            }
        });
        alertPermission.setCancelable(false);
        alertPermission.show();
    }

    private void requestPermission(final Activity c) {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        String rationale = "Please provide permissions so that you can use this functionality...";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");

        Permissions.check(c/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                // on granted permissions
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder select = Controls.selectImage(Profile.this);
                    select.show();
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // on permission denied
                Toast.makeText(c, "permissions denied", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        img.setImageBitmap(selectedImage);
                        setUplodImg(convertToBitmap());
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                img.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                setUplodImg(convertToBitmap());
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sigout_profile:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(Profile.this, Login.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case R.id.update_image:
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder select = Controls.selectImage(Profile.this);
                    select.show();
                } else {
                    permissoinAlert(this);
                }
                break;
        }
    }
}
