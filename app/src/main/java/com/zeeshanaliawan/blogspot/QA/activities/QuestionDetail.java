package com.zeeshanaliawan.blogspot.QA.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.adapters.AnswersAdapter;
import com.zeeshanaliawan.blogspot.QA.extra.Constants;
import com.zeeshanaliawan.blogspot.QA.extra.Controls;
import com.zeeshanaliawan.blogspot.QA.obj.Answer;
import com.zeeshanaliawan.blogspot.QA.obj.Post;
import com.zeeshanaliawan.blogspot.QA.obj.Question;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

public class QuestionDetail extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private RecyclerView answers;
    private ImageView img;
    private String imageLink;
    private TextView title, desc, time;
    private EditText ans;
    private Button postAns;
    private FirebaseUser user;
    private NestedScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
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
        scroll = findViewById(R.id.scroll_detailQ);
        title = findViewById(R.id.title_detail);
        desc = findViewById(R.id.desc_detail);
        time = findViewById(R.id.time_detail);
        img = findViewById(R.id.detail_image);
        img.setOnClickListener(this);
        ans = findViewById(R.id.answer_detail);
        postAns = findViewById(R.id.post_answer);
        postAns.setOnClickListener(this);
        answers = findViewById(R.id.answers_recycler);
        answers.setLayoutManager(new LinearLayoutManager(this));
        getPost();
        getAnswers();
    }

    private String getId() {
        Intent i = getIntent();
        String id = i.getStringExtra("id");
        return id;
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
            case R.id.detail_image:
                if (imageLink != null && !imageLink.isEmpty()) {
                    Intent intent = new Intent(this, OpenImage.class);
                    intent.putExtra("img", imageLink);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(this, (View) img, "img");
                    startActivity(intent, options.toBundle());
                }
                break;
            case R.id.post_answer:
                if (!TextUtils.isEmpty(ans.getText().toString())) {
                    postAnswer();
                } else {
                    ans.setError("Empty!");
                }
                break;
        }
    }

    void getPost() {
        DatabaseReference re = FirebaseDatabase.getInstance().getReference();
        re.child(Constants.POST_TABLE).child(getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                imageLink = post.getImg();
                Glide.with(QuestionDetail.this).load(post.getImg()).placeholder(R.drawable.bg_splash).into(img);
                title.setText(post.getTitle());
                desc.setText(post.getDesc());
                time.setText(post.getDateTime());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postAnswer() {
        String cTime = String.valueOf(Calendar.getInstance().getTime());
        Answer answer = new Answer(ans.getText().toString(), cTime, user.getUid());
        final Dialog d = Controls.processing(this);
        d.show();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.ANSWER_TABLE).child(getId()).push().setValue(answer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                d.dismiss();
                ans.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                d.dismiss();
                Toast.makeText(QuestionDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAnswers() {
        final ArrayList<Answer> ansList = new ArrayList<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.ANSWER_TABLE).child(getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ansList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Answer answer = snapshot.getValue(Answer.class);
                    ansList.add(answer);
                }
                Collections.reverse(ansList);
                answers.setAdapter(new AnswersAdapter(QuestionDetail.this, ansList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
