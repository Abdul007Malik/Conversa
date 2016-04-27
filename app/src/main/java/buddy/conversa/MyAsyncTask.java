package buddy.conversa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseException;

import buddy.conversa.acitvityutility.UserNameInviteList;
import firebase.ServerDataHandling;
import sqliteDB.Group;


public class MyAsyncTask extends AsyncTask<String,Void,Boolean> {
    ProgressDialog mAuthProgressDialog;
    ServerDataHandling sdHandler;
    String name,pwd;
    Firebase.AuthStateListener mAuthStateListener;
    Group group;
    UserNameInviteList userNameInviteList;

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

    public MyAsyncTask(Activity activity, AsyncResponse delegate, Group group){
        this.group = group;
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

            sdHandler = ServerDataHandling.getInstance();

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
                   myListener();
                }
            };
        }else if(params[0].equals("signup")){


            sdHandler = ServerDataHandling.getInstance();

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
                    myListener();
                }
            };

        }
        else if(params[0].equals("CreateGroup")){


            sdHandler = ServerDataHandling.getInstance();
            status[0] = false;
            userNameInviteList = UserNameInviteList.getInstance();
            status[0] = sdHandler.createGroup(group.getGroup_name()
                    ,group.getGroup_city()
                    ,group.getDescription()
                    ,userNameInviteList.getUserNames()
                    ,this);
            mAuthProgressDialog = new ProgressDialog(activity);
            mAuthProgressDialog.setTitle("Loading");
            mAuthProgressDialog.setMessage("Creating Group and sending invitation to Friends...");
            mAuthProgressDialog.setCancelable(false);
            mAuthProgressDialog.show();
            if(!status[0] && sdHandler.userAccount.getMessage().equals("login first") )
            {   myListener();
                return status[0];
            }
            else if(!status[0]){
                myListener();
                throw new FirebaseException("Server went Down try after some time");
            }
            else{
                return status[0];
            }






        }
        /*else {
            sdHandler = ServerDataHandling.getInstance();

            try {
                status[0] = false;
                status[0] = sdHandler.sendInvitationToFriends();
                if(!status[0] && sdHandler.userAccount.getMessage().equals("login first") )
                {
                    return status[0];
                }
                else if(!status[0]){
                    throw new FirebaseException("Server went Down try after some time");
                }
                else{
                    return status[0];
                }


            } catch (FirebaseException ex) {
                sdHandler.userAccount.setMessage(ex.getMessage());
                ex.printStackTrace();
            }

            mAuthProgressDialog = new ProgressDialog(activity);
            mAuthProgressDialog.setTitle("Loading");
            mAuthProgressDialog.setMessage("Creating Group and sending invitation to Friends...");
            mAuthProgressDialog.setCancelable(false);
           // mAuthProgressDialog.show();



        }*/
        return status[1];
    }

 /*  public MyAsyncTask(){

    }*/

    public void myListener(){
        mAuthProgressDialog.dismiss();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        delegate.processFinish(result,sdHandler);
    }
}