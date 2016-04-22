package firebase;

import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;

import java.util.HashMap;
import java.util.Map;


public class UserAccount {

    Firebase firebase;

    String firebase_url;

    String username, password;

    Boolean accountStatus, status; //True if task is successfully completed or not

    /* Data from the authenticated user */
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

    public void setMessage(String message) {
        this.errorMsg = message;
    }

    /*
    * This method creates user account but not authenticate, for that userLogin() method is used
    * The app url:https://conversa.firebaseIO.com" is used to access the app
    * */
    public Boolean createUserAccount(String username, String password)throws Exception{
       try{ if(firebase == null)
            firebase = new Firebase(firebase_url);
        firebase.createUser(username+"@firebase.com", password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                System.out.println("Successfully created user account with uid:" + result.get("uid"));
                accountStatus = true;
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                System.out.println("Something went wrong!! \n Account cannot be created..");
                accountStatus = false;
            }
        });

        if(accountStatus)
        {   if(userLogin(username,password)){
                this.username = username;
                this.password = password;
            }
            else{
            accountStatus = false;
            }
        }
       }catch(FirebaseException fe){
            fe.printStackTrace();
       }

        return accountStatus;

    }

    /*
    * This method Authenticate/Login the user
    * */
    public Boolean userLogin(String username, String password)throws Exception {
        try{
            if(firebase == null)
                firebase = new Firebase(firebase_url);
        firebase.authWithPassword(username+"@firebase.com", password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
               System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());

                mAuthData = authData;
              // Authentication just completed successfully :)
                /*Map<String, String> map = new HashMap<>();
                map.put("provider", authData.getProvider());
                if(authData.getProviderData().containsKey("displayName")) {
                    map.put("displayName", authData.getProviderData().get("displayName").toString());
                }*/

                setAuthenticatedUser(authData);
                //firebase.child("users").child(authData.getUid()).setValue(map);
                status = true;
            }

            @Override
            public void onAuthenticationError(FirebaseError error) {
                System.out.println("Something went wrong!! \n logging is not possible cannot be created..");
                // Something went wrong :(
                switch (error.getCode()) {
                    case FirebaseError.USER_DOES_NOT_EXIST:
                        // handle a non existing user
                        setMessage("either username or password is incorrect");
                        System.out.println("either username or password is incorrect ");
                        break;
                    case FirebaseError.INVALID_PASSWORD:
                        // handle an invalid password
                        setMessage("either username or password is incorrect");
                        System.out.println("password is invalid");
                        break;
                    default:
                        // handle other errors
                        setMessage("Server is Down!!");
                        System.out.println("server is down!!!");
                        break;
                }
                status = false;
            }
        });
        }catch(FirebaseException fe){
            fe.printStackTrace();
        }

        return status;
    }


    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void logout() {
        try{if (this.mAuthData != null) {

            /* logout of Firebase */
            firebase.unauth();

            /* Call method that make login button visible in main Activity*/
            //method()

            /* Update authenticated user */
            mAuthData = null;
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

    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    private Boolean setAuthenticatedUser(AuthData authData) {
        Boolean status;
        if (authData != null) {
            status=true;
            /* show a provider specific status text */
            String name = null;
            /*if ( authData.getProvider().equals("password")) {
                name = authData.getUid();
            } else{
                //Log.e(TAG,"Invalid Provide:"+authData.getProvider());
            }
            if (name != null) {
                mLoggedInStatusTextView.setText("Logged in as " + name + " (" + authData.getProvider() + ")");
            }*/
        } else {
            status = false;

        }
        this.mAuthData = authData;

        return status;
    }


}
