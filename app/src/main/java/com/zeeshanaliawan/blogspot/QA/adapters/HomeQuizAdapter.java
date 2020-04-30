package com.zeeshanaliawan.blogspot.QA.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.activities.QuestionDetail;
import com.zeeshanaliawan.blogspot.QA.extra.Constants;
import com.zeeshanaliawan.blogspot.QA.obj.MyUser;
import com.zeeshanaliawan.blogspot.QA.obj.Post;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeQuizAdapter extends RecyclerView.Adapter<HomeQuizAdapter.Item> {
    Activity c;
    ArrayList<Post> posts;

    public HomeQuizAdapter(Activity c, ArrayList<Post> posts) {
        this.c = c;
        this.posts = posts;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.home_quiz_item_view, parent, false);
        return new Item(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        final Post post = posts.get(position);
        holder.title.setText(post.getTitle());
        holder.description.setText(post.getDesc());
        holder.time.setText(post.getDateTime());
        user(post.getPoster(), holder.img);
        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detail = new Intent(c, QuestionDetail.class);
                detail.putExtra("id", post.getId());
                c.startActivity(detail);
                c.overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class Item extends RecyclerView.ViewHolder {
        TextView title, description, time;
        CircleImageView img;
        Button open;

        public Item(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_home_item);
            description = itemView.findViewById(R.id.desc_home_item);
            img = itemView.findViewById(R.id.profile_home_item);
            open = itemView.findViewById(R.id.open_home_item);
            time = itemView.findViewById(R.id.time_home_item);
        }
    }

    private void user(String id, final CircleImageView img) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Constants.USERS_TABLE).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyUser user = dataSnapshot.getValue(MyUser.class);
                Glide.with(c).load(user.getImg()).placeholder(R.drawable.avatar).into(img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateList(ArrayList<Post> findQues) {
        posts = new ArrayList<>();
        posts.addAll(findQues);
        notifyDataSetChanged();
    }
}
