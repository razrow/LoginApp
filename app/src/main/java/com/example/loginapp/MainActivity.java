package com.example.loginapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button mLogin;
    EditText mUsername;
    EditText mPassword;

    private UserDAO mUserDAO;
    private User mUser;
    private int mUserId = -1;
    private SharedPreferences mPreferences = null;
    private static final String USER_ID_KEY = "com.example.loginapp.userIdKey";
    private static final String PREFERENCES_KEY = "com.example.loginapp.PREFERENCES_KEY";

    private String mPasswordString;
    private String mUsernameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserDAO = Room.databaseBuilder(this, AppDatabase.class,AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getUserDAO();
        checkForUser();

        mUsername = findViewById(R.id.etUsername);
        mPassword = findViewById(R.id.etPassword);

        mLogin = findViewById(R.id.loginButton);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();
                if(checkForUserInDatabase()){
                    if(validatePassword()){
                        Intent intent = new Intent(getApplicationContext(), WelcomeScreen.class);
                        intent.putExtra("Username", mUsernameString);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "Password incorrect.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "User not found.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getValues(){
        mUsernameString = mUsername.getText().toString();
        mPasswordString = mPassword.getText().toString();
    }

    private boolean checkForUserInDatabase(){
        mUser = mUserDAO.getUserByUsername(mUsernameString);
        if(mUser == null){
            Toast.makeText(getApplicationContext(), "User not found.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePassword(){
        return mUser.getPassword().equals(mPasswordString);
    }

    public void getPrefs(){
        mPreferences = this.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    private void checkForUser(){
        //do we have a user in the prefs?
        if(mUserId != -1){
            return;
        }

        if(mPreferences == null) {
            getPrefs();
        }

        mUserId = mPreferences.getInt(USER_ID_KEY, -1);

        if(mUserId != -1){
            return;
        }

        //do we have any users at all?
        List<User> users = mUserDAO.getAllUsers();
        if(users.size() <= 0 ){
            User defaultUser = new User("din_djarin","baby_yoda_ftw");
            mUserDAO.insert(defaultUser);
        }
    }
}