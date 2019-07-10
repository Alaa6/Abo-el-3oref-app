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

public class allComplainActivity extends AppCompatActivity {
    private RecyclerView recyclerView ;

    private allComplainAdapter allComplainAdapter;
    private List<Complain> mComplain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_complain);

        recyclerView = findViewById(R.id.allcomplain_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mComplain = new ArrayList<Complain>();
        readUsers();

    }
    private void readUsers(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Complains");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mComplain.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Complain complain = snapshot.getValue(Complain.class);
                    // assert user != null;
                    //assert firebaseUser !=null;

                    mComplain.add(complain);

                    /*if ( user.getIsblocked().equals("false")) {
                        mComplain.add(user);
                    }*/
                    //mUsers.add(user);
                }
                allComplainAdapter = new allComplainAdapter(getApplicationContext(), mComplain);
                recyclerView.setAdapter(allComplainAdapter);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
