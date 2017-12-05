package com.thetytoalba.bounroomallocator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static com.thetytoalba.bounroomallocator.Constants.HOST_IP;
import static com.thetytoalba.bounroomallocator.Constants.HOST_PORT;
import static com.thetytoalba.bounroomallocator.Constants.TAG_CONNECTION_TYPE;
import static com.thetytoalba.bounroomallocator.Constants.TAG_CREDENTIAL;
import static com.thetytoalba.bounroomallocator.Constants.TAG_LOGIN_CONNECTION;
import static com.thetytoalba.bounroomallocator.Constants.TAG_PASSWORD;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SUCCESS;
import static com.thetytoalba.bounroomallocator.Constants.TAG_USERNAME;

public class LoginActivity extends AppCompatActivity {

        private class LoginTask extends AsyncTask<Void, Void, Boolean> {
        JSONObject credentialMessage;
        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;

        LoginTask(JSONObject credentialMessage) {
            this.credentialMessage = credentialMessage;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Boolean result = Boolean.FALSE;
            try {
                socket = new Socket(HOST_IP, HOST_PORT);
                out = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                Log.e("LoginActivity", "Failed to establish connection.");
            }
            try {
                out.write(credentialMessage.toString());
                out.newLine();
                out.flush();

                JSONObject reply = new JSONObject(in.readLine());
                if (reply.getBoolean(TAG_SUCCESS)) {
                    result = Boolean.TRUE;
                }
            } catch (Exception e) {
                Log.e("LoginActivity", "Error while sending credentials to server.");
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("LoginActivity", "Failed to close socket.");
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("LoginActivity", "Failed to close input stream.");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("LoginActivity", "Failed to close output stream.");
                }
            }

            if (result) {
                successfulLogin();
            } else {
                failedLogin();
            }
        }
    }

    private EditText username;
    private EditText password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);

        username = (EditText) findViewById(R.id.usernameEditText);
        password = (EditText) findViewById(R.id.passwordEditText);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInWithCredentials(username.getText().toString(),
                        password.getText().toString());
            }
        });
        Button signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    public void successfulLogin() {
        hideProgress();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void failedLogin() {
        hideProgress();
        username.setText("");
        password.setText("");
        Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.wrongCredentials),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Starts asynctask to send data to server.
     * @param username username string
     * @param password password string
     */
    private void logInWithCredentials(String username, String password) {
        Log.i("LoginActivity", "Trying to log in.");
        JSONObject loginObj = new JSONObject();
        try {
            loginObj.put(TAG_CONNECTION_TYPE, TAG_LOGIN_CONNECTION);
            JSONObject credentialObj = new JSONObject();
            credentialObj.put(TAG_USERNAME, username);
            credentialObj.put(TAG_PASSWORD, password);
            loginObj.put(TAG_CREDENTIAL, credentialObj);
        } catch (JSONException e) {
            Log.e("LoginActivity", "Error while creating credential JSON.");
            return;
        }
        showProgress();
        new LoginTask(loginObj).execute();
    }


    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}