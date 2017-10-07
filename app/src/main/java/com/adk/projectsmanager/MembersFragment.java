package com.adk.projectsmanager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.danimahardhika.cafebar.CafeBar;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MembersFragment extends Fragment implements View.OnClickListener {

    ArrayList<MembersModel> membersList = new ArrayList<>();
    private TeamMembersAdapter teamMembersAdapter;

    public Firebase mRef;
    private String mUserId;
    private String membersUrl;
    private String tasksUrl;
    View parentLayout;
    View view;
    FloatingActionButton createMember;
    ViewPager viewPager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //checks if view is null so that if it is not the view will not be inflated again and the firebase data will not be duplicated
        if(view == null){
            view = inflater.inflate(R.layout.content_main2, container, false);
            //registering widgets, adapters, data structures and layouts
            RecyclerView recyclerView;
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            teamMembersAdapter = new TeamMembersAdapter(membersList);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            //recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(teamMembersAdapter);
            parentLayout = view.findViewById(R.id.root_view);

            //tasksFragment = new TasksFragment();
            viewPager = (ViewPager)getActivity().findViewById(R.id.viewpager);



            Firebase.setAndroidContext(getActivity());
            mRef = new Firebase(FirebaseConfig.FIREBASE_URL);
            //login/reg
            try {
                mUserId = mRef.getAuth().getUid();
            } catch (Exception e) {
                returnToMainActivity();
            }

            membersUrl = FirebaseConfig.FIREBASE_URL  + "/users/" + mUserId + "/Members";

            tasksUrl = FirebaseConfig.FIREBASE_URL  + "/users/" + mUserId + "/Tasks";

            TaskURL tsk = new TaskURL();
            tsk.setTasksURL(tasksUrl);
            tsk.setMembersURL(membersUrl);

            // Toast.makeText(this, mRef.getAuth().getUid(),Toast.LENGTH_SHORT).show();
            //./login/reg
            //creates an object to return true if touch is detected
            final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
            //./creates an object to return true if touch is detected



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

            createMember = (FloatingActionButton)view.findViewById(R.id.create_member_fab);
            createMember.bringToFront();
            createMember.setOnClickListener(this);

            sync();
        }



        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public void sync (){
        syncMembers();

    }


    public void dialogCreateMember(){
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.create_member, null);
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity());
        alertdialog.setView(v);
        final EditText memberName = (EditText)v.findViewById(R.id.member_name);
        final EditText memberPosition = (EditText)v.findViewById(R.id.member_position);
        final EditText memberPhone = (EditText)v.findViewById(R.id.contact_new_member);
        final EditText memberFb = (EditText)v.findViewById(R.id.fb_new_member);

        final Button dismissUser = (Button)v.findViewById(R.id.btn_dismiss_create_new_user);
        final Button createUser = (Button)v.findViewById(R.id.btn_create_new_user);


        alertdialog.setCancelable(true);
        final Dialog dialog  = alertdialog.create();

        dismissUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = memberName.getText().toString();
                String position = memberPosition.getText().toString();
                String phone = memberPhone.getText().toString();
                String fbSocial = memberFb.getText().toString();

                if(name.equals("") | position.equals("")){
                    if(name.equals("")){
                        memberName.setError("Please enter add a name");
                    }
                    if(position.equals("")){
                        memberPosition.setError("Please select position");
                    }
                }
                else{
                    uploadMember(name, position, phone, fbSocial);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }


    private void CafeBarMessageMemberAdded(){
        CafeBar.builder(getActivity())
                .content("Member")
                .neutralText("CREATED")
                .neutralColor(R.color.teal)
                .floating(true)
                .icon(R.drawable.ic_member, false)
                .show();
    }

    private void CafeBarMessageMemberDeleted(){
        CafeBar.builder(getActivity())
                .content("Member")
                .neutralText("DELETED")
                .neutralColor(R.color.teal)
                .floating(true)
                .icon(R.drawable.ic_member, false)
                .show();
    }

    public final void uploadMember(String name, String position, String phone, String fbSocial){
        //saving name and occupation in map
        Map<String, Object> post1 = new HashMap<>();
        post1.put("name", name);
        post1.put("position", position);
        post1.put("phone", phone);
        post1.put("facebook", fbSocial);
        //Storing values to firebase
        //itemsUrl creates link there to upload
        new Firebase(membersUrl)
                .push()
                .child("Person")
                .setValue(post1);
        CafeBarMessageMemberAdded();
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
                String phoneNumber = (String) snapshot.child("Person").child("phone").getValue();
                String facebookAccount = (String) snapshot.child("Person").child("facebook").getValue();
                //System.out.println(occupation);
                prepareTeamMembersData(name, occupation, phoneNumber, facebookAccount);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Toast.makeText(Team.this,R.string.toast_member_deleted,Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog);

        builder.setTitle(R.string.dialog_title_delete_member);
        builder.setMessage(R.string.dialog_message_delete_member);

        builder.setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // deletes member and its parent name from list
                delete(pos);
                //restarts activity
                teamMembersAdapter.notifyItemRemoved(pos);
                teamMembersAdapter.membersList.remove(pos);
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
                    IDList.add(child.getKey());
                    //used to be child.toString().substring(21,41)
                }
                String del = IDList.get(position);

                ref.child(del).child("Person").child("name").removeValue();
                ref.child(del).child("Person").child("position").removeValue();
                ref.child(del).child("Person").child("phone").removeValue();
                ref.child(del).child("Person").child("facebook").removeValue();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        CafeBarMessageMemberDeleted();
    }


    //sending data to objects (TeamMembers) which is then accessed in TeamMembersAdapter via set or get methods
    private void prepareTeamMembersData(String name, String occupancy, String phoneNumber, String facebookAccount) {

        MembersModel membersModel = new MembersModel(name, occupancy, R.drawable.ic_member, phoneNumber, facebookAccount);
        membersList.add(membersModel);
        teamMembersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }


    public void returnToMainActivity(){
        mRef.unauth();
        Intent intent = new Intent (getActivity(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.create_member_fab:
                dialogCreateMember();
                break;
        }
    }
}
