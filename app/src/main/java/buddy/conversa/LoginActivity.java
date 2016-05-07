package buddy.conversa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;

import java.util.concurrent.ExecutionException;

import firebase.ServerDataHandling;

public class LoginActivity extends AppCompatActivity {


    private EditText username;
    private EditText password;
    private Button btnLogin;
    private TextView signUpLink;
    private final int SIGNUP_INTENT = 3;
    private boolean loginStatus;


    ServerDataHandling sdHandler;
    private ProgressDialog progressDialog;
    private Firebase firebase;
    private AuthData authData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sdHandler = ServerDataHandling.getInstance();
        sdHandler.setActivity(this);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        signUpLink = (TextView) findViewById(R.id.signUpLink);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == SIGNUP_INTENT
                && data.getExtras() != null
                && data.getExtras().containsKey("flag")
                && data.getExtras().getBoolean("flag")) {
            signUpLink.setError("Already signup");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public void clickHandler(View target) {

        switch (target.getId()) {
            case R.id.btnLogin:

                username = (EditText) findViewById(R.id.username);
                password = (EditText) findViewById(R.id.pwd);

                if (username.getText().toString().length() == 0
                        || username.getText().toString().length() > 20) {
                    username.setError("Specify Username of length less than 20 characters!!");
                } else if (password.getText().length() < 8 && password.getText().length() > 15) {
                        password.setError("Password must be in the range 8 to 15");
                }
                else {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Loading");
                    progressDialog.setMessage("Creating Account");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(true);
                    progressDialog.show();



                    login(username.getText().toString(),password.getText().toString());
                }

                break;
            case R.id.signUpLink:
                Intent intent = new Intent(this, SignUpActivity.class);
                this.startActivityForResult(intent, SIGNUP_INTENT);
                break;
        }

    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) throws Exception {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void login(String username, String password) throws FirebaseException {
        firebase = sdHandler.getFirebaseRef();
        firebase.authWithPassword(username+"@firebase.com", password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                progressDialog.hide();
                Toast.makeText(LoginActivity.this,"You are Logged in with" + authData ,Toast.LENGTH_LONG).show();
                progressDialog.show();
                setAuthenticatedUser();
            }

            @Override
            public void onAuthenticationError(FirebaseError error) {
                progressDialog.hide();
                try {
                    showErrorDialog(error.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /* *
            * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.*/
    private void setAuthenticatedUser() throws FirebaseException {
        final boolean[] authStatus = new boolean[1];
        authStatus[0] = true;
        String userId = authData.getUid();
        try{
        if (firebase.child("users").child(userId).child("username") == null) {
                firebase.child("users").child(userId).setValue(sdHandler.myInfo, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if(firebaseError == null){
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"User ID: " +authData .getUid() + ", Provider: "
                                    + authData.getProvider(),Toast.LENGTH_LONG).show();
                            returnResult(true);

                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"User Info is not stored to the Server",Toast.LENGTH_LONG).show();
                            returnResult(false);
                        }
                    }
                });
            }}catch (Exception error){
            error.printStackTrace();
            try {
                showErrorDialog(error.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

public void returnResult(boolean val){

    Intent intent = new Intent();
    if(val)
        setResult(RESULT_OK,intent);
    else
    setResult(RESULT_CANCELED,intent);
    finish();

}


}