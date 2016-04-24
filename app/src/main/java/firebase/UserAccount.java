package firebase;

import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class UserAccount {

    Firebase firebase;

    String firebase_url;

    Boolean status; //True if task is successfully completed or not

    private AuthData mAuthData;

    private String mProvider; /*possible values: password, facebook,twitter*/

    private String errorMsg;/* contain error message*/

    public String getFirebase_url() {
        return firebase_url;
    }

    public void setFirebase_url(String firebase_url) {
        this.firebase_url = firebase_url;
    }

    public String getMessage() {
        return errorMsg;
    }


    public Firebase getFirebaseRef() {
        return firebase;
    }
    /*
    * This method creates user account but not authenticate, for that userLogin() method is used
    * The app url:https://conversa.firebaseIO.com" is used to access the app
    * */
    public Boolean createUserAccount(String username, String password)throws FirebaseException{
        if(firebase == null)
            firebase = new Firebase(firebase_url);
        firebase.createUser(username+"@firebase.com", password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                System.out.println("Successfully created user account with uid:" + result.get("uid"));
                status = true;
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                System.out.println("Something went wrong!! \n Account cannot be created..");
                status = false;
            }
        });



        return status;

    }

    /*
    * This method Authenticate/Login the user
    * */
    public AuthData userLogin(String username, String password)throws FirebaseException,Exception {


            if(firebase == null)
                firebase = new Firebase(firebase_url);
        firebase.authWithPassword(username+"@firebase.com", password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
               System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
              /* Authentication just completed successfully :)
                Map<String, String> map = new HashMap<>();
                map.put("provider", authData.getProvider());
                if(authData.getProviderData().containsKey("displayName")) {
                    map.put("displayName", authData.getProviderData().get("displayName").toString());
                }firebase.child("users").child(authData.getUid()).setValue(map);*/
                mAuthData = authData;
            }

            @Override
            public void onAuthenticationError(FirebaseError error) {
                errorMsg = error.getMessage();
                mAuthData = null;
            }
        });


        return mAuthData;
    }


    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void logout(AuthData authData) {
        try{
            if (authData != null) {

            /* logout of Firebase */
            firebase.unauth();

            /* Call method that make login button visible in main Activity*/
            //method()

            /* Update authenticated user */
            //authData = null;
        }
        }catch(FirebaseException fe){
        fe.printStackTrace();
        }
    }


/*
* This method will remove the user from database
*/

    protected Boolean remove(String username, String password)throws FirebaseException{
        try{
            if(firebase == null)
                firebase = new Firebase("https://conversa.firebaseio.com");
        firebase.removeUser(username, password, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                status = true;
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                status = false;
            }
        });
        }catch(FirebaseException fe){
        fe.printStackTrace();
        }

        return status;
    }
    public String getCurrentDate(){
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

        return ft.format(date);
    }



}
