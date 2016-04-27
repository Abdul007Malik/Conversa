package firebase;

import android.content.Intent;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import buddy.conversa.CreateGroupActivity;
import buddy.conversa.LoginActivity;
import buddy.conversa.MyAsyncTask;
import buddy.conversa.acitvityutility.Invite;
import buddy.conversa.acitvityutility.UserNameInviteList;
import sqliteDB.MyDB;


public class ServerDataHandling {
    private static ServerDataHandling sdHandler = new ServerDataHandling();

    public static ServerDataHandling getInstance(){return  sdHandler;}



    private Firebase firebase;
    private Firebase messageRef;
    private Firebase groupRef;
    private Firebase moderatorRef;
    private Firebase userOnlineRef;
    private Firebase userRef;
    private List<String> idLists;
    private Firebase inviteRef;

    /* Data from the authenticated user */
    private AuthData authData;
    public UserAccount userAccount;
    private MyAsyncTask asyncTask;
    String groupId;
    UserNameInviteList userNameInviteList;

    private Iterator<DataSnapshot> iterator;


    String user, userId, userName, sessionId, city, mobileNo, displayName;
    Boolean isModerator;

    // A mapping of room IDs to a boolean indicating presence.
    List<String> rooms;



    private ServerDataHandling() {
        // User-specific instance variables.

        this.user = null;
        this.userId = null;
        this.userName = null;
        this.isModerator = false;

        // A unique id generated for each session.
        this.sessionId = null;

        userAccount = UserAccount.getUserAccountInstance();
        this.firebase = userAccount.getFirebaseRef();

    }

  /*  public void setReference() {
        if (firebase != null) {
            if (this.firebase.child("group_messages") != null)
                this.messageRef = this.firebase.child("group_messages");
            if (this.firebase.child("groups") != null)
                this.groupRef = this.firebase.child("groups");
            if (this.firebase.child("moderators") != null)
                this.moderatorRef = this.firebase.child("moderators");
            if (this.firebase.child("user_names_online") != null)
                this.userOnlineRef = this.firebase.child("user_names_online");
        } else
            System.out.println("setReference() method in ServerDataHandling firebase ref is null");
    }
*/

    public Boolean[] createAccount(String userName, String password) {
        Boolean[] status = new Boolean[2];
        status[0] = status[1] = false;
        try {
            status[0] = userAccount.createUserAccount(userName, password);

        } catch (FirebaseException fe) {
            fe.printStackTrace();
        }
        return status;

    }

    public Boolean login(String username, String password) {
        try {
            authData = userAccount.userLogin(username, password);
            return setAuthenticatedUser(authData);

        } catch (FirebaseException fe) {
            fe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /* *
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.*/
    private Boolean setAuthenticatedUser(AuthData authData) throws FirebaseException {
        Boolean status;

        if (authData != null) {
            status = true;
            userId = authData.getUid();
            //instead put complete class of user
            if (userId != null && (firebase.child("users").child(userId).child("username") == null)) {
                Map<String, String> map = new HashMap<>();
                map.put("username", userName);
                map.put("displayName", displayName);
                map.put("city", city);
                map.put("mob", mobileNo);
                map.put("provider", "password");
                firebase.child("users").child(userId).setValue(map);
            }
        } else {
            status = false;
        }

        return status;
    }

    public Boolean createGroup(String groupName, String city, String desc, List<String> list, MyAsyncTask task ) {
        Boolean status = false;
        this.asyncTask = task;

        this.groupRef = this.firebase.child("groups");
        Map<String, String> map = new HashMap<>();
        map.put("name", groupName);
        map.put("desc", desc);
        map.put("city", city);
        // if(authData!=null)
        map.put("admin", userId);
        //else
        //  Intent intent = new Intent(this,Login.class);
        map.put("creation_date", userAccount.getCurrentDate());
        map.put("members", list.toString());
        AuthData authData = firebase.getAuth();
        if(authData ==  null){
            userAccount.setMessage("login first");
            return false;
        }

        if (this.groupRef != null) {
           final Firebase newRef = groupRef.push();
                   newRef.setValue(map, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        userAccount.setMessage("Data could not be saved. " + firebaseError.getMessage());
                    } else {
                        groupId = newRef.getKey();
                        sendInvitationToFriends();
                        asyncTask.myListener();
                    }
                }
            });
            status = true;
        }

        return status;
    }

    public List<String> getGroups() {
        if (groupRef == null)
            groupRef = this.firebase.child("groups");
        final List<String> groupList = new ArrayList<>();

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    groupList.add(post.getKey());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return groupList;
    }


    public void addInGroup() {
        if (groupRef == null)
            groupRef = this.firebase.child("groups");

    }


    public Boolean sendInvitationToFriends(){
        Boolean status = false;
        idLists = new ArrayList<>();
        userNameInviteList = UserNameInviteList.getInstance();

        userRef = this.firebase.child("users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    MyDB userInfo = postSnapshot.getValue(MyDB.class);
                    if(userNameInviteList.getUserNames().contains(userInfo.getUsername())){

                        idLists.add(postSnapshot.getKey());

                    }


                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                sdHandler.userAccount.setMessage(firebaseError.getMessage());

            }
        });

        Invite invite = new Invite(authData.getUid(),userName,groupId,false);

        //to remove dublicate items
        idLists = new ArrayList<>(new HashSet<>(idLists));
        for(String list : idLists) {
            inviteRef = firebase.child("users").child(list).child("invites").push();
            invite.setId(inviteRef.getKey());
            inviteRef.setValue(invite);
            status = true;
        }



        return status;
    }

    public void acceptInvite(){


    }

}