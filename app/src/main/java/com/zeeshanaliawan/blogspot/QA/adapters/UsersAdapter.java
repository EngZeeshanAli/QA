package com.zeeshanaliawan.blogspot.QA.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.fragments.Users;
import com.zeeshanaliawan.blogspot.QA.obj.MyUser;
import com.zeeshanaliawan.blogspot.QA.obj.Post;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.Item> {
    ArrayList list;
    Context c;

    public UsersAdapter(Context c, ArrayList list) {
        this.c = c;
        this.list = list;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.user_view, parent, false);
        return new Item(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        final MyUser user = (MyUser) list.get(position);
        Glide.with(c).load(user.getImg()).placeholder(R.drawable.avatar).into(holder.img);
        holder.name.setText(user.getName());
        holder.profession.setText(user.getSkill());
        holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{user.getEmail()});
                i.putExtra(Intent.EXTRA_SUBJECT, "Need to contact you.");
                i.putExtra(Intent.EXTRA_TEXT, "I want to get some help. Respond me Sir kindly...");
                try {
                    c.startActivity(Intent.createChooser(i, "User only Gmail or Yahoo mail to Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(c, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, profession;
        Button contact;

        public Item(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.user_img);
            name = itemView.findViewById(R.id.user_name);
            profession = itemView.findViewById(R.id.user_profession);
            contact = itemView.findViewById(R.id.contact);
        }
    }

    public void updateList(ArrayList<MyUser> findQues) {
        list = new ArrayList<>();
        list.addAll(findQues);
        notifyDataSetChanged();
    }
}
