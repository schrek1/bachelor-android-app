package cz.vspj.schrek.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import cz.vspj.schrek.im.R;
import cz.vspj.schrek.im.common.LoggedUser;
import cz.vspj.schrek.im.common.Utils;
import cz.vspj.schrek.im.fragment.friends.FriendsListFragment;
import cz.vspj.schrek.im.fragment.meetups.MeetupsListFragment;
import cz.vspj.schrek.im.fragment.messages.ConversationListFragment;
import cz.vspj.schrek.im.model.User;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private ActionBarDrawerToggle actionBar;

    private FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                startActivity(new Intent(getApplicationContext(), LoginAcitivty.class));
                finish();
            } else {
                LoggedUser.setCurrentUser(new User(user.getUid(), user.getEmail()));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDefaultToolbar();

        replaceFragment(new ConversationListFragment());
    }

    public void createDefaultToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        LoggedUser.setCurrentUser(new User(user.getUid(), user.getEmail()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBar = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(actionBar);
        actionBar.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void showMenuIcon(boolean visible) {
        actionBar.setDrawerIndicatorEnabled(visible);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (R.id.action_logout == id) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
//            dbRef.child("app").child("users").child(LoggedUser.getCurrentUser().uid).child("info").child("instanceId").removeValue();
            Utils.setUserOfflineState(LoggedUser.getCurrentUser().uid);
            firebaseAuth.signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_messages) {
            replaceFragment(new ConversationListFragment());
        } else if (id == R.id.nav_groups) {

        } else if (id == R.id.nav_friends) {
            replaceFragment(new FriendsListFragment());
        } else if (id == R.id.nav_meetups) {
            replaceFragment(new MeetupsListFragment());
        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //clear backstack
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        transaction.replace(R.id.frameView, fragment);
        transaction.commit();
    }

    public void pushFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameView, fragment);
        transaction.addToBackStack(fragment.getTag());
        transaction.commit();
    }

    public void popBackStack() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Utils.setUserOnlineState(LoggedUser.getCurrentUser().uid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.setUserOfflineState(LoggedUser.getCurrentUser().uid);
    }


}
