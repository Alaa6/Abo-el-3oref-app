package com.example.test;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class setup_user_Activity extends AppCompatActivity
{
    private EditText setup_username , setup_fullname , setup_address;
    private CircleImageView user_pp;
    private Button get_location_btn , setup_save_btn;
    private ProgressDialog loadingBar;
    final static int Gallery_pick = 1;// to used as second parametar in open phone gallery method
    private static final int IMAGE_REQUEST =1;
    private StorageReference userProfileImageRef,storageReference; // to use to store the link of pp to firebase storage
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef ,areaRef,reference;
    private double areaLat , areaLng;
    private Spinner choose_area;
    private String selectedArea ,currentUserID;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_user_);

        user_pp = findViewById(R.id.setup_user_pp);

        setup_username = findViewById(R.id.setup_user_username);
        setup_fullname = findViewById(R.id.setup_user_fullname);
        setup_address = findViewById(R.id.setup_user_Address);
        // get_location_btn = findViewById(R.id.setup_user_location);
        setup_save_btn = findViewById(R.id.setup_user_save_btn);
        storageReference= FirebaseStorage.getInstance().getReference("uploads");
        loadingBar = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);

        //create folder to store images in firebase storage
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("users prifile images");

        reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_class user = dataSnapshot.getValue(user_class.class);
                if (user.getImageUrl().equals("default")){
                    user_pp.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(user_pp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        user_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });


        //---------button action to save user informatiom method ---------------------------
        setup_save_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                save_setup_user_info();
            }
        });

        //------------------------------------------------------------------------------------

        //-----button action to open phone gallery method -------------------------------------
       /* user_pp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent openGallery = new Intent();
                openGallery.setAction(Intent.ACTION_GET_CONTENT);
                openGallery.setType("image/*");
                startActivityForResult(openGallery , Gallery_pick);
            }
        });*/
        //--------------------------------------------------------------------------------------

        //-----------------get Location of user-------------------------------------------------
//        get_location_btn.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent goToMapGetUserLocation = new Intent(setup_user_Activity.this , Maps_get_user_location.class);
//                startActivity(goToMapGetUserLocation);
//
//            }
//        });

        /*___________________________________ area spinner ____________________________________*/
        areaRef=FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("Areas");
        choose_area=findViewById(R.id.AreaSpinner);
        final ArrayList<String> areaList=new ArrayList<>();

        areaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot areaSnapshot : dataSnapshot.getChildren())
                {
                    Area_class areaObj=areaSnapshot.getValue(Area_class.class);
                    String areaName=areaObj.getArea_name();
                    areaList.add(areaName);

                }

                ArrayAdapter<String> areaAdapter =new ArrayAdapter<String>(setup_user_Activity.this, android.R.layout.simple_spinner_item ,areaList);
                choose_area.setAdapter(areaAdapter);

                choose_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        selectedArea=areaList.get(position);
                        Toast.makeText(setup_user_Activity.this, ""+selectedArea, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*__________________________________________________________________________________________*/



        //--------------------------------------------------------------------------------------
        //-----------to display the user profile image------------------------------------------
//        usersRef.addValueEventListener(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                if(dataSnapshot.exists())
//                {
//                    final String userImage = dataSnapshot.child("profileImage").getValue().toString();
////                    Picasso.get()
////                            .load(userImage)
////                            .placeholder(R.drawable.profile_icon)
////                            .error(R.drawable.home_icon2)
////                            .resize(50, 50)
////                            .centerCrop()
////                            .networkPolicy(NetworkPolicy.NO_CACHE)
////                            .into(user_pp);
//                    Glide.with(getApplicationContext())
//                    .load(userImage)
//                    .apply(new RequestOptions()
//                    .placeholder(R.drawable.profile_icon)
//                    .fitCenter())
//                    .into(user_pp);
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError)
//            {
//
//            }
//        });
        //--------------------------------------------------------------------------------------
    }


    //---------------method of croping image-----------------------------------------------------

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
    private void uploadImage(){
        /*final ProgressDialog pd = new ProgressDialog(getApplicationContext());
        pd.setMessage("Uploading...");
        pd.show();*/
        if (imageUri !=null){
            final  StorageReference fileRef = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserID);
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("ImageUrl",mUri);
                        reference.updateChildren(map);
                        //   pd.dismiss();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                        // pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    // pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"No image Selected",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        /*if(requestCode == Gallery_pick && resultCode == RESULT_OK && data != null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result =CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("profile image uploading");
                loadingBar.setMessage("please wait until your profile image uploading complete...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImageRef.child(currentUserID + "."+getFileExtension(resultUri));

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(setup_user_Activity.this, "profile image stored successfully to firebase storage", Toast.LENGTH_SHORT).show();

                            final String downloadUrl =task.getResult().getStorage().getDownloadUrl().toString();
                            usersRef.child("ImageUrl").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Intent selfSetupIntent = new Intent(getApplicationContext(), setup_user_Activity.class);
                                                startActivity(selfSetupIntent);

                                                Toast.makeText(setup_user_Activity.this, "profile image stored to firebase database successfully", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(setup_user_Activity.this, "Error occured : "+message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(setup_user_Activity.this, "Error occured : Image can't cropped , try Again", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }*/
        if (requestCode== IMAGE_REQUEST && resultCode==RESULT_OK
                && data != null && data.getData()!=null){
            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(),"upload in progress",Toast.LENGTH_SHORT).show();


            }else {
                uploadImage();
            }
        }
    }

    //-------------------------------------------------------------------------------------------

    private void save_setup_user_info()
    {
        String username = setup_username.getText().toString();
        String fullname = setup_fullname.getText().toString();
        String address = setup_address.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "please fill username field", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this, "please fill fullname field", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(address))
        {
            Toast.makeText(this, "please fill Address field", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("complete Account setup");
            loadingBar.setMessage("please wait until your account setup complete...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
//            get_location_btn.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//
//                    AlertDialog alertDialog = new AlertDialog.Builder(setup_user_Activity.this).create();
//                    alertDialog.setTitle("Pick Area Location");
//                    alertDialog.setIcon(R.drawable.location_icon);
//                    LayoutInflater layoutInflater = LayoutInflater.from(setup_user_Activity.this);
//                    View promptView = layoutInflater.inflate(R.layout.dialogmap, null);
//                    alertDialog.setView(promptView);
//
//                    MapView mMapView =  promptView.findViewById(R.id.mapView);
//                    MapsInitializer.initialize(setup_user_Activity.this);
//
//                    mMapView.onCreate(alertDialog.onSaveInstanceState());
//                    mMapView.onResume();
//
//
//                    mMapView.getMapAsync(new OnMapReadyCallback()
//                    {
//                        @Override
//                        public void onMapReady(final GoogleMap googleMap)
//                        {
//                            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                                @Override
//                                public void onMapClick(LatLng latLng)
//                                {
//                                    // Creating a marker
//                                    MarkerOptions markerOptions = new MarkerOptions();
//
//                                    // Setting the position for the marker
//                                    markerOptions.position(latLng);
//
//                                    // Setting the title for the marker.
//                                    // This will be displayed on taping the marker
//                                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);
//
//                                    // Clears the previously touched position
//                                    googleMap.clear();
//
//                                    // Animating to the touched position
//                                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//
//                                    // Placing a marker on the touched position
//                                    googleMap.addMarker(markerOptions);
//
//                                    areaLat = latLng.latitude;
//                                    areaLng = latLng.longitude;
//
//                                }
//                            });
//                        }
//                    });
//
//                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Location", new DialogInterface.OnClickListener()
//                    {
//                        public void onClick(DialogInterface dialog, int which)
//                        {
//
//                            Toast.makeText(setup_user_Activity.this, "Location has been picked successuflly...", Toast.LENGTH_LONG).show();
//
//                        }
//
//                    });
//
//
//                    alertDialog.show();
//
//                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
//                    //alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor());
//
//
//                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.WRAP_CONTENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT);
//                    params.setMargins(0,0,150,0);
//
//                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setLayoutParams(params);
//
//                }
//            });



            user_class userClassObject = new user_class(username , fullname ,address,2,"false");
            HashMap userMap = new HashMap();
            userMap.put("username",userClassObject.getUser_username());
            userMap.put("fullname",userClassObject.getUser_fullname());
            userMap.put("address",userClassObject.getUser_userAddress());
            userMap.put("latitude",areaLat);
            userMap.put("longitude",areaLng);

            usersRef.updateChildren(userMap)
                    .addOnCompleteListener(new OnCompleteListener()
                    {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if(task.isSuccessful())
                            {
                                sendUserToMapActivity();
                                Toast.makeText(setup_user_Activity.this, "your account setup finished successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(setup_user_Activity.this, "error occured : "+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

            /*__________________________________save selected area ______________________________________*/

            usersRef.child("UserArea").setValue(selectedArea);
            /*____________________________________________________________________________________________*/


        }
    }

    //send user to main activity after account is situped-------------------------
    private void sendUserToMainActivity()
    {
        Intent mainActivityIntent = new Intent(getApplicationContext() , MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

    private void sendUserToMapActivity()
    {
        Intent mapActivityIntent = new Intent(getApplicationContext() , GoogleMapsActivity.class);
        mapActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mapActivityIntent);
        finish();
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

    }
    //-----------------------------------------------------------------------------
    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( loadingBar!=null && loadingBar.isShowing() ){
            loadingBar.cancel();
        }
    }
}
