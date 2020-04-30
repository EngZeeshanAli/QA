package com.zeeshanaliawan.blogspot.QA.extra;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.activities.NewQuestionAdd;

import java.util.ArrayList;

public class Controls {

    public static final boolean validation(EditText[] texts, ScrollView view) {
        boolean validate = false;
        for (EditText text : texts) {
            if (TextUtils.isEmpty(text.getText().toString())) {
                validate = false;
                text.setError("something wrong");
                focusOnView(view, text);
                break;
            } else {
                validate = true;
            }
        }
        return validate;
    }

    private static final void focusOnView(final ScrollView view, final View v) {
        view.post(new Runnable() {
            @Override
            public void run() {
                view.scrollTo(0, v.getBottom());
            }
        });
    }

    public static AlertDialog.Builder selectImage(final Activity c) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    c.startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    c.startActivityForResult(pickPhoto, 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        return builder;
    }

    public static Dialog processing(Activity c) {
        Dialog d = new Dialog(c);
        d.setContentView(R.layout.processing);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.setCancelable(false);
        return d;
    }


}
