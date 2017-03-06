package com.adk.projectsmanager;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.TabLayout.MODE_SCROLLABLE;

public class DataTabs extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    private int[] icons = {R.drawable.add_description, R.drawable.add_member, R.drawable.add_new_tasks, R.drawable.icon_calendar, R.drawable.icon_notes};
    public Firebase mRef;
    AppBarLayout appBarLayout;
    private String mUserId;
    FirebaseConfig firebaseConfig;
    String descriptionURL, membersURL, tasksURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_tabs);
        appBarLayout = (AppBarLayout)findViewById(R.id.app_bar_layout);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams)toolbar.getLayoutParams();
        firebaseConfig = new FirebaseConfig();
        params.setScrollFlags(0);
        appBarLayout.setElevation(0);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

            setupViewPager(viewPager);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.setVisibility(View.GONE);

        //mRef is used to logout of the activity when back button is pressed
        Firebase.setAndroidContext(this);
        mRef = new Firebase(FirebaseConfig.FIREBASE_URL);
        try {
            mUserId = mRef.getAuth().getUid();
        } catch (Exception e) {
            Log.i("TAG", "error auth");
        }

        membersURL = FirebaseConfig.FIREBASE_URL  + "/users/" + mUserId + "/Members";
        firebaseConfig.setMembersURL(membersURL);
        tasksURL = FirebaseConfig.FIREBASE_URL  + "/users/" + mUserId + "/Tasks";
        firebaseConfig.setTasksURL(tasksURL);


        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(icons[0]);
        tabLayout.getTabAt(1).setIcon(icons[1]);
        tabLayout.getTabAt(2).setIcon(icons[2]);
        tabLayout.getTabAt(3).setIcon(icons[3]);
        tabLayout.getTabAt(4).setIcon(icons[4]);
    }


    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new DescriptionFragment(), "Description");
        viewPagerAdapter.addFragment(new MembersFragment(), "Members");
        viewPagerAdapter.addFragment(new TasksFragment(), "Tasks");
        viewPagerAdapter.addFragment(new CalendarFragment(), "Calendar");
        viewPagerAdapter.addFragment(new NotesFragment(), "Notes");
        viewPager.setAdapter(viewPagerAdapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter{
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();


        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
         returnToMainActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void returnToMainActivity(){
        mRef.unauth();
        Intent intent = new Intent (DataTabs.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
