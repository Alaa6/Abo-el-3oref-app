package com.example.test;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SilverActivity extends MainActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,//added
        GoogleApiClient.OnConnectionFailedListener,//added
        LocationListener //added

{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient; //to use in buildGoogleApi Method
    private LocationRequest locationRequest; //to use in onConnected method
    private Location lastLocation;
    private Marker currentUserLocationMarker , category_marker;
    private static final int Request_User_Location_Code = 99;
    private double latitude ,longitude;
    // private int ProximityRaduis = 10000;
    private Spinner sp_area , sp_category , sp_area_popup , sp_category_popup; //spinner
    private ArrayAdapter<String> arrayAdapter ;
    private DatabaseReference AbuEl3orif_DB_Ref,db ,db2 , AbuEl3orifRefFinal;
    private Button btn_filter;
    String selected_Area , selected_category;
    private Marker marker;
    private EditText addressField;
    private ImageView search;

    ArrayList<ArrayList<ArrayList<String>>> commentMap2 = new ArrayList<>();


    HashMap<String , Integer> userCalcLikes ;
    HashMap<String , Integer> userCalcLikesTop3;

    HashMap<String , HashMap< String , Integer>> userCalcArea = new  HashMap<String , HashMap< String , Integer>>();
    HashMap<String , HashMap< String , Integer>> userCalcAreaTop3 = new  HashMap<String , HashMap< String , Integer>>();


    String  SingleAreaName , SingleAreaUserId , UserNameOfAllItsComment , commentsLilkeCount  , commentsDisLilkeCount  ;


    Double categoryLat;
    Double categoryLng;
    String categoryName;

    //----------------as main-------------------------

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView post_list;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private CircleImageView navProfileImage;
    private TextView navUsername;

    private String CurrentUserId ;
    private DatabaseReference userRef , calcRef;
    private FirebaseAuth mAuth;//to check user authentication loggedin or not
    private Dialog AskQuetionDialog;
    private TextView textCloseIcon;
    private Button btn_que_filtter;
    //-------------------------------------------------
    private ArrayList<String> AllAreaNamesComments = new ArrayList<String>();


    //My Work
    private EditText mSearchField;
    private EditText locationSearch;

    private ImageButton mSearchBtn , mSearchMapBtn;
    private RecyclerView mResultList;
    public Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silver);
//My Work
        mSearchField = (EditText) findViewById(R.id.search_field);
        locationSearch = findViewById(R.id.search_map_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        mSearchMapBtn = findViewById(R.id.search_map_btn);
        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mResultList.setLayoutManager(linearLayoutManager);
        // mResultList.setLayoutManager(new LinearLayoutManager(this));
        mSearchBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mMap.clear();
                String searchText = mSearchField.getText().toString().toLowerCase();
                firebaseUserSearch(searchText);
                mSearchField.setText("");
            }


        });


//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                Toast.makeText(getApplicationContext(), "rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrRRRRRRRRRRRRRRRRRRRRRRRRRrrrrrrrrrrrr", Toast.LENGTH_SHORT).show();
//            }
//       }, 30000);




        //-----------------------as main ---------------------------------------------------

        mToolbar = findViewById(R.id.main_page_toolbar);//call toolbar
        drawerLayout = findViewById(R.id.darw_layout);
        navigationView = findViewById(R.id.nav_view);


        AskQuetionDialog = new Dialog(this);


        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        calcRef =FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("CalcAbuEl3orif");
        AbuEl3orifRefFinal = FirebaseDatabase.getInstance().getReference().child("finalAbuEl3orifs");


        mAuth = FirebaseAuth.getInstance();//to check user authentication loggedin or not

        CurrentUserId = mAuth.getCurrentUser().getUid();




        //--------to open side menue otmatically-----------------------------//
        actionBarDrawerToggle = new ActionBarDrawerToggle(SilverActivity.this, drawerLayout,mToolbar ,R.string.drawer_open,R.string.drawer_close)
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


        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //-----------------------------------------------------------------------
        View nav_view=navigationView.inflateHeaderView(R.layout.navigation_header);//to get view of navigation header

        navProfileImage = nav_view.findViewById(R.id.nav_profile_img);
        navUsername = nav_view.findViewById(R.id.nav_username);

        userRef.child(CurrentUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    // String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    String userProfileImage = dataSnapshot.child("ImageUrl").getValue().toString();

                    navUsername.setText(userFullName);
                    Glide.with(getApplicationContext()).load(userProfileImage).into(navProfileImage);
                    // Picasso.with(MainActivity.this).load(userProfileImage).placeholder(R.drawable.profile_icon).into(navProfileImage);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //-----------------------------------------------------------------------

        //--------------------------------------------------------------------
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("AbuEl3orif Map");//to set title to toolbar

        //---------------------------------------------------------------------


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                userMenueSelected(item);
                return false;
            }
        });
        //----------------------------------------------------------------------------------

        btn_filter = findViewById(R.id.btn_map_filter);
        final List<String> areas = new ArrayList<String>();
        final List<String> categories = new ArrayList<String>();
        AbuEl3orif_DB_Ref  = FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB");

        db =FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("categories");
        db2 =FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("AllPlaces");

        //  addressField = (EditText) findViewById(R.id.search_place_firebase_bar);
        // search =(ImageView) findViewById(R.id.search_place_firebase_icon);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }

       /* search.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view)
            {
                mMap.clear();
                final String address = addressField.getText().toString();

                if(TextUtils.isEmpty(address))
                {
                    Toast.makeText(getApplicationContext(), "searh text is empty...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    final List<String> ca_keys = new ArrayList<String>();
                    //final List<Category_class> ca_ob_2 = new ArrayList<Category_class>();
                    final MarkerOptions userMarkerOptions = new MarkerOptions();
                    db2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot caSnapshot : dataSnapshot.getChildren())
                            {
                                ca_keys.add(caSnapshot.getKey());
                            }
                          for(int i = 0 ; i < ca_keys.size() ; i++)
                          {
                                if(ca_keys.get(i).equals(address))
                                {
                                    db2.child(ca_keys.get(i)).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            Category_class categoryObj = dataSnapshot.getValue(Category_class.class);
                                            categoryName = categoryObj.getCategory_name();
                                            categoryLat = categoryObj.getCategory_latitude();
                                            categoryLng = categoryObj.getCategory_longitude();
                                            LatLng latLng = new LatLng(categoryLat , categoryLng);
                                            userMarkerOptions.position(latLng);
                                            userMarkerOptions.title(categoryName);
                                            userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                            if(category_marker !=null){
                                                category_marker.remove();
                                            }
                                            category_marker = mMap.addMarker(userMarkerOptions);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "EEEEEEEEE", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    addressField.setText("");
                }


            }
        });*/


        //-------------------------------for spinner areas----------------------------------------------------


        AbuEl3orif_DB_Ref.child("Areas").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                areas.clear();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren())
                {
                    Area_class area_object = areaSnapshot.getValue(Area_class.class);
                    String Area_name = area_object.getArea_name();
                    areas.add(Area_name);
                }

                sp_area = findViewById(R.id.sp_area_filter);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(SilverActivity.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_area.setAdapter(areasAdapter);

                sp_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        selected_Area = areas.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //--------------------------------------------------------------------------------------------------------------


        //-------------------------------for spinner category----------------------------------------------------


        AbuEl3orif_DB_Ref.child("categories").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categories.clear();

                for (DataSnapshot categorySnapshot: dataSnapshot.getChildren())
                {

                    String category_key = categorySnapshot.getKey();
                    categories.add(category_key);
                }

                sp_category = findViewById(R.id.sp_category_filter);
                ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(SilverActivity.this, android.R.layout.simple_spinner_item, categories);
                categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_category.setAdapter(categoriesAdapter);

                sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        selected_category = categories.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //--------------------------------------------------------------------------------------------------------------

        //--------------------------- Button Sreach Google Map-----------------------------------------------------------------------------------
        mSearchMapBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMap.clear();
                onMapSearch();
                locationSearch.setText("");
            }
        });

        //--------------------------------------------------------------------------------------------------------------


        //------------------btn filter method------------------------------------------------------

        btn_filter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMap.clear();

                AbuEl3orif_DB_Ref.child("Areas").child(selected_Area).child("categories").child(selected_category).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot ca_Snapshot: dataSnapshot.getChildren())
                        {
                            Category_class category_object = ca_Snapshot.getValue(Category_class.class);
                            double category_lat = category_object.getCategory_latitude();
                            double category_lng = category_object.getCategory_longitude();
                            String category_name = category_object.getCategory_name();

                            LatLng latLng = new LatLng(category_lat, category_lng);

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(category_name);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


                            mMap.addMarker(markerOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });

            }
        });

        //------------------------------------------------------------------------------------------

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void firebaseUserSearch(String searchText)
    {

        Query firebaseSearchQuery = db2.orderByChild("category_name").startAt(searchText).endAt(searchText + "\uf8ff");
        firebaseSearchQuery.toString().equalsIgnoreCase(searchText);
        FirebaseRecyclerOptions<Category_class> options=
                new FirebaseRecyclerOptions.Builder<Category_class>()
                        .setQuery(firebaseSearchQuery,Category_class.class)
                        .build();
        final FirebaseRecyclerAdapter<Category_class, GoogleMapsActivity.UsersViewHolder> adapter =
                new FirebaseRecyclerAdapter<Category_class, GoogleMapsActivity.UsersViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final GoogleMapsActivity.UsersViewHolder holder, final int position, @NonNull Category_class model)
                    {
                        final String searchKey=getRef(position).getKey();
                        db2.child(searchKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Category_class catSearch = dataSnapshot.getValue(Category_class.class);
                                String catName= catSearch.getCategory_name();
                                Log.e("ssssssssssssss",catName);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        holder.categoryName.setText("Name : " +model.getCategory_name());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String searchKey=getRef(position).getKey();
                                //Toast.makeText(getApplicationContext(), "El Galyyyyyyyyyyyy", Toast.LENGTH_SHORT).show();
                                db2.child(searchKey).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Category_class catSearch = dataSnapshot.getValue(Category_class.class);
                                        final MarkerOptions userMarkerOptions = new MarkerOptions();
                                        String catName= catSearch.getCategory_name();
                                        categoryName = catSearch.getCategory_name();
                                        categoryLat = catSearch.getCategory_latitude();
                                        categoryLng = catSearch.getCategory_longitude();
                                        LatLng latLng = new LatLng(categoryLat , categoryLng);
                                        userMarkerOptions.position(latLng);
                                        userMarkerOptions.title(categoryName);
                                        userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                        if(category_marker !=null){
                                            category_marker.remove();
                                        }
                                        category_marker = mMap.addMarker(userMarkerOptions);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                                        Log.e("ssssssssssssss",catName);
                                        Log.e("ssssssssssssss",""+categoryLat);
                                        Log.e("ssssssssssssss",""+categoryLng);
                                        //Toast.makeText(getApplicationContext(), "El Galyyyyyyyyyyyy Tanyyyyyyyyyyyyyyyyyy", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        });
                        holder.categoryRate.setText("Rate : " +model.getCategory_rate());
                    }
                    @NonNull
                    @Override
                    public GoogleMapsActivity.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout,parent,false);
                        context = parent.getContext();
                        GoogleMapsActivity.UsersViewHolder viewHolder= new GoogleMapsActivity.UsersViewHolder(view);
                        return viewHolder;
                    }
                };
        mResultList.setAdapter(adapter);
        adapter.startListening();


        //------------------------------------------------------------------------------------------------
        final List<String> ca_keys = new ArrayList<String>();
        //final List<Category_class> ca_ob_2 = new ArrayList<Category_class>();
        final MarkerOptions userMarkerOptions = new MarkerOptions();
      /*  db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot caSnapshot : dataSnapshot.getChildren())
                {
                    ca_keys.add(caSnapshot.getKey());
                }
                for(int i = 0 ; i < ca_keys.size() ; i++)
                {
                    if(ca_keys.get(i).equals(address))
                    {
                        db2.child(ca_keys.get(i)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                Category_class categoryObj = dataSnapshot.getValue(Category_class.class);
                                categoryName = categoryObj.getCategory_name();
                                categoryLat = categoryObj.getCategory_latitude();
                                categoryLng = categoryObj.getCategory_longitude();
                                LatLng latLng = new LatLng(categoryLat , categoryLng);
                                userMarkerOptions.position(latLng);
                                userMarkerOptions.title(categoryName);
                                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                if(category_marker !=null){
                                    category_marker.remove();
                                }
                                category_marker = mMap.addMarker(userMarkerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "EEEEEEEEE", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        // addressField.setText("");
        //------------------------------------------------------------------------------------------------
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    //My WOrk Here Recycler : : :

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        RecyclerView mRecyclerView;

        View mView;
        TextView categoryName,categoryRate;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            categoryName =  mView.findViewById(R.id.name_text);
            categoryRate =  mView.findViewById(R.id.status_rate);
        }
/*
        public void setDetails(String userName, int userStatus){


            //ImageView user_image = (ImageView) mView.findViewById(R.id.profile_image);


            categoryName.setText(userName);
            categoryRate.setText(userStatus);

            //Glide.with(ctx).load(userImage).into(user_image);


        }*/



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            //call buid google api client method
            buildGoogeApiClient();

            mMap.setMyLocationEnabled(true);


            return;
        }
    }

    //checkUserLocationPermission method ------------------------------------------
    public boolean checkUserLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this ,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.ACCESS_COARSE_LOCATION))
            {
                ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , Request_User_Location_Code);
            }
            else
            {
                ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , Request_User_Location_Code);

            }
            return false;

        }
        else
        {
            return true;
        }
    }
    //------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        switch (requestCode)
        {
            case Request_User_Location_Code:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this ,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient == null)
                        {
                            buildGoogeApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }


    //-----------------buid google api client method -------------------------

    protected synchronized void buildGoogeApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    //-----------------------------------------------------------------------

    @Override
    public void onLocationChanged(Location location)
    {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        lastLocation = location;

        if(currentUserLocationMarker != null)
        {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("your current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        if(googleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient ,  this);
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this ,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient , locationRequest , this);

        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //---------------------as main------------------------------------
    //--------------------------to make drwaer open when click on side button--------------


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //------------------------------------------------------------------------------------------


    //--------------to make action to each chosse ------------------------------------------------
    public void userMenueSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.nav_profile:
                sendUserToProfileActivity();
                break;

            case R.id.nave_home:
                Toast.makeText(this, "Home icon Action", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_saved_comments:
                sendUserToSavedCommentActivity();
                break;

//            case R.id.nav_followers:
//                Toast.makeText(this, "followers icon Action", Toast.LENGTH_SHORT).show();
//                break;

            case R.id.nav_settings:
                sendUserToSetupActivity();
                break;



            case R.id.nav_logout:

                mAuth.signOut();
                sendUserToLoginActivity();
                break;

            case R.id.ask_question:
                Toast.makeText(this, "ask queston icon Action", Toast.LENGTH_SHORT).show();
//                Intent quetionIntent = new Intent(GoogleMapsActivity.this , PostsActivity.class);
//                startActivity(quetionIntent);


                //-------------------------------for spinner areas----------------------------------------------------
                final List<String> areas = new ArrayList<String>();

                final List<String> categories = new ArrayList<String>();
                AbuEl3orif_DB_Ref  = FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB");


                AskQuetionDialog.setContentView(R.layout.custompopup);
                AskQuetionDialog.setTitle("Your Question about !?");

                textCloseIcon = AskQuetionDialog.findViewById(R.id.textClose);
                btn_que_filtter = AskQuetionDialog.findViewById(R.id.btn_question_filtter);
                //---------------------close dialog--------------------
                textCloseIcon.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        AskQuetionDialog.dismiss();

                    }
                });
                //-----------------------------------------------------



                AbuEl3orif_DB_Ref.child("Areas").addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        areas.clear();

                        for (DataSnapshot areaSnapshot: dataSnapshot.getChildren())
                        {
                            Area_class area_object = areaSnapshot.getValue(Area_class.class);
                            String Area_name = area_object.getArea_name();
                            areas.add(Area_name);
                        }



                        sp_area_popup = AskQuetionDialog.findViewById(R.id.sp_area_filter);
                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(SilverActivity.this, R.layout.custom_spinner_layout, areas);
                        areasAdapter.setDropDownViewResource( R.layout.custom_spinner_layout);
                        sp_area_popup.setAdapter(areasAdapter);

                        sp_area_popup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                        {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                            {
                                selected_Area = areas.get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //--------------------------------------------------------------------------------------------------------------


                //-------------------------------for spinner category----------------------------------------------------


                AbuEl3orif_DB_Ref.child("categories").addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        categories.clear();

                        for (DataSnapshot categorySnapshot: dataSnapshot.getChildren())
                        {

                            String category_key = categorySnapshot.getKey();
                            categories.add(category_key);
                        }

                        sp_category_popup = AskQuetionDialog.findViewById(R.id.sp_category_filter);
                        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(SilverActivity.this,  R.layout.custom_spinner_layout, categories);
                        categoriesAdapter.setDropDownViewResource( R.layout.custom_spinner_layout);
                        sp_category_popup.setAdapter(categoriesAdapter);

                        sp_category_popup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                        {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                            {
                                selected_category = categories.get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //--------------------------------------------------------------------------------------------------------------

                //------------------btn filter method------------------------------------------------------

                btn_que_filtter.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent quetionIntent = new Intent(SilverActivity.this , QuestionActivity.class);
                        quetionIntent.putExtra("SELAREA", selected_Area);
                        quetionIntent.putExtra("SELCATEGORY", selected_category);
                        startActivity(quetionIntent);

//                        AbuEl3orif_DB_Ref.child("Areas").child(selected_Area).child("categories").child(selected_category).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//                            {
//                                for (DataSnapshot ca_Snapshot: dataSnapshot.getChildren())
//                                {
//                                    Category_class category_object = ca_Snapshot.getValue(Category_class.class);
//                                    double category_lat = category_object.getCategory_latitude();
//                                    double category_lng = category_object.getCategory_longitude();
//                                    String category_name = category_object.getCategory_name();
//
//                                    LatLng latLng = new LatLng(category_lat, category_lng);
//
//                                    MarkerOptions markerOptions = new MarkerOptions();
//                                    markerOptions.position(latLng);
//                                    markerOptions.title(category_name);
//                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//
//
//                                    mMap.addMarker(markerOptions);
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                                    mMap.animateCamera(CameraUpdateFactory.zoomBy(15));
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError)
//                            {
//
//                            }
//                        });

                    }
                });

                //------------------------------------------------------------------------------------------
                AskQuetionDialog.show();
                break;

//            case R.id.add_post:
//
//                Intent PostIntent = new Intent(GoogleMapsActivity.this , NewPostActivity.class);
//                startActivity(PostIntent);
//                break;
            case R.id.show_post:
                Toast.makeText(this, "ask queston icon Action", Toast.LENGTH_SHORT).show();
                Intent allPostsIntent = new Intent(SilverActivity.this , MainSecondActivity.class);
                startActivity(allPostsIntent);
                break;
            case R.id.make_chat:
                Toast.makeText(this, "ask queston icon Action", Toast.LENGTH_SHORT).show();
                Intent ChatIntent = new Intent(SilverActivity.this , StartActivity.class);
                startActivity(ChatIntent);
                break;

            case R.id.followAbo:
                Toast.makeText(this, "ask queston icon Action", Toast.LENGTH_SHORT).show();
                Intent AboIntent = new Intent(SilverActivity.this , AboEl3orif.class);
                startActivity(AboIntent);
                break;
        }

    }

    private void sendUserToLoginActivity()
    {
        Intent goToLoginActivity = new Intent(getApplicationContext(),Login_Activity.class);
        goToLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToLoginActivity);
        finish();
    }
    private void sendUserToSavedCommentActivity()
    {
        Intent goToSavedCommentActivity = new Intent(getApplicationContext(),SavedCommentActivity.class);
        //goToSavedCommentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToSavedCommentActivity);

    }
    private void sendUserToSetupActivity()
    {
        Intent goToSetupActivity = new Intent(getApplicationContext(),setup_user_Activity.class);
        startActivity(goToSetupActivity);

    }
    private void sendUserToProfileActivity() {
        Intent goToProfileActivity = new Intent(getApplicationContext(),UserProfileActivity.class);
        startActivity(goToProfileActivity);

    }



    //-----------------------------------------sreach Google Map----------------------------------------------------------
    public void onMapSearch()
    {
        String location = locationSearch.getText().toString().toLowerCase();
        List<Address> addressList = null;

        if(!TextUtils.isEmpty(location))
        {
            Geocoder geocoder = new Geocoder(this);
            try
            {
                addressList = geocoder.getFromLocationName(location, 6);

                if(addressList != null)
                {
                    for(int i = 0 ; i < addressList.size() ; i++)
                    {
                        Address address = addressList.get(i);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("here your Search Result");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Sorry This Location Not Found ..", Toast.LENGTH_SHORT).show();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }


        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please enter place name to search about ...", Toast.LENGTH_SHORT).show();
        }


    }

    public static HashMap<String, Integer> sortByValue(HashMap <String , Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


}
