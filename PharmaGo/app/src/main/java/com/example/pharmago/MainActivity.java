package com.example.pharmago;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.example.pharmago.controller.NotificationController;
import com.example.pharmago.model.User;
import com.example.pharmago.ui.IOnBackPressed;
import com.example.pharmago.ui.dashboard.DashBoardFragment;
import com.example.pharmago.ui.home.HomeFragment;
import com.example.pharmago.ui.home.HomeNewFragment;
import com.example.pharmago.ui.notification.NotificationFragment;
import com.example.pharmago.ui.outlet.ClosestPharmacyFragment;
import com.example.pharmago.ui.outlet.OutletListFragment;
import com.example.pharmago.util.NotificationMessenger;
import com.example.pharmago.util.SharedPref;
import com.example.pharmago.util.TimeUtil;
import com.example.pharmago.util.Update_manger;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;


import androidx.appcompat.app.ActionBar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private SharedPref sharedPref;
    private TextView textName, textMail, textVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = new SharedPref(MainActivity.this);
        User user = sharedPref.getStoredUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        if (user.getType() == User.PHARMACY) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_drawer);
            getFragment(1, "Home", "","");
        } else {

            getFragment(4, "Home", "","");
        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        View nav_header = navigationView.getHeaderView(0);


        textName = (TextView) nav_header.findViewById(R.id.textName);
        textMail = (TextView) nav_header.findViewById(R.id.textMail);
        textVersion = (TextView) nav_header.findViewById(R.id.textVersion);

        String versionName = " - ";
        PackageManager manager = MainActivity.this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(MainActivity.this.getPackageName(), 0);

            versionName = info.versionName;


        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
        }

        textVersion.setText("Version : " + versionName);


        if (user != null) {
            textName.setText(user.getName());
            textMail.setText(user.getName());


        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();

                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here

                String title = menuItem.getTitle().toString();
                if (menuItem.getItemId() == R.id.nav_dashboard) {
                    getFragment(0, title, "","");

                } else if (menuItem.getItemId() == R.id.nav_outlet_list) {
                    getFragment(2, title, "","");


                } else if (menuItem.getItemId() == R.id.nav_notification) {
                    getFragment(3, title, "","");
                } else if (menuItem.getItemId() == R.id.nav_chat_bot) {
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);
                    finish();

                } else if (menuItem.getItemId() == R.id.nav_logout) {
                    sharedPref.clearPref();
                    NotificationController.clearNotificationTable(MainActivity.this);
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else if (menuItem.getItemId() == R.id.nav_home) {
                    getFragment(4, title, "Home","");
                }


                return true;

            }
        });

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );


        startServices();


        //   throw new RuntimeException("Test Crash");

        try {

            Intent data = getIntent();
            Bundle bundle = data.getExtras();
            String navPositionS = bundle.getString("navPosition");

            Integer navPosition = Integer.parseInt(navPositionS);

            if (navPosition != 0) {
                String content = "";
                String originalContent = "";


                if (navPosition == 2) {
                    content = bundle.getString("content");
                    originalContent = bundle.getString("originalContent");
                }

                getFragment(navPosition, "", content,originalContent);

            }
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }

    }

    private void getFragment(int index, String title, String content,String originalContent) {
        Fragment fragment = null;

        switch (index) {
            case 0:
                fragment = DashBoardFragment.getInstance();
                break;

            case 1:
                fragment = HomeFragment.getInstance();
                break;

            case 2:
                fragment = ClosestPharmacyFragment.getInstance(content,originalContent);
                break;

            case 3:
                fragment = NotificationFragment.getInstance();
                break;
            case 4:
                fragment = HomeNewFragment.getInstance();
                break;
            default:
                fragment = HomeFragment.getInstance();
                break;
        }

        getSupportActionBar().setTitle(title);

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();

            // set the toolbar title

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void startServices() {

        Intent iv = new Intent(MainActivity.this, Update_manger.class);
        startService(iv);
        Intent i = new Intent(MainActivity.this, NotificationMessenger.class);
        startService(i);

    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {

            new MaterialAlertDialogBuilder(this).setTitle("Exit").setMessage("Do You Really Want To Exit?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();


        }
    }
}