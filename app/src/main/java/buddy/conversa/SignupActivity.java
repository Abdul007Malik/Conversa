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
    private EditText userCity;
    private Button btnSignUp;

    boolean status;

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
                boolean isNumber = false;
                String text = mob.getText().toString();
                try {
                    double num = Double.parseDouble(text);
                    isNumber = true;
                } catch (NumberFormatException e) {

                }
                newUserName = (EditText)findViewById(R.id.newUserName);
                newPwd = (EditText)findViewById(R.id.newPwd);
                userCity = (EditText)findViewById(R.id.userCity);
                mob = (EditText)findViewById(R.id.mob);
                if (newUserName.getText().toString().length() == 0
                        || newUserName.getText().toString().length() > 20) {
                    newUserName.setError("Specify Username and not more than 20 characters");
                } else if (newPwd.getText().length() < 8 && newPwd.getText().length() > 15) {
                    newPwd.setError("Password must be in the range 8 to 15");
                } else if (userCity.getText().toString().length() == 0) {
                    userCity.setError("Specify the City");
                } else if (mob.getText().length() != 10 && !isNumber) {
                    mob.setError("Enter the mobile No and of length 10 and must be numeric");
                }
                //if validated then perform account creation
                else {

                    MyAsyncTask task = new MyAsyncTask(newUserName.getText().toString()
                            , newPwd.getText().toString()
                            ,Double.parseDouble(mob.getText().toString())
                            ,userCity.getText().toString()
                            , SignUpActivity.this
                            , new MyAsyncTask.AsyncResponse() {
                        @Override
                        public void processFinish(boolean status, ServerDataHandling sdHandlerOutput) {
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

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("flag",true);
                    setResult(RESULT_OK,resultIntent);
                    finish();
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
