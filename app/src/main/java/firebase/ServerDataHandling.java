package firebase;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AM on 23-04-2016.
 */
public class ServerDataHandling {
    private Firebase firebase;
    private Firebase messageRef;
    private Firebase groupRef;
    private Firebase moderatorRef;
    private Firebase userOnlineRef;

    /* Data from the authenticated user */
    private AuthData authData;
    private UserAccount userAccount;


    String user,userId,userName,sessionId,city,mobileNo,displayName,;
    Boolean isModerator;

    // A mapping of room IDs to a boolean indicating presence.
    List<String> rooms;

    public ServerDataHandling(){


        this.messageRef = this.firebase.child("group_messages");
        this.groupRef = this.firebase.child("groups");
        this.moderatorRef = this.firebase.child("moderators");
        this.userOnlineRef = this.firebase.child("user_names_online");

        // User-specific instance variables.
        this.user = null;
        this.userId = null;
        this.userName = null;
        this.isModerator = false;

        // A unique id generated for each session.
        this.sessionId = null;

        userAccount = new UserAccount();
        this.firebase = userAccount.getFirebase();
    }

    public void createAccount(String userName, String password){

        try {
            if(userAccount.createUserAccount(userName,password)){
                authData = userAccount.userLogin(userName,password);
            }
        }
        catch(FirebaseException fe){
            fe.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(String username, String password){
        try{
            authData = userAccount.userLogin(username,password);
            setAuthentication(authData);

        }catch (FirebaseException fe){
            fe.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /* *
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.*/
         private Boolean setAuthenticatedUser(AuthData authData) {
        Boolean status;
        if (authData != null) {
            status=true;
            userId = authData.getUid();
            if (userId != null && (firebase.child("users").child(authData.getUid().toString()).child("username")==null)) {
                Map<String,String> map = new HashMap<>();
                map.put("username",userName);
            }
        } else {
            status = false;

        }

        return status;
    }

}
