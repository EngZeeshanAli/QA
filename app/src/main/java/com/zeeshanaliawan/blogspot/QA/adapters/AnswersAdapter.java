package com.zeeshanaliawan.blogspot.QA.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeeshanaliawan.blogspot.QA.R;
import com.zeeshanaliawan.blogspot.QA.extra.Constants;
import com.zeeshanaliawan.blogspot.QA.fragments.Users;
import com.zeeshanaliawan.blogspot.QA.obj.Answer;
import com.zeeshanaliawan.blogspot.QA.obj.MyUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.CLIPBOARD_SERVICE;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.Item> {
    Context context;
    ArrayList<Answer> ans;
    FirebaseUser user;

    public AnswersAdapter(Context context, ArrayList<Answer> ans) {
        this.context = context;
        this.ans = ans;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }


    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.answer_item_view, parent, false);
        return new Item(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Item holder, int position) {
        final Answer answer = ans.get(position);
        holder.ans.setText(answer.getAnswer());
        holder.time.setText(answer.getTime());
        user(answer.getPoster(), holder.img, holder.name);
        holder.ans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", holder.ans.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ans.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView ans, time, name;

        public Item(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_answer_view);
            ans = itemView.findViewById(R.id.answer_view);
            time = itemView.findViewById(R.id.date_answer_view);
            name = itemView.findViewById(R.id.name_answer_view);
        }
    }

    private void user(String id, final CircleImageView img, final TextView name) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Constants.USERS_TABLE).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyUser user = dataSnapshot.getValue(MyUser.class);
                name.setText(user.getName());
                Glide.with(context).load(user.getImg()).placeholder(R.drawable.avatar).into(img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
