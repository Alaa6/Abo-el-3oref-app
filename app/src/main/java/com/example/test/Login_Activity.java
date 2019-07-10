package com.example.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Login_Activity extends AppCompatActivity
{

    private EditText user_email_login , user_password_login;
    private Button login_button,profile_btn_1,profile_btn_2,profile_btn_3;
    private TextView register_link;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    private Animation loginLogoAnim ,translateAnim;
    private LinearLayout loginByLayout;
    private ImageView loginLogo,loginFace_btn,facePic;

    // login with facebook
    private CallbackManager mCallbackManager;
    private String TAG="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        user_email_login = findViewById(R.id.login_email_input);
        user_password_login = findViewById(R.id.login_password_input);
        login_button = findViewById(R.id.login_btn);

        register_link = findViewById(R.id.login_register_link);
        loginFace_btn = findViewById(R.id.face_logo);
        facePic = findViewById(R.id.profile_img);
        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);

        //link to send user to register activity--------------------------------
        register_link.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendUserToRegisterActiity();
            }
        });
        //-----------------------------------------------------------------------

        //--------login button action--------------------------------------------
        login_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                user_login_method();
            }
        });
        //------------------------------------------------------------------------


        /*_________________________________login logo animation _____________________________________*/
        loginLogo=findViewById(R.id.login_logo);
        loginLogoAnim= AnimationUtils.loadAnimation(this , R.anim.logo_anim);
        loginLogo.startAnimation(loginLogoAnim);
        /*_________________________________loginBy layout animation _____________________________________*/
        loginByLayout=findViewById(R.id.loginBy_layout);
        translateAnim= AnimationUtils.loadAnimation(this , R.anim.translat_anim);
        loginByLayout.startAnimation(translateAnim);

        /*_________________________________________________________________________________________________*/


    }
    //-------------facebook login method-----------------------------//////////////////////////////////////////////////////




    //-------------user login method-----------------------------
    private void user_login_method()
    {
        String email = user_email_login.getText().toString();
        String password = user_password_login.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "please fill email feild", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "please fill password feild", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login to Account");
            loadingBar.setMessage("please wait until you login to your account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email , password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            user_class user1 = dataSnapshot.getValue(user_class.class);
                                            int type = user1.getUser_type();
                                            if (type==1){
                                                Intent intent = new Intent(Login_Activity.this, AdminActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                            else if(type == 3)
                                            {
                                                sendUserToGoldenActivity();

                                            }
                                            else if(type == 4)
                                            {
                                                sendUserToSelverActivity();

                                            }
                                            else if(type == 5)
                                            {
                                                sendUserToBronzeActivity();

                                            }
                                          else  if ( user1.getIsblocked().equals("true"))
                                          {
                                                Toast.makeText(Login_Activity.this,"You Are Blocked ",Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                sendUserToMapActivity();

                                                Toast.makeText(Login_Activity.this, "you are logged in successfully ", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }

                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(Login_Activity.this, "error occured : "+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( loadingBar!=null && loadingBar.isShowing() ){
            loadingBar.cancel();
        }
    }

    private void sendUserToBronzeActivity()
    {
        Intent BronzeActivityIntent = new Intent(getApplicationContext() , BronzeActivity.class);
        BronzeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(BronzeActivityIntent);
        finish();
    }

    private void sendUserToSelverActivity()
    {
        Intent SelverActivityIntent = new Intent(getApplicationContext() , SilverActivity.class);
        SelverActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(SelverActivityIntent);
        finish();
    }

    private void sendUserToGoldenActivity()
    {
        Intent GoldenActivityIntent = new Intent(getApplicationContext() , GoldenActivity.class);
        GoldenActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(GoldenActivityIntent);
        finish();
    }

    //-------------send user to login activity ---------------------
    private void sendUserToMainActivity()
    {
        Intent mainActivityIntent = new Intent(getApplicationContext() , MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }
    //--------------------------------------------------------------

    private void sendUserToMapActivity()
    {
        Intent MapActivityIntent = new Intent(getApplicationContext() , GoogleMapsActivity.class);
        MapActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MapActivityIntent);
        finish();
    }

    //-----------------------------------------------------------

    //-------------link to register Activity----------------------
    private  void sendUserToRegisterActiity()
    {
        Intent goToRegisterActivity = new Intent(getApplicationContext(), Register_Activity.class);
        startActivity(goToRegisterActivity);
        //finish();
    }
    //------------------------------------------------------------

//          Profiles -----------------------------------------------------------
    //-------------link to Profiles Activity----------------------
    private  void sendUserToProfile1Actiity()
    {
        Intent goToRegisterActivity = new Intent(getApplicationContext(), Profile1.class);
        startActivity(goToRegisterActivity);
    //    finish();
    }
    private  void sendUserToProfile2Actiity()
    {
        Intent goToRegisterActivity = new Intent(getApplicationContext(), Profile2.class);
        startActivity(goToRegisterActivity);
        //    finish();
    }
    private  void sendUserToProfile3Actiity()
    {
        Intent goToRegisterActivity = new Intent(getApplicationContext(), Profile3.class);
        startActivity(goToRegisterActivity);
            finish();
    }
    //------------------------------------------------------------

}
