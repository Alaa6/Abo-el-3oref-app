package com.example.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView ;

    private AllUserAdapter usersAdapter;
    private List<user_class> mUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        recyclerView = findViewById(R.id.recycler_View);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mUsers = new ArrayList<user_class>();
        readUsers();
    }
    private void readUsers(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user_class user = snapshot.getValue(user_class.class);
                    // assert user != null;
                    //assert firebaseUser !=null;
                    if (!user.getUid().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                    /*if ( user.getIsblocked().equals("false")) {
                        mUsers.add(user);
                    }*/
                    //mUsers.add(user);
                }
                usersAdapter = new AllUserAdapter(getApplicationContext(), mUsers);
                recyclerView.setAdapter(usersAdapter);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
