package com.adk.projectsmanager;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;



public class LogInFragment extends Fragment implements View.OnClickListener {

    EditText emailEditText, passwordEditText;
    Button loginButton, loginRegisterButton, loginDismiss;
    final Firebase ref = new Firebase(FirebaseConfig.FIREBASE_URL);

@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.login_fragment_layout, container, false);
    Firebase.setAndroidContext(getActivity());
    emailEditText = (EditText)v.findViewById(R.id.emailField);
    passwordEditText = (EditText)v.findViewById(R.id.passwordField);
    loginRegisterButton = (Button)v.findViewById(R.id.login_register);
    loginButton = (Button)v.findViewById(R.id.loginButton);
    loginDismiss = (Button)v.findViewById(R.id.login_dismiss);
    loginRegisterButton.setOnClickListener(this);
    loginButton.setOnClickListener(this);
    loginDismiss.setOnClickListener(this);
    return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login_dismiss:
                callWelcomeFragment();
                break;
            case R.id.loginButton:
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                email = email.trim();
                password = password.trim();

                if (email.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.login_error_message);
                    builder.setTitle(R.string.login_error_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    final String emailAddress = email;

                    //Login with an email/password combination
                    ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            // Authenticated successfully with payload authData
                            Map<String, Object> map = new HashMap<>();
                            map.put("email", emailAddress);
                            ref.child("users").child(authData.getUid()).updateChildren(map);

                            Intent intent = new Intent(getActivity(), DataTabs.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            //overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // Authenticated failed with error firebaseError
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(firebaseError.getMessage());
                            builder.setTitle(R.string.login_error_title);
                            builder.setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
                break;
            case R.id.login_register:
                callRegisterFragment();
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

    public void callRegisterFragment(){
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RegisterFragment registerFragment = new RegisterFragment();
        fragmentTransaction.setCustomAnimations(R.animator.enter_from_top, R.animator.exit_to_top);
        fragmentTransaction.replace(R.id.scene_layout, registerFragment);
        fragmentTransaction.commit();
    }

}
