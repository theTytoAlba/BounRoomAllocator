package com.thetytoalba.bounroomallocator;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import static com.thetytoalba.bounroomallocator.Constants.TAG_SUCCESS;

public class SignUpActivity extends AppCompatActivity {

    private class SignUpTask extends AsyncTask<Void, Void, Boolean> {
        JSONObject messageToSend;
        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;

        SignUpTask(JSONObject messageToSend) {
            this.messageToSend = messageToSend;
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
                Log.e("SignUpActivity", "Failed to establish connection.");
            }
            try {
                out.write(messageToSend.toString());
                out.newLine();
                out.flush();

                JSONObject reply = new JSONObject(in.readLine());
                if (reply.getBoolean(TAG_SUCCESS)) {
                    result = Boolean.TRUE;
                }
            } catch (Exception e) {
                Log.e("SignUpActivity", "Error while sending sign up credentials to server.");
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {
            Log.i("SignUpActivity", "SignUpTask post execute");
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("SignUpActivity", "Failed to close socket.");
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("SignUpActivity", "Failed to close input stream.");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("SignUpActivity", "Failed to close output stream.");
                }
            }

            if (result) {
                successfulSignUp();
            } else {
                failedSignUp();
            }
        }
    }

    private String userType = "";
    private boolean isVisible = false;
    private ProgressBar progressBar;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressBar = (ProgressBar) findViewById(R.id.signUp_progressBar);

        initUserTypes();
        initPasswordVisibilityIcon();
        initSubmitButton();
    }

    private void initUserTypes(){
        final TextView student = (TextView) findViewById(R.id.signUp_student);
        final TextView teacher = (TextView) findViewById(R.id.signUp_teacher);
        final TextView manager = (TextView) findViewById(R.id.signUp_manager);

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                student.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                teacher.setBackgroundColor(getResources().getColor(R.color.midGray));
                manager.setBackgroundColor(getResources().getColor(R.color.midGray));

                userType = Constants.TAG_STUDENT;
            }
        });

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                student.setBackgroundColor(getResources().getColor(R.color.midGray));
                teacher.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                manager.setBackgroundColor(getResources().getColor(R.color.midGray));

                userType = Constants.TAG_TEACHER;
            }
        });

        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                student.setBackgroundColor(getResources().getColor(R.color.midGray));
                teacher.setBackgroundColor(getResources().getColor(R.color.midGray));
                manager.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                userType = Constants.TAG_MANAGER;
            }
        });
    }

    private void initPasswordVisibilityIcon() {
        final EditText password = (EditText) findViewById(R.id.signUp_passwordEditText);
        final ImageView visibility = (ImageView) findViewById(R.id.signUp_eyeIcon);
        visibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisible) {
                    visibility.setImageResource(R.drawable.visible);
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    isVisible = false;
                } else {
                    visibility.setImageResource(R.drawable.invisible);
                    password.setTransformationMethod(null);
                    isVisible = true;
                }
            }
        });
    }

    private void initSubmitButton() {
        submit = (Button) findViewById(R.id.signUp_signUpButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "You have to select a user type.", Toast.LENGTH_SHORT).show();
                    return;
                }
                EditText username = (EditText) findViewById(R.id.signUp_usernameEditText);
                if (username.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "You have to enter a username.", Toast.LENGTH_SHORT).show();
                    return;
                }
                EditText password = (EditText) findViewById(R.id.signUp_passwordEditText);
                if (password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "You have to enter a password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject objectToSend = new JSONObject();
                    objectToSend.put(TAG_CONNECTION_TYPE, Constants.TAG_SIGN_UP_CONNECTION);
                    JSONObject credential = new JSONObject();
                    credential.put(Constants.TAG_USERNAME, username.getText().toString());
                    credential.put(Constants.TAG_PASSWORD, password.getText().toString());
                    credential.put(Constants.TAG_USER_TYPE, userType);
                    objectToSend.put(Constants.TAG_CREDENTIAL, credential);
                    new SignUpTask(objectToSend).execute();
                    showProgress();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void successfulSignUp() {
        Toast.makeText(getApplicationContext(), "Sign Up Successful!.", Toast.LENGTH_SHORT).show();
        hideProgress();
    }

    private void failedSignUp() {
        Toast.makeText(getApplicationContext(), "Sign Up Failed.", Toast.LENGTH_SHORT).show();
        hideProgress();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        submit.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        submit.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
