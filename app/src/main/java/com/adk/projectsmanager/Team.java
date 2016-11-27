package com.adk.projectsmanager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Team extends AppCompatActivity {

    private List<TeamMembers> tm = new ArrayList<>();
    private TeamMembersAdapter teamMembersAdapter;
    TextView projectName, projectDescription;
    public Firebase mRef;
    private String mUserId;
    private String membersUrl;
    private String descUrl;
    private String tasksUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);
          mRef = new Firebase(Config.FIREBASE_URL);
        //login/reg


        try {
            mUserId = mRef.getAuth().getUid();
        } catch (Exception e) {
            MainActivity login = new  MainActivity();
            login.loadLoginView();
        }

        membersUrl = Config.FIREBASE_URL  + "/users/" + mUserId + "/Members";
        descUrl = Config.FIREBASE_URL  + "/users/" + mUserId + "/Description";
        tasksUrl = Config.FIREBASE_URL  + "/users/" + mUserId + "/Tasks";

      TaskURL tsk = new TaskURL();
        tsk.setTasksURL(tasksUrl);

       // Toast.makeText(this, mRef.getAuth().getUid(),Toast.LENGTH_SHORT).show();
        //./login/reg
        //creates an object to return true if touch is detected
        final GestureDetector mGestureDetector = new GestureDetector(Team.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });
        //./creates an object to return true if touch is detected
        sync();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callActivity();
            }
        });


        //registering widgets, adapters, data structures and layouts
        RecyclerView recyclerView;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        teamMembersAdapter = new TeamMembersAdapter(tm);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(teamMembersAdapter);


        //intercepts onclick and gets position
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
                if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
                    int pos = recyclerView.getChildPosition(child);
                   // delete(pos);
                    dialogDeleteMember(pos);

                    return true;
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        //./intercepts onclick and gets position


    }


    public void sync (){
        View parentLayout = findViewById(R.id.root_view);
        Snackbar snackbar = Snackbar
                .make(parentLayout, R.string.synchronizing, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
        syncMembers();
        syncDescription();
    }


    public void dialogCreateMember(){
        View v = LayoutInflater.from(Team.this).inflate(R.layout.create_member, null);
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(Team.this);
        alertdialog.setView(v);
        final EditText memberName = (EditText)v.findViewById(R.id.member_name);
        final EditText memberPosition = (EditText)v.findViewById(R.id.member_position);
        memberName.setHint(R.string.hint_member_name);
        memberPosition.setHint(R.string.hint_member_position);
        alertdialog.setCancelable(true);
        alertdialog.setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = memberName.getText().toString();
                String position = memberPosition.getText().toString();
                uploadMember(name, position);
            }

        });
        alertdialog.setNegativeButton(R.string.dialog_negative_button_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertdialog.setTitle(R.string.create_new_member).setIcon(R.drawable.add_member);
        Dialog dialog  = alertdialog.create();
        dialog.show();
    }


    public final void uploadMember(String name, String position){
        //saving name and occupation in map
        Map<String, Object> post1 = new HashMap<>();
        post1.put("name", name);
        post1.put("position", position);
        //Storing values to firebase
        //itemsurl creates link there to upload
        new Firebase(membersUrl)
                .push()
                .child("Person")
                .setValue(post1);

    }


    public void syncMembers(){

        //syncing with firebase
        Firebase ref = new Firebase(membersUrl);

        ref.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to Firebase
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                //fetches data for teammembers
                String name = (String) snapshot.child("Person").child("name").getValue();
                String occupation = (String) snapshot.child("Person").child("position").getValue();
                //System.out.println(occupation);
                prepareTeamMembersData(name, occupation);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(Team.this,R.string.toast_member_deleted,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
            //... ChildEventListener also defines onChildChanged, onChildRemoved,
            //    onChildMoved and onCanceled, covered in later sections.
        });
    }


    public void dialogDeleteMember(final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_title_delete_member);
        builder.setMessage(R.string.dialog_message_delete_member);

        builder.setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // deletes member and its parent name from list
                delete(pos);
                //restarts activity
                finish();
                startActivity(getIntent());
                //./restarts activity
                dialog.dismiss();
            }

        });

        builder.setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // I do not need any action here you might
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }


    public void delete(final int position){
        final Firebase ref = new Firebase(membersUrl);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> IDList = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //Toast.makeText(Main2Activity.this,child.toString().substring(21,41),Toast.LENGTH_LONG).show();
                    IDList.add(child.toString().substring(21,41));
                }
                String del = IDList.get(position);
                Toast.makeText(Team.this, R.string.toast_member_deleted, Toast.LENGTH_LONG).show();
                ref.child(del).child("Person").child("name").removeValue();
                ref.child(del).child("Person").child("position").removeValue();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }



    public final void dialogCreateDescription(String name, String description){
        View v = LayoutInflater.from(Team.this).inflate(R.layout.create_description,null);
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(Team.this);
        alertdialog.setView(v);
        final EditText projectName = (EditText)v.findViewById(R.id.project_name);
        final EditText projectDescription = (EditText)v.findViewById(R.id.project_description);
        projectName.setHint(R.string.hint_project_name);
        projectDescription.setHint(R.string.hint_project_description);
        projectName.setText(name);
        projectDescription.setText(description);
        alertdialog.setCancelable(true);
        alertdialog.setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Creating firebase object
                String name =  projectName.getText().toString();
                String description = projectDescription.getText().toString();
                uploadDescription (name, description);
                //Getting values to store


                Toast.makeText(Team.this, R.string.toast_description_saved, Toast.LENGTH_SHORT).show();
            }
        });
        alertdialog.setNegativeButton(R.string.dialog_negative_button_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertdialog.setTitle(R.string.create_description).setIcon(R.drawable.icon_add_new);
        Dialog dialog = alertdialog.create();
        dialog.show();



    }


    private void uploadDescription (String name, String description){
        //saving name and occupation in map
        Map<String, Object> post1 = new HashMap<>();
        post1.put("Project name", name);
        post1.put("Project description", description);
        //Storing values to firebase
        new Firebase(descUrl)
                .push()
                .child("Description")
                .setValue(post1);
    }


    private void syncDescription(){
        projectName = (TextView)findViewById(R.id.project_name);
        projectDescription = (TextView)findViewById(R.id.project_description);
        projectName.setHint(R.string.hint_project_name);
        projectDescription.setHint(R.string.hint_project_description);
        projectDescription.setMovementMethod(new ScrollingMovementMethod());
        //syncing with firebase
        Firebase ref = new Firebase(descUrl);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //fetches data for description
                String nameProject = (String) dataSnapshot.child("Description").child("Project name").getValue();
                String descriptionProject = (String) dataSnapshot.child("Description").child("Project description").getValue();

                /*both editTexts sync info from Firebase and when dialogCreateDescription() (to edit the description) is called in onOptionsItemSelected()
                these two are passed to it so that the info is loaded in the dialog and can be edited*/
                projectName.setText(nameProject);
                projectDescription.setText(descriptionProject);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


    //sending data to objects (TeamMembers) which is then accessed in TeamMembersAdapter via set or get methods
    private void prepareTeamMembersData(String name, String occupancy) {

        TeamMembers teamMembers = new TeamMembers(name, occupancy, R.drawable.member);
        tm.add(teamMembers);
        teamMembersAdapter.notifyDataSetChanged();
    }
    



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            dialogCreateMember();

            return true;
        }
        else if(id == R.id.action_add_description){
            dialogCreateDescription(projectName.getText().toString(), projectDescription.getText().toString());

            return true;
        }
        else if(id==R.id.action_logout){
            mRef.unauth();
            Intent intent = new Intent(Team.this,MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void callActivity(){

        Intent intent = new Intent(this, Tasks.class);
        startActivity(intent);
        overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            mRef.unauth();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
