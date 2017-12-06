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
import static com.thetytoalba.bounroomallocator.Constants.TAG_USER_TYPE;

public class LoginActivity extends AppCompatActivity {

        private class LoginTask extends AsyncTask<Void, Void, JSONObject> {
        JSONObject credentialMessage;
        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;

        LoginTask(JSONObject credentialMessage) {
            this.credentialMessage = credentialMessage;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject result = null;
            try {
                result = new JSONObject().put("success", Boolean.FALSE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ;
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

                result = new JSONObject(in.readLine());
            } catch (Exception e) {
                Log.e("LoginActivity", "Error while sending credentials to server.");
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(JSONObject result) {
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

            try {
                if (result.getBoolean("success")) {
                    successfulLogin(result);
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            failedLogin(result);
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

    public void successfulLogin(JSONObject result) {
        hideProgress();
        String userType = "";
        try {
            userType = result.getString(TAG_USER_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (userType.equals("manager")) {
                startActivity(new Intent(LoginActivity.this, ManagerActivity.class).putExtra(TAG_USERNAME, username.getText().toString()));
                finish();
                return;
        } else if(userType.equals("teacher")) {
            startActivity(new Intent(LoginActivity.this, TeacherActivity.class).putExtra(TAG_USERNAME, username.getText().toString()));
            finish();
            return;
        } else if (userType.equals("student")){
            startActivity(new Intent(LoginActivity.this, StudentActivity.class).putExtra(TAG_USERNAME, username.getText().toString()));
            finish();
            return;
        }
        Toast.makeText(getApplicationContext(), "Failed to complete login.", Toast.LENGTH_SHORT).show();
    }

    public void failedLogin(JSONObject result) {
        hideProgress();
        username.setText("");
        password.setText("");
        String err = "Could not login.";
        try {
            err += result.getString("errorReason");
        } catch (Exception e) {
            Log.i("LoginActivity", "Login fail reason not specified.");
        }
        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
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