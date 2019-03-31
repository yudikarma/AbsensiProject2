package com.example.alfattah.absensiproject;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.alfattah.absensiproject.fragment_mainActivity.AboutFragment;
import com.example.alfattah.absensiproject.fragment_mainActivity.DashboardFragment;
import com.example.alfattah.absensiproject.fragment_mainActivity.DashboardMonitoringFragment;
import com.example.alfattah.absensiproject.fragment_mainActivity.InputDataPerusahaanFragment;
import com.example.alfattah.absensiproject.fragment_mainActivity.SettingsFragment;
import com.example.alfattah.absensiproject.utils.PreferenceManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

/*
    @BindView(R.id.test)
    LinearLayout test;*/

    //fIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mUserRef;
    private DatabaseReference mdataperusahaan;


    //user display
    /*@BindView(R.id.display_profil_cirle)*/
    CircleImageView circleImageView;
    /*@BindView(R.id.display_name)*/
    TextView display_name;
    /*@BindView(R.id.display_job)*/
    TextView display_address;
    //navigation menu
    @BindView(R.id.drawer_layout_main)
    DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    @BindView(R.id.toolbar_appbar)
    Toolbar toolbar;
    @BindView(R.id.navigationMenu)
    NavigationView navigationView;

    private PreferenceManager preferenceManager;


    //fragment
    DashboardFragment dashboardFragment;
    DashboardMonitoringFragment dashboardMonitoringFragment;
    SettingsFragment settingsFragment;
    AboutFragment aboutFragment;
    InputDataPerusahaanFragment inputDataPerusahaanFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        //toolbar action
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setTitle("Absensi Project");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        //navigation action
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        View inflateView = navigationView.getHeaderView(0);
        preferenceManager = new PreferenceManager(this);
        circleImageView = inflateView.findViewById(R.id.display_profil_cirle);
        display_name = inflateView.findViewById(R.id.display_name);
        display_address = inflateView.findViewById(R.id.display_job);

        //userinformation



        //Getting Uid user


        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = ""+dataSnapshot.child("nama").getValue().toString();
                    String jabatan = ""+dataSnapshot.child("jabatan").getValue().toString();
                    String image = ""+dataSnapshot.child("image").getValue().toString();
                    display_name.setText(""+name);
                    display_address.setText(""+jabatan);
                    if (!image.equals("default") && image!= null){
                        Glide.with(getApplicationContext()).load(image).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(circleImageView);
                    }else {

                    }

                    mUserRef.child("role").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String srole = dataSnapshot.getValue().toString();


                            preferenceManager.simpanRole(srole);
                            setdashbourUI(preferenceManager.ambilRole());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }

        //inisialisasi fragment
        dashboardFragment = new DashboardFragment();
        dashboardMonitoringFragment = new DashboardMonitoringFragment();
        settingsFragment = new SettingsFragment();
        aboutFragment = new AboutFragment();
        inputDataPerusahaanFragment = new InputDataPerusahaanFragment();




        //drawerlayout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();







    }


    //Method set Fragment
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_main, fragment);
        /*fragmentTransaction.addToBackStack(null);*/
        fragmentTransaction.commit();
    }
    private void setdashbourUI(String role) {

        if (role.equalsIgnoreCase("staff")){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_main, dashboardFragment);
            fragmentTransaction.commitAllowingStateLoss();

        }else {
            checkdataperusahaan();


        }

    }


    //MEthod backpressed to exit
    long back_pressed;

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Toast.makeText(getBaseContext(), "Press Back once again to exit!", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            sendTostart();
        } else {
            mUserRef.child("online").setValue("true");
        }*/
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (!(currentUser != null)) {
            // !User is signed in
            sendTostart();


        } else {
            mUserRef.child("online").setValue("true");


        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }
    }

    private void sendTostart() {
        //Check i user is Sign-out
        Intent startIntent = new Intent(MainActivity.this, HomeActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);

        finish();

    }

    private void checkdataperusahaan(){
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("DataPerusahaan")){
                    //belum punya
                    Log.i("tidak ada database", "DataPerusahaan");
                    // Hiding the title bar has to happen before the view is created
                   /* requestWindowFeature(Window.FEATURE_NO_TITLE);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
                    // Hide the Status Bar
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_main, inputDataPerusahaanFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();



                }else {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_main, dashboardMonitoringFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    Log.i(" database Ini tersedia", "DataPerusahaan ");
                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        switch (id) {
            case R.id.dashboard:

                /*setFragment(dashboardFragment);*/
                String cekrole = preferenceManager.ambilRole();
                Log.i("PREFMANAGER ROLE", ""+cekrole);
                if (cekrole.equalsIgnoreCase("staff")){
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.frame_main, dashboardFragment).commit();
                    break;
                }else if (cekrole.equalsIgnoreCase("Monitoring")){
                    checkdataperusahaan();
                    break;

                }
                break;

            case R.id.acountSettings:
                /*setFragment(settingsFragment);*/
                Intent intent = new Intent(MainActivity.this,SettingsFragment.class);
                startActivity(intent);
                break;
            case R.id.Logout:
                mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

                FirebaseAuth.getInstance().signOut();
                sendTostart();
                break;

            case R.id.about:
                setFragment(aboutFragment);
                break;
            default:
                break;





        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

}
