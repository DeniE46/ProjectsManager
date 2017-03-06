package com.adk.projectsmanager;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;



public class TasksFragment extends Fragment implements View.OnClickListener{


    private List<TasksModel> tm = new ArrayList<>();
    private TaskAdapter taskAdapter;
    CardView cardView;
    TextView createTaskName, createTaskOwner,createTaskDeadline, createTaskDescription;
    Button createTaskSubmit;
    View parentLayout;
    public Firebase mRef;
    TabLayout tabLayout;
    DataTabs dataTabs;
    Firebase ref;
    ChildEventListener mChildEventListener;
    View view;
    RecyclerView recyclerView;
    TasksModel tasksModel;
    String uniqueNodeName;
    int taskItemPosition;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //checks if view is null so that if it is not the view will not be inflated again and the firebase data will not be duplicated
        if (view == null) {
            view = inflater.inflate(R.layout.activity_tasks, container, false);
            Firebase.setAndroidContext(getActivity());
            tabLayout = (TabLayout) view.findViewById(R.id.tabs);
            dataTabs = new DataTabs();
            ref = new Firebase(TaskURL.tasksURL);
            tasksModel = new TasksModel();

            cardView = (CardView)view.findViewById(R.id.card_test);
            parentLayout = view.findViewById(R.id.root_view);
            //checking if the view is null, if not hide it
            if (cardView != null) {
                cardView.setVisibility(View.GONE);
            }



            createTaskName = (TextView)view.findViewById(R.id.create_task_name);
            createTaskOwner = (TextView)view.findViewById(R.id.create_task_owner);
            createTaskDeadline = (TextView)view.findViewById(R.id.create_task_deadline);
            createTaskDescription = (TextView)view.findViewById(R.id.create_task_description);
            createTaskSubmit = (Button)view.findViewById(R.id.create_task_submit);
            createTaskDeadline.setOnClickListener(this);
            createTaskSubmit.setOnClickListener(this);

            //creates an object to return true if touch is detected
            final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
            //./creates an object to return true if touch is detected



            syncTasks();
            Log.d("app", "calling sync tasks");


            //registering widgets, adapters, data structures and layouts
            recyclerView = (RecyclerView) view.findViewById(R.id.rv);
            taskAdapter = new TaskAdapter(tm);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            if(recyclerView != null){
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(taskAdapter);
                //intercepts onclick and gets position
                //pos is int containing the position of the card view that is being clicked

                recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                        View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
                        if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
                            int pos = recyclerView.getChildAdapterPosition(child);
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

            setHasOptionsMenu(true);

            Firebase.setAndroidContext(getActivity());
            mRef = new Firebase(FirebaseConfig.FIREBASE_URL);
        }



        return view;
    }

    public void taskOptions(final int position){
        //TODO: some clean up in this method
        final Firebase ref = new Firebase(TaskURL.tasksURL);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                List<String> IDList = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //iterates through all of the Task node children and saves their names in a list called IDList
                    IDList.add(child.getKey());
                    //used to be child.toString().substring(21,41)
                }
                final String uniqueNode = IDList.get(position);
                taskItemPosition = position;
                uniqueNodeName = uniqueNode;
                //start of dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                        Toast.makeText(getActivity(), "Task deleted", Toast.LENGTH_SHORT).show();
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
                                       ####Can be improved by putting the code that restarts the activity in OnDataChange() method
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

                                        taskAdapter.notifyItemRemoved(position);
                                        tm.remove(position);
                                        dialog.dismiss();
                                        break;
                                    //case 1: changes/modifies specific information in certain node specified by the position that is passed
                                    //can be used as a base to add modify task/member
                                    //basic modification should be: add option and modify the existing dialog to show modify option, which then calls
                                    //new dialog that loads information thanks to the position of the card, when this info is modified the information is saved just like flag updating
                                    case 1:
                                        Toast.makeText(getActivity(), "Task completed", Toast.LENGTH_SHORT).show();
                                        ref.child(uniqueNode).child("Tasks").child("flag").setValue("true");
                                        refreshTask();
                                        dialog.dismiss();

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



    //syncing with Firebase
    public final void syncTasks(){
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //fetching data from Firebase for task name, task owner, task status and task description
                String taskName = (String) dataSnapshot.child("Tasks").child("task name").getValue();
                String taskOwner = (String) dataSnapshot.child("Tasks").child("task owner").getValue();
                String taskDateCreated = (String) dataSnapshot.child("Tasks").child("task status").getValue();
                String taskDescription = (String) dataSnapshot.child("Tasks").child("task description").getValue();
                String completionFlag = (String)dataSnapshot.child("Tasks").child("flag").getValue();
                Boolean taskIsCompleted = Boolean.valueOf(completionFlag);
                //./fetching data from Firebase for task name, task owner, task status, task description and flag
                long timeRemaining = calculateDeadline(taskDateCreated);
                Log.d("app", "adding elements");
                prepareTeamMembersData(taskName, taskOwner, timeRemaining, taskDescription, taskIsCompleted);
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
        };
        ref.addChildEventListener(mChildEventListener);

    }


    public void prepareTeamMembersData(String taskName, String taskOwner, long timeRemaining,  String taskDescription, Boolean taskIsCompleted) {
        // TeamMembers teamMembers = new TeamMembers(name, occupancy, R.drawable.member);
        tm.add(new TasksModel(taskName, taskOwner, timeRemaining, taskDescription, taskIsCompleted));
        taskAdapter.notifyDataSetChanged();

    }

    public void updateTeamMembersData(int position, String taskName, String taskOwner, long timeRemaining,  String taskDescription, Boolean taskIsCompleted) {
        tm.set(position, new TasksModel(taskName, taskOwner, timeRemaining, taskDescription, taskIsCompleted));
        taskAdapter.notifyItemChanged(position);
    }

    public void showDatePickerDialog(){
        DialogFragment dialogFragment = new DialogFragmentPicker();
        dialogFragment.show(getActivity().getFragmentManager(), "datePicker");
    }



    //add conditions for EditTexts and datePicker according to which the save button will be activated/disabled
    private  void checkFieldsForEmptyValues(){
        String TaskName = createTaskName.getText().toString();
        String TaskOwner = createTaskOwner.getText().toString();
        String deadline = createTaskDeadline.getText().toString();
        String TaskDescription = createTaskDescription.getText().toString();


        if(TaskName.equals("") | TaskOwner.equals("") | deadline.equals("") | TaskDescription.equals(""))
        {
            //createTaskSubmit.setEnabled(false);
            //add icon to setError
            if(TaskName.equals("")&& TaskOwner.equals("") && deadline.equals("") && TaskDescription.equals("")) {
                createTaskName.setError("You need to enter a name for the task");
                createTaskOwner.setError("Owner is not selected");
                createTaskDeadline.setError("deadline is not selected");
                createTaskDescription.setError("you need to add description");
            }
            else if(TaskName.equals("")){
                createTaskName.setError("You need to enter a name for the task");
            }

            if(TaskOwner.equals(""))
            {
                createTaskOwner.setError("Owner is not selected");
            }
            if(TaskDescription.equals(""))
            {
                createTaskDescription.setError("you need to add description");
            }
            if(deadline.equals(""))
            {
                createTaskDeadline.setError("deadline is not selected");
            }
        }


        else
        {
            Toast.makeText(getActivity(), "Task uploaded", Toast.LENGTH_SHORT).show();
            new TaskUploader().execute(TaskName,TaskOwner, deadline, TaskDescription);
            cardView.setVisibility(View.GONE);
            clearTaskFields();
        }


    }

    public void clearTaskFields(){
        createTaskName.setText("");
        createTaskOwner.setText("");
        createTaskDeadline.setText("");
        createTaskDescription.setText("");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case(R.id.create_task_deadline):
                showDatePickerDialog();
                break;
            case(R.id.create_task_submit):
                checkFieldsForEmptyValues();

                break;
        }
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_add_task:
                if(cardView.getVisibility() == View.VISIBLE){
                    cardView.setVisibility(View.GONE);
                }
                else{
                    cardView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.action_logout:
                returnToMainActivity();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    public void refreshTask(){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String taskName = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task name").getValue();
                String taskOwner = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task owner").getValue();
                String taskDateCreated = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task status").getValue();
                String taskDescription = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task description").getValue();
                String completionFlag = (String)dataSnapshot.child(uniqueNodeName).child("Tasks").child("flag").getValue();
                Boolean taskIsCompleted = Boolean.valueOf(completionFlag);
                long timeRemaining = calculateDeadline(taskDateCreated);
                updateTeamMembersData(taskItemPosition, taskName, taskOwner, timeRemaining, taskDescription, taskIsCompleted);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


    public void returnToMainActivity(){
        mRef.unauth();
        Intent intent = new Intent (getActivity(), MainActivity.class);
        startActivity(intent);
    }

    long calculateDeadline(String taskDateCreated){
           /*choosing format, getting current date, transforming it to StringBuilder, then string,
                 then date and comparing it to datePicker, then getting the difference in days*/
        Calendar calendar;
        int year, month, day;
        long diff = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy", Locale.ENGLISH);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDateString = day + " " + (month+1) + " " + year;
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
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}
