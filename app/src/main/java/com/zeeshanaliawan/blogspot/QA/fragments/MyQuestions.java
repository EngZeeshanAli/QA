package com.zeeshanaliawan.blogspot.QA.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.activities.NewQuestionAdd;
import com.zeeshanaliawan.blogspot.QA.adapters.HomeQuizAdapter;
import com.zeeshanaliawan.blogspot.QA.extra.Constants;
import com.zeeshanaliawan.blogspot.QA.extra.Controls;
import com.zeeshanaliawan.blogspot.QA.obj.Post;

import java.util.ArrayList;
import java.util.Collections;

public class MyQuestions extends Fragment implements View.OnClickListener,SearchView.OnQueryTextListener{
    private RecyclerView myQues;
    private SearchView search;
    private LinearLayout searchOpen;
    private ArrayList<Post> posts;
    private HomeQuizAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_questions_frag, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        search = v.findViewById(R.id.search_myQues);
        search.setOnQueryTextListener(this);
        searchOpen = v.findViewById(R.id.search_layout_myQues);
        searchOpen.setOnClickListener(this);
        myQues = v.findViewById(R.id.questions_myQues);
        myQues.setLayoutManager(new LinearLayoutManager(getContext()));
        getQuestions();
    }


    private void getQuestions() {
        final Dialog d = Controls.processing(getActivity());
        d.show();
        posts = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Constants.POST_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    String key = snapshot.getKey();
                    Post finalPost = new Post(key, post.getTitle(), post.getDesc(), post.getImg(), post.getDateTime(), post.getPoster());
                    posts.add(finalPost);
                }
                Collections.reverse(posts);
                adapter = new HomeQuizAdapter(getActivity(), posts);
                myQues.setAdapter(adapter);
                if (d.isShowing()) {
                    d.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (d.isShowing())
                    d.dismiss();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userQuery = newText.toLowerCase();
        ArrayList<Post> findList = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            if (post.getTitle().toLowerCase().contains(userQuery) || post.getDesc().toLowerCase().contains(userQuery)) {
                findList.add(post);
            }
        }

        if (!findList.isEmpty()) {
            adapter.updateList(findList);
        }

        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_layout_myQues:
                search.onActionViewExpanded();
                break;
        }
    }
}
