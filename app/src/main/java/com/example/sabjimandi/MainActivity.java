package com.example.sabjimandi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sabjimandi.Configuration.ApiAuthenticationClient;
import com.example.sabjimandi.Validation.EmailValidation;

public class MainActivity extends AppCompatActivity {

    private Button button_login_login;
    private EditText editText_login_Username;
    private EditText editText_login_password;
    private String username;
    private String password;
    private String baseUrl;

    EmailValidation validation = new EmailValidation();

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void login(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("login", "This is login activity");
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.login_activity);

        editText_login_Username = (EditText) findViewById(R.id.loginEmail);
        editText_login_password = (EditText) findViewById(R.id.loginPassword);
        button_login_login = (Button) findViewById(R.id.login);
        baseUrl = "http://192.168.2.103:8080/login";
        button_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editText_login_Username.getText().toString();
                password = editText_login_password.getText().toString();

                ApiAuthenticationClient apiAuthenticationClient =new ApiAuthenticationClient(
                        baseUrl,
                        username,
                        password
                );
                AsyncTask<Void, Void, String> excute = new ExecuteNetworkOperation(apiAuthenticationClient);
                excute.execute();
            }
        });
        String email = editText_login_Username.getText().toString();
        String password = editText_login_password.getText().toString();
        if (!validation.isValidEmail(email) || email.equals("")) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
        } else if (password.equals("") || password.length() < 6) {
            Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "The Email is " + email + " and password is " + password, Toast.LENGTH_SHORT).show();
        }


    }

    private class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ApiAuthenticationClient apiAuthenticationClient;
        private String isValidCrediantials;

        public ExecuteNetworkOperation(ApiAuthenticationClient apiAuthenticationClient) {
            this.apiAuthenticationClient = apiAuthenticationClient;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.loadingpanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            findViewById(R.id.loadingpanel).setVisibility(View.GONE);
            if(isValidCrediantials.equals(true)){
                gotoSecondActivity();
            }
            else {
                Toast.makeText(getApplicationContext(),"Login Failed", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                isValidCrediantials = apiAuthenticationClient.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void gotoSecondActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        bundle.putString("baseUrl",baseUrl);
        Intent intent = new Intent(this,HomePageActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
