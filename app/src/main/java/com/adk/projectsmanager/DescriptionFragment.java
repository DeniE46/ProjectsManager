package com.adk.projectsmanager;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;



public class DescriptionFragment extends Fragment {

    TextView projectName, projectDescription;
    private String descUrl;
    private String mUserId;
    public Firebase mRef;
    String nameProject;
    String descriptionProject;
    StatisticsChartFragment statisticsChartFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.description_fragment_layout, container, false);
        projectName = (TextView)view.findViewById(R.id.view_project_name);
        projectDescription = (TextView)view.findViewById(R.id.view_project_description);
        statisticsChartFragment = new StatisticsChartFragment();
        setHasOptionsMenu(true);

        Firebase.setAndroidContext(getActivity());
        mRef = new Firebase(FirebaseConfig.FIREBASE_URL);
        try {
            mUserId = mRef.getAuth().getUid();
        } catch (Exception e) {

            returnToMainActivity();
        }

        descUrl = FirebaseConfig.FIREBASE_URL  + "/users/" + mUserId + "/Description";
        syncDescription();
        displayStatisticsChartFragment();
        return view;
    }


    private void syncDescription(){
        projectDescription.setMovementMethod(new ScrollingMovementMethod());
        //syncing with firebase
        Firebase ref = new Firebase(descUrl);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //fetches data for description
                nameProject = (String) dataSnapshot.child("Description").child("Project name").getValue();
                descriptionProject = (String) dataSnapshot.child("Description").child("Project description").getValue();

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




    public final void dialogCreateDescription(){
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.create_description, null);
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity());
        alertdialog.setView(v);
        final EditText editProjectName = (EditText)v.findViewById(R.id.edit_project_name);
        final EditText editProjectDescription = (EditText)v.findViewById(R.id.edit_project_description);

        editProjectName.setHint(R.string.hint_project_name);
        editProjectDescription.setHint(R.string.hint_project_description);
        editProjectName.setText(nameProject);
        editProjectDescription.setText(descriptionProject);
        alertdialog.setCancelable(true);
        alertdialog.setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Creating firebase object
                String name =  editProjectName.getText().toString();
                String description = editProjectDescription.getText().toString();
                uploadDescription (name, description);
                //Getting values to store
                Toast.makeText(getActivity(), R.string.toast_description_saved, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.description_fragment_menu_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_add_description:
                dialogCreateDescription();
                break;
            case R.id.action_logout:
                returnToMainActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void returnToMainActivity(){
        mRef.unauth();
        Intent intent = new Intent (getActivity(), MainActivity.class);
        startActivity(intent);
    }

    void displayStatisticsChartFragment(){
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.chart_scene, statisticsChartFragment);
        fragmentTransaction.commit();
    }

}
