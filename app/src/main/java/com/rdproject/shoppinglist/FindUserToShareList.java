package com.rdproject.shoppinglist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rdproject.shoppinglist.login.BaseActivity;
import com.rdproject.shoppinglist.login.User;
import com.shoppinglist.rdproject.shoppinglist.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class FindUserToShareList extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    public final String TAG = "FindUserToShareListTAG";
    private EditText searchBox;
    private TextView noUserFounded;
    private ImageButton searchButton;
    private ProgressBar progressBar;
    private RecyclerView mResultList;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private List<User> users;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user_to_share_list);
        setTitle(R.string.back_to_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initialize();


    }

    private void initialize() {
        searchBox = findViewById(R.id.search_box);
        searchButton = findViewById(R.id.search_button);
        progressBar = findViewById(R.id.progressBar2);
        mResultList = findViewById(R.id.user_list_recycler_view);
        noUserFounded = findViewById(R.id.no_user_founded);

        searchButton.setOnClickListener(this);
        searchBox.setOnEditorActionListener(this);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));
        users = new ArrayList<>();

        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View  v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_info, parent, false);
                return new UsersViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                UsersViewHolder usersViewHolderholder = (UsersViewHolder)holder;
                usersViewHolderholder.setDetails(users.get(position).getName(), users.get(position).getEmail(), users.get(position).getPicUrl());
            }

            @Override
            public int getItemCount() {
                return users.size();
            }
        };
        mResultList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard(v);

        String searchText = searchBox.getText().toString().trim().toLowerCase();
        if (searchText.equals("")){
            Toast.makeText(this, "Enter user name!", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseUserSearch(searchText);
    }

    private void firebaseUserSearch(final String searchText) {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                user.setUserId(dataSnapshot.getKey());
                Log.d(TAG, "user  " + user.getName() + "   " + user.getEmail() + "    " + user.getUserId());

                if (user == null || user.getName() == null || user.getEmail() == null) return;

                if (!users.contains(user) && (user.getName().toLowerCase().contains(searchText) || user.getEmail().toLowerCase().contains(searchText))) {
                    users.add(user);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mUserDatabase.removeEventListener(childEventListener);
        users.clear();
        adapter.notifyDataSetChanged();
        mUserDatabase.addChildEventListener(childEventListener);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        onClick(searchButton);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    // method declarated at xml layout
    public void onClickCurrentUser(View view) {
        final User chosenUser = users.get((int)view.getTag());
        SharedList sharedList = (SharedList) getIntent().getSerializableExtra("sharedList");
        mUserDatabase.child(chosenUser.getUserId()).child("sharedlists").child(sharedList.getSharedListName()).setValue(sharedList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(FindUserToShareList.this, getString(R.string.list_available_to) + "  " +
                        chosenUser.getName() + "  " + getString(R.string.now), Toast.LENGTH_SHORT).show();
            }
        });
        }

    // View Holder Class

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;


        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }
        public void setDetails(String userName, String userMail, String userImage) {

            TextView user_name = (TextView) mView.findViewById(R.id.user_name_searching);
            TextView user_mail = (TextView) mView.findViewById(R.id.user_mail_searching);
            ImageView user_image = (ImageView) mView.findViewById(R.id.user_pic);

            userMail = userMail.substring(0, 1) + " *******" + userMail.substring(userMail.indexOf("@"));

            user_name.setText(userName);
            user_mail.setText(userMail);
            if (userImage != null && !userImage.equals("")) {
                Picasso.get().load(userImage).transform(new CropCircleTransformation()).into(user_image);
            }

            mView.setTag(getAdapterPosition());
        }
    }
}
