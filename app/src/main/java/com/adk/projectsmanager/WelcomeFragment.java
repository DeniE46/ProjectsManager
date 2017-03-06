package com.adk.projectsmanager;

import android.app.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.firebase.client.Firebase;



public class WelcomeFragment extends Fragment implements View.OnClickListener {

    Button LogIn;
    Button Register;
    Firebase mRef;
    RelativeLayout relativeLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    View v = inflater.inflate(R.layout.welcome_fragment_layout, container, false);
        relativeLayout =(RelativeLayout)v.findViewById(R.id.welcome_fragment_layout);
        LogIn = (Button)v.findViewById(R.id.welcome_fragment_login);
        Register = (Button)v.findViewById(R.id.welcome_fragment_register);
        LogIn.setOnClickListener(this);
        Register.setOnClickListener(this);
        mRef = new Firebase(FirebaseConfig.FIREBASE_URL);
        return v;

    }

    @Override
    public void onClick(View v) {
switch(v.getId()){
    case R.id.welcome_fragment_login:
        callRegisterFragment();
        break;
    case R.id.welcome_fragment_register:
        callLogInFragment();
        //the following is a check if there is a user already logged in
       /* mRef = new Firebase(Config.FIREBASE_URL);

        try {
            mUserId = mRef.getAuth().getUid();
        } catch (Exception e) {
            loadLoginView();
            // System.out.println("error");
        }*/
        break;

}


    }



    public void callLogInFragment(){

        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LogInFragment logInFragment = new LogInFragment();
        fragmentTransaction.setCustomAnimations(R.animator.enter_from_top, R.animator.exit_to_top);
        fragmentTransaction.replace(R.id.scene_layout, logInFragment);
        fragmentTransaction.commit();
    }

    public void callRegisterFragment(){
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RegisterFragment registerFragment = new RegisterFragment();
        fragmentTransaction.setCustomAnimations(R.animator.enter_from_top, R.animator.exit_to_top);
        fragmentTransaction.replace(R.id.scene_layout, registerFragment);
        fragmentTransaction.commit();

    }

}
