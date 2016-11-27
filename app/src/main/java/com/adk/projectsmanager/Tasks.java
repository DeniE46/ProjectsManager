package com.adk.projectsmanager;

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
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Tasks extends AppCompatActivity {
    //adding two lines of code for the adapter and teamMembers class to work


    private Calendar calendar;
    private int year, month, day;
    private List<TeamMembers> tm = new ArrayList<>();
    private TaskAdapter taskAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Firebase.setAndroidContext(this);




        //creates an object to return true if touch is detected
        final GestureDetector mGestureDetector = new GestureDetector(Tasks.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });
        //./creates an object to return true if touch is detected

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // fab.setBackgroundColor(getResources().getColor(Color.red));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              callActivity();

            }

        });
         sync();



        //registering widgets, adapters, data structures and layouts
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        taskAdapter = new TaskAdapter(tm);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(taskAdapter);

       // prepareTeamMembersData();



        //intercepts onclick and gets position
        //pos is int containing the position of the card view that is being clicked
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
                    if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
                        int pos = recyclerView.getChildPosition(child);
                        //dialogTaskOptions(pos);
                        taskOptions(pos);
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



    public void taskOptions(final int position){
        final Firebase ref = new Firebase(TaskURL.tasksURL);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> IDList = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //iterates through all of the Task node children and saves their names in a list called IDList
                    IDList.add(child.toString().substring(21,41));
                }
               final String uniqueNode = IDList.get(position);
                //start of dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Tasks.this);
                builder.setTitle("Task options");
                builder.setItems(new CharSequence[]
                                {"Delete task", "Task is completed"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    //case 0: deletes info in certain node specified by the position that is passed
                                    case 0:
                                        //values should be checked, if  null is returned the toast is displayed
                                        Toast.makeText(Tasks.this, "Task deleted", Toast.LENGTH_SHORT).show();
                                        // deletes task and its parent name from list
                                        //deleting certain node can be done like this as well: ref.child(uniqueNode).child("Tasks").removeValue();
                                        //uniqueNode needs better name, e.g. TimeStampNodeName, should check Firebase name
                                        /*current method of data deletion:
                                        1. delete data in Firebase
                                        2. restart activity so that when it loads again the method that iterates through the data fills the list but without the element that
                                        had been deleted, then the recycler view is filled using the data from the list thus eliminating the need for recycler view update and element removal
                                        +simple
                                        +it works
                                        + no need to update objects
                                        -not too professional
                                        -flickering caused by activity restart
                                        -has to iterate through the base after each deletion (if bad it will be because it may has to iterate through lots of information)
                                       ####Can be improved by putting the code that restars the activity in OnDataChange() method
                                         future method of data deletion:
                                         1. delete the data from Firebase
                                         2. delete the element from the list using the corresponding position
                                         3. delete the element in and update the recycler view to repopulate with updated data
                                        +one iterating through Firebase's data is needed
                                        +additional iterations may be planned (once in an hour for example)
                                        +should improve the logic and the quality of the code (using native approach)
                                        +remove the screen flickering caused by activity restart
                                        -no real performance upgrade
                                        -more code involved/more difficult to maintain
                                        */
                                        ref.child(uniqueNode).child("Tasks").child("task description").removeValue();
                                        ref.child(uniqueNode).child("Tasks").child("task name").removeValue();
                                        ref.child(uniqueNode).child("Tasks").child("task owner").removeValue();
                                        ref.child(uniqueNode).child("Tasks").child("task status").removeValue();
                                        ref.child(uniqueNode).child("Tasks").child("flag").removeValue();
                                        //restarts activity
                                        finish();
                                        startActivity(getIntent());
                                        //./restarts activity
                                        dialog.dismiss();
                                        break;
                                    //case 1: changes/modifies specific information in certain node specified by the position that is passed
                                    //can be used as a base to add modify task/member
                                    //basic modification should be: add option and modify the existing dialog to show modify option, which then calls
                                    //new dialog that loads information thanks to the position of the card, when this info is modified the information is saved just like flag updating
                                    case 1:
                                        Toast.makeText(Tasks.this, "Task completed", Toast.LENGTH_SHORT).show();
                                        ref.child(uniqueNode).child("Tasks").child("flag").setValue("true");
                                        //restarts activity
                                        finish();
                                        startActivity(getIntent());
                                        //./restarts activity
                                        break;
                                    // case 2: when "modify task" is selected in the dialog another dialog may appear to let the user decide what to modify:
                                    // title, owner (should be done with context menu)etc and the case should look like:
                                    /*  case 2:
                                        ref.child(uniqueNode).child("Tasks").child("task description").setValue(value entered by the user goes here);
                                        ref.child(uniqueNode).child("Tasks").child("task name").setValue(value entered by the user goes here);
                                        ref.child(uniqueNode).child("Tasks").child("task owner").setValue(value entered by the user goes here);
                                        ref.child(uniqueNode).child("Tasks").child("task status").setValue(value entered by the user goes here);
                                        ref.child(uniqueNode).child("Tasks").child("flag").setValue(value entered by the user goes here);
                                        break;
                                        //"value entered by the user goes here" will be taken from the dialog where user inputs info
                                        "CreateTask" activity may be reused to modify tasks rather than creating new dialog, would be easier for smaller screens as well,
                                        that means that "CreateTask" will be loaded with the info of the chosen task/card, using its position, this activity might be changed with fragment to make the app lighter
                                        which may lead to building tasks list and task creation on one activity using fragments
                                        Same thing will be done to Team activity, which may be converted to fragment as well, thus getting Team, Tasks and CreateTasks to fragments and call them on same activity
                                        at which point the LogIn and and Register may be fragments as well meaning the whole app may be built on single activity
                                    */
                                }
                            }
                        });

                builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                //end of dialog

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }



    public void callActivity(){
        Intent intent = new Intent(this, CreateTasks.class);
        startActivity(intent);
        overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(this, Team.class);
            startActivity(intent);
            finish();
            overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void sync (){
        View parentLayout = findViewById(R.id.root_view);
        Snackbar snackbar = Snackbar
                .make(parentLayout, "Synchronizing...", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
        syncTasks();
    }


    //syncing with Firebase
    public final void syncTasks(){

        Firebase ref = new Firebase(TaskURL.tasksURL);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                long diff = 0;
                //fetching data from Firebase for task name, task owner, task status and task description
                String taskName = (String) dataSnapshot.child("Tasks").child("task name").getValue();
                String taskOwner = (String) dataSnapshot.child("Tasks").child("task owner").getValue();
                String taskDateCreated = (String) dataSnapshot.child("Tasks").child("task status").getValue();
                String taskDescription = (String) dataSnapshot.child("Tasks").child("task description").getValue();
                String flag = (String)dataSnapshot.child("Tasks").child("flag").getValue();
                //./fetching data from Firebase for task name, task owner, task status, task description and flag

                /*choosing format, getting current date, transforming it to StringBuilder, then string,
                 then date and comparing it to datePicker, then getting the difference in days*/
                SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                StringBuilder currentDateStringBuilder = new StringBuilder().append(day).append(" ")
                        .append(month+1).append(" ").append(year);
                String currentDateString = currentDateStringBuilder.toString();
                try
                {
                    Date currentDate = formatter.parse(currentDateString);
                    //deadline for the task:
                    Date pickerDate = formatter.parse(taskDateCreated);
                     diff = pickerDate.getTime() - currentDate.getTime();
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
                //converting the difference into int
                long timeRemaining = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                prepareTeamMembersData(taskName, taskOwner, timeRemaining, taskDescription, flag);
                /*./getting current date, transforming it to StringBuilder, then string,
                 then date and comparing it to datePicker, then getting the difference in days*/


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



    private void prepareTeamMembersData(String taskName, String taskOwner, long timeRemaining,  String taskDescription, String flag) {

       // TeamMembers teamMembers = new TeamMembers(name, occupancy, R.drawable.member);
        tm.add(new TeamMembers(taskName, taskOwner, timeRemaining, taskDescription, flag));
        taskAdapter.notifyDataSetChanged();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://should check what this does
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

     }

}
