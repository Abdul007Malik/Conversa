package firebase;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;

import java.util.Map;

/**
 * Created by AM on 13-04-2016.
 */
public class UserAccount {

    Firebase firebase;
    String username;
    String password;
    Boolean accountStatus, loginStatus; //True if task is successfully completed or not

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
                loginStatus = true;
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                System.out.println("Something went wrong!! \n logging is not possible cannot be created..");
                loginStatus = false;
            }
        });

        return loginStatus;
    }



}
