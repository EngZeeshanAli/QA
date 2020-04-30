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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.extra.Constants;
import com.zeeshanaliawan.blogspot.QA.extra.Controls;
import com.zeeshanaliawan.blogspot.QA.obj.Post;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class NewQuestionAdd extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private EditText title, desc;
    private ImageView img;
    private Button post, addImage;
    private boolean imgCheck = false;
    private FirebaseUser user;
    private ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question_add);
        toolbar();
        init();
    }

    private void toolbar() {
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        scroll = findViewById(R.id.scroll_add);
        title = findViewById(R.id.title_add);
        desc = findViewById(R.id.desc_add);
        img = findViewById(R.id.img_to_post);
        addImage = findViewById(R.id.upload_image_add);
        addImage.setOnClickListener(this);
        post = findViewById(R.id.post_add);
        post.setOnClickListener(this);

    }

    private Bitmap convertToBitmap() {
        img.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
        Bitmap bmap = drawable.getBitmap();
        return bmap;
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
                    AlertDialog.Builder select = Controls.selectImage(NewQuestionAdd.this);
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

    private void setUplodImg(Bitmap bitmap) {
        final Dialog processing = Controls.processing(this);
        processing.show();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        final String time = String.valueOf(Calendar.getInstance().getTime());
        String name = time + user.getUid();
        reference.child(Constants.STORE_IMAGE_POST + name).putBytes(byteArray).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        Post post = new Post(title.getText().toString(), desc.getText().toString(), imageUrl, time, user.getUid());
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(Constants.POST_TABLE).push().setValue(post).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            processing.dismiss();
                                            finish();
                                            overridePendingTransition(R.anim.enter, R.anim.exit);
                                            Toast.makeText(NewQuestionAdd.this, "Posted Successfully !", Toast.LENGTH_SHORT).show();
                                        } else {
                                            processing.dismiss();
                                            Toast.makeText(NewQuestionAdd.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                processing.dismiss();
                                Toast.makeText(NewQuestionAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                processing.dismiss();
                Toast.makeText(NewQuestionAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post() {
        final Dialog processing = Controls.processing(this);
        processing.show();
        String time = String.valueOf(Calendar.getInstance().getTime());
        Post post = new Post(title.getText().toString(), desc.getText().toString(), time, user.getUid());
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.POST_TABLE).push().setValue(post).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            processing.dismiss();
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            Toast.makeText(NewQuestionAdd.this, "Posted Successfully !", Toast.LENGTH_SHORT).show();
                        } else {
                            processing.dismiss();
                            Toast.makeText(NewQuestionAdd.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                processing.dismiss();
                Toast.makeText(NewQuestionAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            case R.id.post_add:
                EditText[] texts = {title, desc};
                if (Controls.validation(texts, scroll)) {
                    if (imgCheck) {
                        Bitmap b = convertToBitmap();
                        setUplodImg(b);
                    } else {
                        post();
                    }
                }
                break;

            case R.id.upload_image_add:
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder select = Controls.selectImage(NewQuestionAdd.this);
                    select.show();
                } else {
                    permissoinAlert(this);
                }
                break;
        }
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
                        imgCheck = true;
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
                                imgCheck = true;
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }


}
