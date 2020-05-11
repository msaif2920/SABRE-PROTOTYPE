package com.example.prototypesabre.AuthenticatedUserFragment.Actions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.prototypesabre.AuthenticatedUserFragment.Actions.complementOthers;
import com.example.prototypesabre.AuthenticatedUserFragment.Chat.Chat;
import com.example.prototypesabre.AuthenticatedUserFragment.CreateGroup.CreateGroup;
import com.example.prototypesabre.AuthenticatedUserFragment.Group.Group;
import com.example.prototypesabre.AuthenticatedUserFragment.GroupRequest.GroupRequest;
import com.example.prototypesabre.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AuthenticateduserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    FirebaseAuth mauth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticateduser);


        Toolbar toolbar = findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout2);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);


        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.Create_Group:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CreateGroup()).commit();
                break;

            case R.id.Group_Request:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new GroupRequest()).commit();
                break;


            case R.id.Group:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Group()).commit();


                break;

            case R.id.chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Chat()).commit();

                break;


        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.users_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.SignOut:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.Compliment:
                Intent intent = new Intent(getApplicationContext(), complementOthers.class);
                startActivity(intent);
                return true;

            case R.id.complaints:
                Intent intent1 = new Intent(getApplicationContext(), Report.class);
                startActivity(intent1);
                return true;
        }
        return false;
    }
}
