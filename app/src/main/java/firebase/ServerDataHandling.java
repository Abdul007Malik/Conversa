package firebase;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import buddy.conversa.acitvityutility.Invite;
import buddy.conversa.acitvityutility.UserNameInviteList;
import sqliteDB.Group;
import sqliteDB.MyDB;


public class ServerDataHandling {
    private static final ServerDataHandling sdHandler = new ServerDataHandling();


    public static ServerDataHandling getInstance(){  return  sdHandler; }



    private Firebase firebase;
    private Firebase groupRef;
    private Firebase friendRef;
    private Firebase userRef;
    private Firebase inviteRef;
    private Activity activity;

    public Firebase getFirebaseRef() {
        return firebase;
    }

    public void setActivity(Activity activity){ this.activity = activity;}

    private List<String> idList;

    private List<Group> groupList;

    private List<String> friendList;

    private boolean authStatus;

    /* Data from the authenticated user */
    private AuthData authData;

    private UserNameInviteList userNameInviteList;

    private List<String> userGroupList;

    private List<Invite> inviteList;


    public List<Group> getGroupList() {
        return groupList;
    }

    public MyDB myInfo;
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public boolean isAuthStatus() {
        return authStatus;
    }

    // A mapping of room IDs to a boolean indicating presence.



    private ServerDataHandling() {
        // User-specific instance variables.
        myInfo = new MyDB();
        firebase = new Firebase(firebase_url);
        this.activity = activity;

    }

    final String firebase_url = "https://conversa.firebaseIO.com";

    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    public void logout(AuthData authData) {
        try{
            if (authData != null) {

            /* logout of Firebase */
                firebase.unauth();

            }
        }catch(FirebaseException fe){
            fe.printStackTrace();
        }
    }


    public String getCurrentDate(){
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

        return ft.format(date);
    }

}