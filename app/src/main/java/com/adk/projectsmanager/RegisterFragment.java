package com.adk.projectsmanager;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


public class RegisterFragment extends Fragment implements View.OnClickListener {


    protected EditText passwordEditText;
    protected EditText emailEditText;
    protected Button signUpButton, registerDismiss;
    Firebase ref;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.register_fragment_layout, container, false);

        passwordEditText = (EditText)v.findViewById(R.id.passwordField);
        emailEditText = (EditText)v.findViewById(R.id.emailField);
        signUpButton = (Button)v.findViewById(R.id.signupButton);
        registerDismiss = (Button)v.findViewById(R.id.register_dismiss);
        signUpButton.setOnClickListener(this);
        registerDismiss.setOnClickListener(this);
        ref = new Firebase(FirebaseConfig.FIREBASE_URL);
        return v;
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_dismiss:
                callWelcomeFragment();
              break;
            case R.id.signupButton:
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();

                password = password.trim();
                email = email.trim();

                if (password.isEmpty() || email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {

                    // Sign up
                    ref.createUser(email, password, new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(R.string.signup_success)
                                    .setPositiveButton(R.string.login_button_label, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            callLogInFragment();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(firebaseError.getMessage())
                                    .setTitle(R.string.signup_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
               break;


        }
    }

    public void callWelcomeFragment(){
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        WelcomeFragment welcomeFragment = new WelcomeFragment();
        fragmentTransaction.setCustomAnimations(R.animator.enter_from_top, R.animator.exit_to_top);
        fragmentTransaction.replace(R.id.scene_layout, welcomeFragment);
        fragmentTransaction.commit();
    }

    public void callLogInFragment(){

        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LogInFragment logInFragment = new LogInFragment();
        fragmentTransaction.setCustomAnimations(R.animator.enter_from_top, R.animator.exit_to_top);
        fragmentTransaction.replace(R.id.scene_layout, logInFragment);
        fragmentTransaction.commit();
    }

}
