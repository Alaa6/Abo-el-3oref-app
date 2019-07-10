package com.example.test;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AboEl3orif extends AppCompatActivity {
    private NavigationView navigationView ;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar mToolbar;

    ArrayList<user_class> list = new ArrayList<>();
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference,followersRef ;
    String userId = firebaseUser.getUid();
    String aboId ;
    int followerCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abo_el3orif);
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        final Button followAbo_btn = findViewById(R.id.abo_follow_btn);
        final TextView followersNumber = findViewById(R.id.profile_followersNum);


        //--------to open side menue otmatically-----------------------------//
        drawerLayout = findViewById(R.id.abo_darw_layout);
        navigationView = findViewById(R.id.nav_view);
        mToolbar = findViewById(R.id.main_page_toolbar);//call toolbar

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(AboEl3orif.this, drawerLayout,mToolbar ,R.string.drawer_open,R.string.drawer_close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        actionBarDrawerToggle.syncState();
        // ---------------------------------------------------------------------- //
        if(followerCount > 0){
            followersNumber.setText(String.valueOf( followerCount) );
        }
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                user_class user2 = dataSnapshot.getValue(user_class.class);
                if(user2.getUser_type() == 3){
                    aboId = user2.getUid();
                    reference.child(aboId).child("followers").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(userId)){
                                followAbo_btn.setText("Following");
                                //Toast.makeText(getApplicationContext(),"اشتغلت",Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    if(dataSnapshot.hasChild("followersCount")){
                        followerCount = user2.getFollowersCount();
                        followersNumber.setText(String.valueOf( followerCount) );
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        followAbo_btn.setOnClickListener(new View.OnClickListener() {
            boolean followCheck;

            @Override
            public void onClick(View v) {
                aboEl3orifFunctions abo_obj = new aboEl3orifFunctions();
                followCheck=abo_obj.testFollowers();
                if(followCheck == true){
                    followAbo_btn.setText("Follow Me");

                }
                if(followCheck == false){
                    followAbo_btn.setText("Following");
                }
                followersNumber.setText(String.valueOf( followerCount) );
                finish();
                startActivity(getIntent());

            }
        });


        Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();
    }

    public class aboEl3orifFunctions{

        private boolean followCheck;

        public aboEl3orifFunctions() {
        }

        public boolean testFollowers(){
            followersRef = FirebaseDatabase.getInstance().getReference().child("users");
            followersRef.child(aboId).child("followers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(userId)) {
                        followerCount-=1;
                        reference.child(aboId).child("followersCount").setValue(followerCount);
                        reference.child(aboId).child("followers/"+userId).removeValue();
                        followCheck = false;

                    }
                    else {
                        HashMap followers = new HashMap();
                        followers.put("user_id",userId);
                        followerCount+=1;
                        reference.child(aboId).child("followers/"+userId).setValue(followers);
                        reference.child(aboId).child("followersCount").setValue(followerCount);
                        //     Toast.makeText(getApplicationContext(),"ضاف",Toast.LENGTH_LONG).show();
                        followCheck = true;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return followCheck;
        }
    }

}
