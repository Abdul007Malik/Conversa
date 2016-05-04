package buddy.conversa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.concurrent.ExecutionException;

import firebase.ServerDataHandling;
import firebase.UserAccount;

public class LoginActivity extends AppCompatActivity {


    private EditText username;
    private EditText password;
    private Button btnLogin;
    private TextView signUpLink;

    private boolean status;

    private ProgressDialog mAuthProgressDialog;
    private Firebase.AuthStateListener mAuthStateListener;


    ServerDataHandling sdHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        signUpLink = (TextView) findViewById(R.id.signUpLink);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == 3
                && data.getExtras() != null
                && data.getExtras().containsKey("flag")
                && data.getExtras().getBoolean("flag")) {
            signUpLink.setError("Already signup");
        }
    }

    public void clickHandler(View target) {
        status = false;
        switch (target.getId()) {
            case R.id.btnLogin:

                username = (EditText) findViewById(R.id.username);
                password = (EditText) findViewById(R.id.pwd);

                if (username.getText().toString().length() == 0
                        || username.getText().toString().length() > 20) {
                    username.setError("Specify Username aof length less than 20 characters!!");
                } else {
                    if (password.getText().length() < 8 && password.getText().length() > 15) {
                        password.setError("Password must be in the range 8 to 15");
                    } else {


                        MyAsyncTask task = new MyAsyncTask(username.getText().toString()
                                , password.getText().toString()
                                , LoginActivity.this
                                , new MyAsyncTask.AsyncResponse() {
                            @Override
                            public void processFinish(boolean status, ServerDataHandling sdHandlerOutput) {
                                sdHandler = sdHandlerOutput;
                                LoginActivity.this.status = status;
                            }
                        });
                        try {
                            task.execute("login").get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }


                        // status = task.execute();
                        // mLoggedInStatusTextView = (TextView) findViewById(R.id.status);


                    /* Setup the progress dialog that is displayed later when authenticating with
                     Firebase */

                        if (!status) {
                            try {
                                showErrorDialog("OOPS!!\n" + sdHandler.userAccount.getMessage());
                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                        } else {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("flag",true);
                            setResult(RESULT_OK,resultIntent);
                            finish();
                        }


                    }
                }

                break;
            case R.id.signUpLink:
                Intent intent = new Intent(this, SignUpActivity.class);
                this.startActivityForResult(intent, 3);
                finish();

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
}