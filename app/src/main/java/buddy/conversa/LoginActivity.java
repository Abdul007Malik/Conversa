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
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import firebase.ServerDataHandling;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText username;
    private EditText password;
    private Button btnLogin;
    private TextView signUpLink;
    private final int SIGNUP_INTENT = 3;
    private boolean flag;


    ServerDataHandling sdHandler;
    private ProgressDialog progressDialog;
    private Firebase firebase;
    private AuthData authData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sdHandler = ServerDataHandling.getInstance();
        btnLogin = (Button) findViewById(R.id.btnLogin);
        signUpLink = (TextView) findViewById(R.id.signUpLink);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == SIGNUP_INTENT) {
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
                    progressDialog.setMessage("Loging in");
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


    public void login(String username, String password) throws FirebaseException {
        firebase = sdHandler.getFirebaseRef();
        firebase.authWithPassword(username+"@firebase.com", password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                try {
                    progressDialog.hide();
                    sdHandler.myInfo.setId(authData.getUid());
                    progressDialog.setMessage("Authenticating");
                    Toast.makeText(LoginActivity.this, "You are Logged in with" + authData.getProviderData(), Toast.LENGTH_SHORT).show();
                    LoginActivity.this.authData = authData;
                    progressDialog.show();

                    isUserExist();
                    //setAuthenticatedUser();

                }catch (Exception e){
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    returnResult(false);
                    System.out.print("e"+e.getMessage());
                    Log.e(TAG,"exception",e);
                }

            }

            @Override
            public void onAuthenticationError(FirebaseError error) {
                if(progressDialog.isShowing())
                progressDialog.dismiss();
                try {
                    showErrorDialog(error.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e(TAG,"exception",error.toException());

            }
        });

    }

    /* *
            * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.*/
    private void setAuthenticatedUser() {


        try{
                firebase.child("users").child(firebase.getAuth().getUid()).setValue(sdHandler.myInfo, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();

                        if(firebaseError == null){

                            returnResult(true);

                        }
                        else{
                            Log.e(TAG,"exception",firebaseError.toException());
                            returnResult(false);
                        }
                    }
                });
        }catch (Exception e){
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            Log.e(TAG,"exception",e);
            returnResult(false);
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

    public void isUserExist(){
        firebase.child("users").child(firebase.getAuth().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(null==dataSnapshot.getValue()){
                    System.out.println(dataSnapshot);
                   setAuthenticatedUser();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

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

}