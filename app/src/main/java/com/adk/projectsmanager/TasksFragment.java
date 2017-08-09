package com.adk.projectsmanager;


import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.danimahardhika.cafebar.CafeBar;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.robertlevonyan.views.chip.Chip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;



public class TasksFragment extends Fragment implements View.OnClickListener {



    private TaskAdapter taskAdapter;
    CardView uploadTaskCard;
    EditText createTaskName, createTaskDescription, createTaskPeopleWorking;
    Spinner createTaskOwner, createTaskPriority, createTaskDifficulty;
    Button createTaskSubmit, createTaskDeadline;
    View parentLayout;
    public Firebase mRef;
    TabLayout tabLayout;
    ChildEventListener mChildEventListener;
    View fragmentView;

    TasksModel tasksModel;
    String uniqueNodeName;
    public int taskItemPosition;
    DataTabs dataTabs;
    StatisticsChartFragment statistics;
    static String taskFilter = "All";
    CardView filterCard;
    Chip allChip, wipChip, completedChip, deadlineChip, priorityChip, pauseChip;
    RecyclerView recyclerView;
    ArrayList<TasksModel> localTasksList;
    private static final String LOCAL_TAG = ".TasksFragment";
    final float CHIP_ALPHA_PRESSED = 0.7f;
    final float CHIP_ALPHA = 1f;
    long timeRemaining;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //checks if view is null so that if it is not the view will not be inflated again and the firebase data will not be duplicated
        //TODO: leave only firebase and data-related code in this if statement, see what needs to stay
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.activity_tasks, container, false);


            tabLayout = (TabLayout) fragmentView.findViewById(R.id.tabs);
            dataTabs = new DataTabs();



            if(savedInstanceState != null){
                localTasksList = savedInstanceState.getParcelableArrayList("STATE_TASK_MODEL_LIST");
            }
            else{
                localTasksList = new ArrayList<>();

                syncTasks("All");
            }

            taskAdapter = new TaskAdapter(localTasksList, this);
            tasksModel = new TasksModel();
            uploadTaskCard = (CardView) fragmentView.findViewById(R.id.upload_task_card);
            filterCard = (CardView)fragmentView.findViewById(R.id.filter_card);
            parentLayout = fragmentView.findViewById(R.id.root_view);


            statistics = new StatisticsChartFragment();

            createTaskName = (EditText) fragmentView.findViewById(R.id.create_task_name);
            createTaskOwner = (Spinner) fragmentView.findViewById(R.id.set_task_owner);
            createTaskDeadline = (Button) fragmentView.findViewById(R.id.create_task_deadline);
            createTaskDescription = (EditText) fragmentView.findViewById(R.id.create_task_description);
            createTaskDifficulty = (Spinner)fragmentView.findViewById(R.id.create_task_difficulty);
            createTaskPriority = (Spinner)fragmentView.findViewById(R.id.create_task_priority);
            createTaskPeopleWorking = (EditText)fragmentView.findViewById(R.id.create_task_people_working);

            createTaskSubmit = (Button) fragmentView.findViewById(R.id.create_task_submit);

            createTaskDeadline.setOnClickListener(this);
            createTaskDeadline.setText(currentDate());
            createTaskSubmit.setOnClickListener(this);



            Log.d("app", "calling sync tasks");

            //registering widgets, adapters, data structures and layouts
            recyclerView = (RecyclerView) fragmentView.findViewById(R.id.rv);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            if (recyclerView != null) {
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(taskAdapter);
            }


            setHasOptionsMenu(true);
            mRef = new Firebase(FirebaseConfig.FIREBASE_URL);
        }


        allChip = (Chip)fragmentView.findViewById(R.id.all_chip);
        wipChip = (Chip)fragmentView.findViewById(R.id.wip_chip);
        completedChip = (Chip)fragmentView.findViewById(R.id.complete_chip);
        deadlineChip = (Chip)fragmentView.findViewById(R.id.deadline_chip);
        priorityChip = (Chip)fragmentView.findViewById(R.id.priority_chip);
        pauseChip = (Chip) fragmentView.findViewById(R.id.pause_chip);
        allChip.setOnClickListener(this);
        wipChip.setOnClickListener(this);
        completedChip.setOnClickListener(this);
        deadlineChip.setOnClickListener(this);
        priorityChip.setOnClickListener(this);
        pauseChip.setOnClickListener(this);

        MembersFragment membersFragment = new MembersFragment();

        ArrayAdapter<TasksModel> spinnerOwnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, localTasksList);
        spinnerOwnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        createTaskOwner.setAdapter(spinnerOwnerAdapter);
        ArrayAdapter<CharSequence> spinnerPriorityAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.task_spinner_priority, android.R.layout.simple_spinner_item);
        spinnerPriorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        createTaskPriority.setAdapter(spinnerPriorityAdapter);
        ArrayAdapter<CharSequence> spinnerDifficultyAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.task_spinner_difficulty, android.R.layout.simple_spinner_item);
        spinnerDifficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        createTaskDifficulty.setAdapter(spinnerDifficultyAdapter);
        return fragmentView;
    }







    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO: save taskUploadCard as well as filterCard
        outState.putParcelableArrayList("STATE_TASK_MODEL_LIST", localTasksList);
    }

    void chipsState(String chip){
        switch(chip){
            case "All":
                allChip.setAlpha(CHIP_ALPHA_PRESSED);
                wipChip.setAlpha(CHIP_ALPHA);
                completedChip.setAlpha(CHIP_ALPHA);
                deadlineChip.setAlpha(CHIP_ALPHA);
                priorityChip.setAlpha(CHIP_ALPHA);
                pauseChip.setAlpha(CHIP_ALPHA);
                break;
            case "WIP":
                allChip.setAlpha(CHIP_ALPHA);
                wipChip.setAlpha(CHIP_ALPHA_PRESSED);
                completedChip.setAlpha(CHIP_ALPHA);
                deadlineChip.setAlpha(CHIP_ALPHA);
                priorityChip.setAlpha(CHIP_ALPHA);
                pauseChip.setAlpha(CHIP_ALPHA);
                break;
            case "Completed":
                allChip.setAlpha(CHIP_ALPHA);
                wipChip.setAlpha(CHIP_ALPHA);
                completedChip.setAlpha(CHIP_ALPHA_PRESSED);
                deadlineChip.setAlpha(CHIP_ALPHA);
                priorityChip.setAlpha(CHIP_ALPHA);
                pauseChip.setAlpha(CHIP_ALPHA);
                break;
            case "Deadline":
                allChip.setAlpha(CHIP_ALPHA);
                wipChip.setAlpha(CHIP_ALPHA);
                completedChip.setAlpha(CHIP_ALPHA);
                deadlineChip.setAlpha(CHIP_ALPHA_PRESSED);
                priorityChip.setAlpha(CHIP_ALPHA);
                pauseChip.setAlpha(CHIP_ALPHA);
                break;
            case "Priority":
                allChip.setAlpha(CHIP_ALPHA);
                wipChip.setAlpha(CHIP_ALPHA);
                completedChip.setAlpha(CHIP_ALPHA);
                deadlineChip.setAlpha(CHIP_ALPHA);
                priorityChip.setAlpha(CHIP_ALPHA_PRESSED);
                pauseChip.setAlpha(CHIP_ALPHA);
                break;
            case "Paused":
                allChip.setAlpha(CHIP_ALPHA);
                wipChip.setAlpha(CHIP_ALPHA);
                completedChip.setAlpha(CHIP_ALPHA);
                deadlineChip.setAlpha(CHIP_ALPHA);
                priorityChip.setAlpha(CHIP_ALPHA);
                pauseChip.setAlpha(CHIP_ALPHA_PRESSED);
                break;

        }
    }

    public void taskOptions(final int position, final int which) {
        //TODO: some clean up in this method
        final Firebase firebaseRef = new Firebase(TaskURL.tasksURL);
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                List<String> IDList = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //iterates through all of the Task node children and saves their names in a list called IDList
                    IDList.add(child.getKey());
                    //used to be child.toString().substring(21,41)
                }
                uniqueNodeName = IDList.get(position);
                taskItemPosition = position;
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    //case 0: deletes info in certain node specified by the position that is passed
                                    case 0:
                                        //values should be checked, if  null is returned the toast is displayed
                                        //Toast.makeText(getActivity(), "Task deleted", Toast.LENGTH_SHORT).show();
                                        // deletes task and its parent name from list
                                        //deleting certain node can be done like this as well:
                                        firebaseRef.child(uniqueNodeName).child("Tasks").removeValue();
                                        //uniqueNode needs better name, e.g. TimeStampNodeName, should check Firebase name
                                        //ref.child(uniqueNodeName).child("Tasks").child("task description").removeValue();
                                        //ref.child(uniqueNodeName).child("Tasks").child("task name").removeValue();
                                        //ref.child(uniqueNodeName).child("Tasks").child("task owner").removeValue();
                                        //ref.child(uniqueNodeName).child("Tasks").child("task status").removeValue();
                                        //ref.child(uniqueNodeName).child("Tasks").child("flag").removeValue();


                                        Log.d("TasksFragment","removing "+position);
                                        localTasksList.remove(position);
                                        taskAdapter.notifyItemRemoved(position);

                                        break;
                                    //case 1: changes/modifies specific information in certain node specified by the position that is passed
                                    //can be used as a base to add modify task/member
                                    //basic modification should be: add option and modify the existing dialog to show modify option, which then calls
                                    //new dialog that loads information thanks to the position of the card, when this info is modified the information is saved just like flag updating
                                    case 1:
                                        //Toast.makeText(getActivity(), "Task completed", Toast.LENGTH_SHORT).show();
                                        firebaseRef.child(uniqueNodeName).child("Tasks").child("flag").setValue("Completed");
                                        refreshTask(firebaseRef);
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
                                    case 2:
                                        firebaseRef.child(uniqueNodeName).child("Tasks").child("flag").setValue("Paused");
                                        refreshTask(firebaseRef);
                                        break;
                                    case 3:
                                        //TODO: make sure that all data uploaded to Firebase is then synced by syncTasks()
                                        String taskName = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task name").getValue();
                                        //String taskOwner = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task owner").getValue();
                                        String taskDeadlineDate = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task status").getValue();
                                        String taskDescription = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task description").getValue();
                                        String taskDifficulty = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task difficulty").getValue();
                                        String taskPriority = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task priority").getValue();
                                        String taskPeopleWorking = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task people working").getValue();
                                        long taskOwnerIndex = (long) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task owner index").getValue();

                                        populateTaskForm(taskName, taskDeadlineDate, taskDescription, taskDifficulty, taskPriority, taskPeopleWorking, taskOwnerIndex);
                                        break;
                                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    void populateTaskForm(String taskName, String taskDeadlineDate, String taskDescription, String taskDifficulty, String taskPriority, String taskPeopleWorking, long taskOwnerIndex){
        int priority;
        int difficulty;
        switch(taskDifficulty){
            case "Low":
                difficulty = 0;
                break;
            case "Medium":
                difficulty = 1;
                break;
            case "High":
                difficulty = 2;
                break;
            default:
                difficulty = 0;
                break;
        }

        switch(taskPriority){
            case "Low":
                priority = 0;
                break;
            case "Medium":
                priority = 1;
                break;
            case "High":
                priority = 2;
                break;
            default:
                priority = 0;
                break;
        }

        createTaskName.setText(taskName);
        createTaskOwner.setSelection((int) taskOwnerIndex);
        createTaskDeadline.setText(taskDeadlineDate);
        createTaskDescription.setText(taskDescription);
        createTaskDifficulty.setSelection(difficulty);
        createTaskPriority.setSelection(priority);
        createTaskPeopleWorking.setText(taskPeopleWorking);
    }

    //syncing data from Firebase
    public final void syncTasks(final String filter) {
        if(!localTasksList.isEmpty()){
            localTasksList.clear();
        }
        taskFilter = filter;
        Firebase ref = new Firebase(TaskURL.tasksURL);
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //fetching data from Firebase for task name, task owner, task status and task description
                String taskName = (String) dataSnapshot.child("Tasks").child("task name").getValue();
                String taskOwner = (String) dataSnapshot.child("Tasks").child("task owner").getValue();
                String taskDeadlineDate = (String) dataSnapshot.child("Tasks").child("task status").getValue();
                String taskDescription = (String) dataSnapshot.child("Tasks").child("task description").getValue();
                String taskStatus = (String) dataSnapshot.child("Tasks").child("flag").getValue();
                String taskPriority = (String) dataSnapshot.child("Tasks").child("task priority").getValue();
                String taskPeopleWorking = (String)dataSnapshot.child("Tasks").child("people working").getValue();
                String taskDifficulty = (String) dataSnapshot.child("Tasks").child("task difficulty").getValue();

                Log.d(LOCAL_TAG, "syncing objects from Firebase");
                //./fetching data from Firebase for task name, task owner, task status, task description and flag
                timeRemaining = calculateTimeLeft(taskDeadlineDate);
                Log.i(LOCAL_TAG, "calculated time left" + taskDeadlineDate);

                //filtering logic
                if(filter.equals("All")){
                    chipsState("All");
                    localTasksList.add(new TasksModel(taskName, taskOwner, timeRemaining, taskDescription, taskStatus, taskDeadlineDate, taskPriority, taskPeopleWorking, taskDifficulty));
                }
                else if(filter.equals("Paused")){
                    if(taskStatus.equals(filter)){
                        chipsState("Paused");
                        localTasksList.add(new TasksModel(taskName, taskOwner, timeRemaining, taskDescription, taskStatus, taskDeadlineDate, taskPriority, taskPeopleWorking, taskDifficulty));
                    }
                }
                else if(filter.equals("Priority")){
                    if(taskPriority.equals("High")){
                        //TODO: sort the tasks by priority (highest to lowest)
                        chipsState("Priority");
                        localTasksList.add(new TasksModel(taskName, taskOwner, timeRemaining, taskDescription, taskStatus, taskDeadlineDate, taskPriority, taskPeopleWorking, taskDifficulty));
                    }

                }
                else if(filter.equals("Deadline")){
                    //TODO: the number should be chosen by the user
                    if(timeRemaining <= 10){
                        chipsState("Deadline");
                        localTasksList.add(new TasksModel(taskName, taskOwner, timeRemaining, taskDescription, taskStatus, taskDeadlineDate, taskPriority, taskPeopleWorking, taskDifficulty));
                    }

                }
                else{

                    if(taskStatus.equals(filter)){
                        chipsState(filter);
                        localTasksList.add(new TasksModel(taskName, taskOwner, timeRemaining, taskDescription, taskStatus, taskDeadlineDate, taskPriority, taskPeopleWorking, taskDifficulty));
                    }
                }

                CafeBar.builder(getActivity())
                        .content("Filter for Tasks list set:")
                        .neutralText(filter)
                        .neutralColor(R.color.teal)
                        .floating(true)
                        .icon(R.drawable.filter_tasks, false)
                        .show();

                Log.i(LOCAL_TAG,"adding objects to localTasksList " + localTasksList.size());
                taskAdapter.notifyDataSetChanged();


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



void statisticsChart(String taskStatus, int timeRemaining, String taskPriority){
    switch(taskStatus){
        case "WIP":
            break;
        case "Completed":
            break;
        case "Paused":
            break;
    }
    if(timeRemaining<10){

    }
    if(taskPriority.equals("High")){

    }
}


    public void showDatePickerDialog() {
        DialogFragment dialogFragment = new DialogFragmentPicker();
        dialogFragment.show(getActivity().getFragmentManager(), "datePicker");
    }


    //add conditions for EditTexts and datePicker according to which the save button will be activated/disabled
    private void uploadTask( ) {
        String taskName = createTaskName.getText().toString();
        String taskOwner = createTaskOwner.getSelectedItem().toString();
        int ownerIndex = createTaskOwner.getSelectedItemPosition();
        String taskDeadline = createTaskDeadline.getText().toString();
        String taskDescription = createTaskDescription.getText().toString();
        String taskDifficulty = createTaskDifficulty.getSelectedItem().toString();
        String taskPriority = createTaskPriority.getSelectedItem().toString();
        String taskPeopleWorking = createTaskPeopleWorking.getText().toString();

        if (taskName.equals("") | taskOwner.equals("") | taskDeadline.equals("") | taskDescription.equals("") | taskDifficulty.equals("") | taskPriority.equals("") | taskPeopleWorking.equals("")) {
            //createTaskSubmit.setEnabled(false);
            //add icon to setError
            if (taskName.equals("") && taskOwner.equals("") && taskDeadline.equals("") && taskDescription.equals("") && taskDifficulty.equals("") && taskPriority.equals("") && taskPeopleWorking.equals("")) {
                createTaskName.setError("You need to enter a name for the task");
                createTaskDeadline.setError("deadline is not selected");
                createTaskDescription.setError("you need to add description");
            } else if (taskName.equals("")) {
                createTaskName.setError("You need to enter a name for the task");
            }
            if (taskDescription.equals("")) {
                createTaskDescription.setError("you need to add description");
            }
            if (taskDeadline.equals("")) {
                createTaskDeadline.setError("deadline is not selected");
            }
            if (taskPeopleWorking.equals("")) {
                createTaskPeopleWorking.setError("people are not selected");
            }
        } else {
            Toast.makeText(getActivity(), "Task uploaded", Toast.LENGTH_SHORT).show();
            new TaskUploader().execute(taskName, taskOwner, taskDeadline, taskDescription, taskDifficulty, taskPriority, taskPeopleWorking, Integer.toString(ownerIndex));
            uploadTaskCard.setVisibility(View.GONE);
            clearTaskFields();
        }


    }

    void updateTask(){
        final String taskName = createTaskName.getText().toString();
        final String taskOwner = createTaskOwner.getSelectedItem().toString();
        final int taskOwnerIndex = createTaskOwner.getSelectedItemPosition();
        final String taskDeadline = createTaskDeadline.getText().toString();
        final String taskDescription = createTaskDescription.getText().toString();
        final String taskDifficulty = createTaskDifficulty.getSelectedItem().toString();
        final String taskPriority = createTaskPriority.getSelectedItem().toString();
        final String taskPeopleWorking = createTaskPeopleWorking.getText().toString();
        final Firebase firebaseRef = new Firebase(TaskURL.tasksURL);
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseRef.child(uniqueNodeName).child("Tasks").child("task name").setValue(taskName);
                firebaseRef.child(uniqueNodeName).child("Tasks").child("task owner").setValue(taskOwner);
                firebaseRef.child(uniqueNodeName).child("Tasks").child("task status").setValue(taskDeadline);
                firebaseRef.child(uniqueNodeName).child("Tasks").child("task description").setValue(taskDescription);
                firebaseRef.child(uniqueNodeName).child("Tasks").child("task difficulty").setValue(taskDifficulty);
                firebaseRef.child(uniqueNodeName).child("Tasks").child("task priority").setValue(taskPriority);
                firebaseRef.child(uniqueNodeName).child("Tasks").child("task people working").setValue(taskPeopleWorking);
                firebaseRef.child(uniqueNodeName).child("Tasks").child("task owner index").setValue(taskOwnerIndex);
                refreshTask(firebaseRef);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        uploadTaskCard.setVisibility(View.GONE);
    }

    public void clearTaskFields() {
        createTaskName.setText("");
        createTaskOwner.setSelection(0);
        createTaskDeadline.setText(currentDate());
        createTaskDescription.setText("");
        createTaskDifficulty.setSelection(0);
        createTaskPriority.setSelection(0);
        createTaskPeopleWorking.setText("");
    }
    public void clearFieldsErrors(){
        createTaskName.setError(null);
        createTaskDeadline.setError(null);
        createTaskDescription.setError(null);
        createTaskPeopleWorking.setError(null);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.create_task_deadline):
                showDatePickerDialog();
                break;
            case (R.id.create_task_submit):
                if(createTaskSubmit.getText().equals("Submit")){
                    uploadTask();
                    Toast.makeText(getActivity(), "Uploading...", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Modifying...", Toast.LENGTH_SHORT).show();
                    updateTask();
                }
                break;
            case (R.id.all_chip):
                syncTasks("All");
                break;
            case (R.id.wip_chip):
                syncTasks("WIP");
                break;
            case R.id.complete_chip:
                syncTasks("Completed");
                break;
            case R.id.deadline_chip:
                syncTasks("Deadline");
                break;
            case R.id.priority_chip:
                syncTasks("Priority");
                break;
            case R.id.pause_chip:
                syncTasks("Paused");
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
        switch (item.getItemId()) {
            case R.id.action_add_task:
                //TODO:add animation
               if (uploadTaskCard.getVisibility() == View.VISIBLE) {
                    uploadTaskCard.setVisibility(View.GONE);
                    clearFieldsErrors();
                   clearTaskFields();
                } else {

                       createTaskSubmit.setText("Submit");

                    uploadTaskCard.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.action_logout:
                returnToMainActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void refreshTask(Firebase ref) {
        //TODO: Sync all field (like syncTasks())
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String taskName = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task name").getValue();
                String taskOwner = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task owner").getValue();
                String taskDeadlineDate = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task status").getValue();
                String taskDescription = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task description").getValue();
                String taskStatus = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("flag").getValue();
                String taskDifficulty = (String)dataSnapshot.child(uniqueNodeName).child("Tasks").child("task difficulty").getValue();
                String taskPriority = (String) dataSnapshot.child(uniqueNodeName).child("Tasks").child("task priority").getValue();
                String taskPeopleWorking = (String)dataSnapshot.child(uniqueNodeName).child("Tasks").child("people working").getValue();
                long daysRemaining = calculateTimeLeft(taskDeadlineDate);
                //updates the itemList in TaskAdapter with the "Completed" status
                localTasksList.set(taskItemPosition, new TasksModel(taskName, taskOwner, daysRemaining, taskDescription, taskStatus, taskDeadlineDate, taskPriority, taskPeopleWorking, taskDifficulty));
                taskAdapter.notifyItemChanged(taskItemPosition);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }



    public void returnToMainActivity() {
        mRef.unauth();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    long calculateTimeLeft(String taskDateCreated) {
           /*choosing format, getting current date, getting pickerDateTaskCreated,
           then converting them to date object,
           subtract and return the difference in days to get timeRemaining*/

        long diff = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String today = currentDate();
        try {
            //today
            Date currentDate = formatter.parse(today);
            //deadline for the task:
            Date pickerDateTaskCreated = formatter.parse(taskDateCreated);
            diff = pickerDateTaskCreated.getTime() - currentDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //converting the difference into int
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    String currentDate(){
        Calendar calendar;
        int year, month, day;
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        return day + "/" + (month + 1) + "/" + year;
    }


    //inner class
     class PopUpWindow  {

        private final int POPUP_Y_OFFSET = 70;
        private final int POPUP_Y_NEGATIVE_OFFSET = 190; //when the pop up has no space to appear in the card it appears atop


         void showPopup(final int taskPosition, int popupLocation[]) {

            Drawable myIcon = getActivity().getDrawable(R.drawable.popup_bg_negative);
            View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_layout, null);
            final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            Button btnDismiss = (Button) popupView.findViewById(R.id.close);
            Button deleteTaskButton = (Button) popupView.findViewById(R.id.delete_task_button);
            Button completeTaskButton = (Button) popupView.findViewById(R.id.complete_task_button);
            Button pauseTaskButton = (Button) popupView.findViewById(R.id.pause_task_button);
            Button modifyTaskButton = (Button) popupView.findViewById(R.id.modify_task_button);
            Log.d("popup window","popup called");
            btnDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            deleteTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskOptions(taskPosition, 0);
                    popupWindow.dismiss();
                    if(!taskFilter.equals("All")){
                        syncTasks("All");
                        Toast.makeText(getActivity(), "Refreshed to all", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            completeTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskOptions(taskPosition, 1);
                    popupWindow.dismiss();
                    if(!taskFilter.equals("All")){
                       // syncTasks("All");
                        Toast.makeText(getActivity(), "Refreshed to all", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            pauseTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskOptions(taskPosition, 2);
                    popupWindow.dismiss();
                    if(!taskFilter.equals("All")){
                        syncTasks("All");
                        Toast.makeText(getActivity(), "Refreshed to all", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            modifyTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createTaskSubmit.setText("Update");
                    taskOptions(taskPosition, 3);
                    uploadTaskCard.setVisibility(View.VISIBLE);
                    popupWindow.dismiss();
                }
            });

            popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(200);

            //x-width,y-height
            if(popupLocation[1]>1500){
                popupView.setBackground(myIcon);
                popupWindow.showAsDropDown(popupView, 0, popupLocation[1]-POPUP_Y_NEGATIVE_OFFSET);
            }
            else{
                popupWindow.showAsDropDown(popupView, 0, popupLocation[1]+POPUP_Y_OFFSET);
            }
            //TODO: Check this line:
            popupWindow.setElevation(5);

            popupWindow.setFocusable(true);
            popupWindow.update();

        }

    }

}