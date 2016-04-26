package buddy.conversa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseException;

import java.io.IOException;

import firebase.ServerDataHandling;

/**
 * Created by AM on 25-04-2016.
 */
public class MyAsyncTask extends AsyncTask<String,Void,Boolean> {
    ProgressDialog mAuthProgressDialog;
    ServerDataHandling sdHandler;
    String name,pwd;
    Firebase.AuthStateListener mAuthStateListener;

    //status[0] for create Account and status[1] for login in firebase
    Boolean[] status = new Boolean[2];


    public AsyncResponse delegate = null;
    private Activity activity;
    public MyAsyncTask(Activity activity,AsyncResponse delegate){
        this.activity = activity;
        this.delegate = delegate;
    }

    public MyAsyncTask(String s, String s1,Activity activity,AsyncResponse delegate) {
       name = s;
        pwd = s1;
        this.activity = activity;
        this.delegate = delegate;

    }


    // This method is to return the result to Activity class
    public interface AsyncResponse {
        void processFinish(Boolean status,ServerDataHandling sdHandlerOutput);
    }



    @Override
    protected Boolean doInBackground(String... params) {

        if(params[0].equals("login")) {

            sdHandler = new ServerDataHandling();

            try {
                status[1] = sdHandler.login(name
                        , pwd);
            } catch (FirebaseException ex) {
                sdHandler.userAccount.setMessage(ex.getMessage());
                ex.printStackTrace();
            }

            mAuthProgressDialog = new ProgressDialog(activity);
            mAuthProgressDialog.setTitle("Loading");
            mAuthProgressDialog.setMessage("Authenticating with Firebase...");
            mAuthProgressDialog.setCancelable(false);
            mAuthProgressDialog.show();

            mAuthStateListener = new Firebase.AuthStateListener() {
                @Override
                public void onAuthStateChanged(AuthData authData) {
                    mAuthProgressDialog.dismiss();
                }
            };
        }else if(params[0].equals("signup")){
            sdHandler = new ServerDataHandling();

            try {
                status = sdHandler.createAccount(name
                        , pwd);
                if(!status[0])
                {
                    sdHandler.userAccount.setMessage("Sorry account cannot be created try changing username" +
                            "or may be network problem");
                    return status[0];
                }


            } catch (FirebaseException ex) {
                sdHandler.userAccount.setMessage(ex.getMessage());
                ex.printStackTrace();
            }

            mAuthProgressDialog = new ProgressDialog(activity);
            mAuthProgressDialog.setTitle("Loading");
            mAuthProgressDialog.setMessage("Creating Account in Firebase...");
            mAuthProgressDialog.setCancelable(false);
            mAuthProgressDialog.show();

            mAuthStateListener = new Firebase.AuthStateListener() {
                @Override
                public void onAuthStateChanged(AuthData authData) {
                    mAuthProgressDialog.dismiss();
                }
            };

        }else {
            sdHandler = new ServerDataHandling();

            try {
                status = sdHandler.createAccount(name
                        , pwd);
                if(!status[0])
                {
                    sdHandler.userAccount.setMessage("Sorry account cannot be created try changing username" +
                            "or may be network problem");
                    return status[0];
                }


            } catch (FirebaseException ex) {
                sdHandler.userAccount.setMessage(ex.getMessage());
                ex.printStackTrace();
            }

            mAuthProgressDialog = new ProgressDialog(activity);
            mAuthProgressDialog.setTitle("Loading");
            mAuthProgressDialog.setMessage("Creating Account in Firebase...");
            mAuthProgressDialog.setCancelable(false);
            mAuthProgressDialog.show();

            mAuthStateListener = new Firebase.AuthStateListener() {
                @Override
                public void onAuthStateChanged(AuthData authData) {
                    mAuthProgressDialog.dismiss();
                }
            };

        }
        return status[1];
    }

  /*  public MyAsyncTask(AsyncResponse delegate){
        this.delegate = delegate;
    }*/

    @Override
    protected void onPostExecute(Boolean result) {
        delegate.processFinish(result,sdHandler);
    }
}