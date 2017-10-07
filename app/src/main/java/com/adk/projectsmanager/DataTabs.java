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
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.TabLayout.MODE_SCROLLABLE;

public class DataTabs extends AppCompatActivity implements StatisticsChartFragment.FilterListener {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
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
        viewPager.setElevation(0);


        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

            setupViewPager(viewPager);
        //TODO: convert all check to this:
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.setVisibility(View.GONE);
        //getSupportActionBar().hide();

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



    }




    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new DescriptionFragment(), "Description");
        viewPagerAdapter.addFragment(new MembersFragment(), "Members");
        viewPagerAdapter.addFragment(new TasksFragment(), "Tasks");
        viewPagerAdapter.addFragment(new CalendarFragment(), "Calendar");
        //viewPagerAdapter.addFragment(new NotesFragment(), "Notes");
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void setFilter(String filterWIP) {
        //TasksFragment tasksFragment = new TasksFragment();
        //tasksFragment.setFilter(filterWIP);

        TasksFragment.taskFilter = filterWIP;
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
