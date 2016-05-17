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

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import firebase.ServerDataHandling;

public class SignUpActivity extends AppCompatActivity {


    private EditText newUserName;
    private EditText newPwd;
    private EditText mob;
    private EditText userCity;
    private Button btnSignUp;
    private TextView loginLink;

    boolean upStatus;
    Firebase firebase;
    final static String TAG = SignUpActivity.class.getSimpleName();

    ServerDataHandling sdHandler;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sdHandler = ServerDataHandling.getInstance();
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        loginLink = (TextView) findViewById(R.id.loginLink);


    }

/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();

        if(sdHandler.userAccount!=null && sdHandler.userAccount.isAccountCreated()){
            System.out.println("on pause signup");
            Intent intent = new Intent();
            intent.putExtra("flag",true);
            setResult(RESULT_OK,intent);
            finish();
        }
    }*/

    public void clickHandler(View target){
        upStatus = false;
        switch (target.getId()){
            case R.id.btnSignUp:
                //Check for validation
                boolean isNumber = false;
                mob = (EditText)findViewById(R.id.mob);
                String text = mob.getText().toString();
                try {
                    double num = Double.parseDouble(text);
                    isNumber = true;
                } catch (NumberFormatException e) {

                }
                newUserName = (EditText)findViewById(R.id.newUserName);
                newPwd = (EditText)findViewById(R.id.newPwd);
                userCity = (EditText)findViewById(R.id.userCity);

                if (newUserName.getText().toString().length() == 0
                        || newUserName.getText().toString().length() > 20 || newUserName.getText().toString().contains(" ")) {
                    newUserName.setError("Specify Username without spaces and not more than 20 characters");
                } else if (newPwd.getText().length() < 8 && newPwd.getText().length() > 15) {
                    newPwd.setError("Password must be in the range 8 to 15");
                } else if (userCity.getText().toString().length() == 0) {
                    userCity.setError("Specify the City");
                } else if (mob.getText().length() != 10 && !isNumber) {
                    mob.setError("Enter the mobile No and of length 10 and must be numeric");
                }
                //if validated then perform account creation
                else {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Loading");
                    progressDialog.setMessage("Creating Account");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(true);
                    progressDialog.show();

                    createAccount(newUserName.getText().toString()
                            ,newPwd.getText().toString()
                            ,userCity.getText().toString(),
                            Double.parseDouble(mob.getText().toString()));

                }
                /* Check if the user is authenticated with Firebase already. If this is the
                       case we can set the authenticated user*/


                break;


            case R.id.loginLink:
                    Intent resultIntent = new Intent();
                    setResult(RESULT_CANCELED,resultIntent);
                    finish();
                break;
        }



    }

    /**
     * Show errors to users
     */
    private void showDialog(String message) throws Exception{
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void createAccount(final String userName, String password, final String city, final double mob) throws
            FirebaseException {
        firebase = sdHandler.getFirebaseRef();
        firebase.createUser(userName+"@firebase.com", password, new Firebase.ValueResultHandler<Map<String, Object>>() {

            @Override
            public void onSuccess(Map<String, Object> result) {
                try {
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Successfully created user account with uid:" + result.get("uid"), Toast.LENGTH_LONG).show();
                    sdHandler.myInfo.setMyDB(userName, mob, city, null);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("flag", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }catch (Exception e){
                    Log.e(TAG,"exception",e);
                }

            }

            @Override
            public void onError(FirebaseError firebaseError) {
                progressDialog.dismiss();
                Toast.makeText(SignUpActivity.this,"Failed to create user",Toast.LENGTH_LONG).show();
                try {
                    showDialog(firebaseError.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e(TAG,"exception",firebaseError.toException());
            }
        });


    }
}
