package firebase;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;

import java.util.HashMap;
import java.util.Map;


public class UserAccount {

    Firebase firebase;
    String username;
    String password;
    Boolean accountStatus, status; //True if task is successfully completed or not

    /*
    * This method creates user account but not authenticate, for that userLogin() method is used
    * The app url:https://conversa.firebaseIO.com" is used to access the app
    * */
    public Boolean createUserAccount(String username, String password)throws FirebaseException{
        if(firebase == null)
            firebase = new Firebase("https://conversa.firebaseIO.com");
        firebase.createUser(username, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
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
        {   if(userLogin()){
                this.username = username;
                this.password = password;
            }
            else{
            accountStatus = false;
            }
        }

        return accountStatus;

    }

    /*
    * This method Authenticate the user
    * */
    public Boolean userLogin()throws FirebaseException {
        if(firebase == null)
            firebase = new Firebase("https://conversa.firebaseIO.com");
        firebase.authWithPassword(username, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                // Authentication just completed successfully :)
                Map<String, String> map = new HashMap<>();
                map.put("provider", authData.getProvider());
                if(authData.getProviderData().containsKey("displayName")) {
                    map.put("displayName", authData.getProviderData().get("displayName").toString());
                }
                firebase.child("users").child(authData.getUid()).setValue(map);
                status = true;
            }

            @Override
            public void onAuthenticationError(FirebaseError error) {
                System.out.println("Something went wrong!! \n logging is not possible cannot be created..");
                // Something went wrong :(
                switch (error.getCode()) {
                    case FirebaseError.USER_DOES_NOT_EXIST:
                        // handle a non existing user
                        System.out.println("either username or password is incorrect ");
                        break;
                    case FirebaseError.INVALID_PASSWORD:
                        // handle an invalid password
                        System.out.println("password is invalid");
                        break;
                    default:
                        // handle other errors
                        System.out.println("server is down!!!");
                        break;
                }
                status = false;
            }
        });

        return status;
    }

/*
* This method will remove the user from database
*/

    protected void remove(String username, String password)throws FirebaseException{
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
    }
}
