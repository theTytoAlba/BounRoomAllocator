package com.thetytoalba.bounroomallocator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText username = (EditText) findViewById(R.id.usernameEditText);
        final EditText password = (EditText) findViewById(R.id.passwordEditText);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (logInWithCredentials(username.getText().toString(),
                        password.getText().toString())) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    username.getText().clear();
                    password.getText().clear();
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.wrongCredentials),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Returns true if login is successful, false otherwise.
     * @param username username string
     * @param password password string
     * @return success
     */
    private boolean logInWithCredentials(String username, String password) {
        //TODO(issue 1): Implement a real login check.
        return (username.equals("admin") && password.equals("pass"));
    }
}
