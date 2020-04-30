package com.zeeshanaliawan.blogspot.QA.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.activities.NewQuestionAdd;
import com.zeeshanaliawan.blogspot.QA.adapters.UsersAdapter;
import com.zeeshanaliawan.blogspot.QA.extra.Constants;
import com.zeeshanaliawan.blogspot.QA.extra.Controls;
import com.zeeshanaliawan.blogspot.QA.obj.MyUser;

import java.util.ArrayList;

public class Users extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {
    private RecyclerView users;
    private SearchView search;
    private LinearLayout searchOpen;
    private ArrayList<MyUser> userList;
    private UsersAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.users_frag, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        userList = new ArrayList();
        search = v.findViewById(R.id.search_user);
        search.setOnQueryTextListener(this);
        searchOpen = v.findViewById(R.id.search_user_layout);
        searchOpen.setOnClickListener(this);
        users = v.findViewById(R.id.user_recycler);
        users.setLayoutManager(new GridLayoutManager(getContext(), 2));
        users.setHasFixedSize(true);
        getUsers();
    }

    private void getUsers() {
        final Dialog processing = Controls.processing(getActivity());
        processing.show();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.USERS_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MyUser user = snapshot.getValue(MyUser.class);
                    userList.add(user);
                }
                adapter = new UsersAdapter(getActivity(), userList);
                users.setAdapter(adapter);
                processing.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                processing.dismiss();
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_user_layout:
                search.onActionViewExpanded();
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userQuery = newText.toLowerCase();
        ArrayList<MyUser> findList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            MyUser user = userList.get(i);
            if (user.getName().toLowerCase().contains(userQuery) || user.getId().toLowerCase().contains(userQuery) || user.getSkill().toLowerCase().contains(userQuery)) {
                findList.add(user);
            }
        }

        if (!findList.isEmpty()) {
            adapter.updateList(findList);
        }

        return true;
    }
}
