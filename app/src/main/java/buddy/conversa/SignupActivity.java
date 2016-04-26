package buddy.conversa;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import firebase.ServerDataHandling;

public class SignUpActivity extends AppCompatActivity {


    private EditText newUserName;
    private EditText newPwd;
    private EditText mob;
    private EditText city;
    private Button btnSignUp;

    Boolean status;

    ServerDataHandling sdHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);


    }

    public void clickHandler(View target){
        status = false;
        switch (target.getId()){
            case R.id.btnSignUp:
                //Check for validation
                newUserName = (EditText)findViewById(R.id.newUserName);
                newPwd = (EditText)findViewById(R.id.newPwd);
                city = (EditText)findViewById(R.id.userCity);
                mob = (EditText)findViewById(R.id.mob);
                if (newUserName.getText().toString().length() == 0
                        || newUserName.getText().toString().length() > 20) {
                    newUserName.setError("Specify Username and not more than 20 characters");
                } else if (newPwd.getText().length() < 8 && newPwd.getText().length() > 15) {
                    newPwd.setError("Password must be in the range 8 to 15");
                } else if (city.getText().toString().length() == 0) {
                    city.setError("Specify the City");
                } else if (mob.getText().length() != 10) {
                    mob.setError("Enter the mobile No and of length 10");
                }
                //if validated then perform account creation
                else {

                    MyAsyncTask task = new MyAsyncTask(newUserName.getText().toString()
                            , newPwd.getText().toString()
                            , SignUpActivity.this
                            , new MyAsyncTask.AsyncResponse() {
                        @Override
                        public void processFinish(Boolean status, ServerDataHandling sdHandlerOutput) {
                            sdHandler = sdHandlerOutput;
                            SignUpActivity.this.status = status;
                        }
                    });
                    task.execute("signup");


                }
                /* Check if the user is authenticated with Firebase already. If this is the
                       case we can set the authenticated user*/

                if (!status) {
                    try {
                        showErrorDialog("OOPS!!\n" + sdHandler.userAccount.getMessage());
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                }
                break;



        }

    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) throws Exception{
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
