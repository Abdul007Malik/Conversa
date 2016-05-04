package firebase;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;
import com.firebase.client.realtime.util.StringListReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import buddy.conversa.MyAsyncTask;
import buddy.conversa.acitvityutility.Invite;
import buddy.conversa.acitvityutility.UserNameInviteList;
import sqliteDB.Group;
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
    private Firebase inviteRef;

    private List<String> idLists;


    /* Data from the authenticated user */
    private AuthData authData;


    public UserAccount userAccount;
    private MyAsyncTask asyncTask;
    UserNameInviteList userNameInviteList;

    private Iterator<DataSnapshot> iterator;
    private List<Group> groupList;
    private List<String> groupUserList;
    List<Invite> inviteList;


    public List<Group> getGroupList() {
        return groupList;
    }

    String user, userId, userName, sessionId, city;
    double mob;
    boolean isModerator, status;

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

    public boolean[] createAccount(String userName, String password, String city, double mob) {
        boolean[] status = new boolean[2];
        status[0] = status[1] = false;
        try {
            status[0] = userAccount.createUserAccount(userName, password);
            this.userName = userName;
            this.mob = mob;
            this.city = city;

        } catch (FirebaseException fe) {
            fe.printStackTrace();
        }
        return status;

    }

    public boolean login(String username, String password) {
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
    private boolean setAuthenticatedUser(AuthData authData) throws FirebaseException {
        boolean status;

        if (authData != null) {
            status = true;
            userId = authData.getUid();
            //instead put complete class of user
            if (userId != null && (firebase.child("users").child(userId).child("username") == null)) {
                MyDB userInfo = new MyDB(userName,mob,city,null);
                firebase.child("users").child(userId).setValue(userInfo);
            }
        } else {
            status = false;
        }

        return status;
    }

    public boolean createGroup(String groupName, final String groupCity, String desc, List<String> memberList ) {
         status = false;
        if(authData==null)
            authData = firebase.getAuth();

        this.groupRef = this.firebase.child("groups").push();


        if (this.groupRef != null) {
            final Group group = new Group(groupRef.getKey(),groupName,groupCity
                    ,desc,authData.getUid(),userAccount.getCurrentDate()
                    ,memberList,memberList.size());

                   groupRef.setValue(group, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        userAccount.setMessage("Data could not be saved. " + firebaseError.getMessage());
                        status = false;
                    } else {
                        groupUserList = null;

                        firebase.child("users").child(authData.getUid()).child("groups")
                                .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                GenericTypeIndicator<List<String>> t =
                                        new GenericTypeIndicator<List<String>>() {};


                                groupUserList = dataSnapshot.getValue(t);
                                groupUserList.add(groupRef.getKey());
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                        firebase.child("users").child(authData.getUid()).child("groups").setValue(groupUserList);
                        sendInvitationToFriends(authData.getUid(),userName,groupRef.getKey());
                        status = true;
                    }
                }
            });
        }

        return status;
    }

    public boolean getGroups() {
        status=false;
        if (groupRef == null)
            groupRef = this.firebase.child("groups");

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()) {

                    Group group = post.getValue(Group.class);

                    groupList.add(group);

                }

                status = true;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                userAccount.setMessage(firebaseError.getMessage());
                status = false;            }
        });
        return status;
    }


    public void addInGroup() {
        if (groupRef == null)
            groupRef = this.firebase.child("groups");

    }


    public boolean sendInvitationToFriends(String uid,String userName,String groupId){
        status = false;
        idLists = new ArrayList<>();
        userNameInviteList = UserNameInviteList.getInstance();

        userRef = this.firebase.child("users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    MyDB userInfo = postSnapshot.getValue(MyDB.class);
                    if(userNameInviteList.getUserNames().contains(userInfo.getUsername()))
                        idLists.add(postSnapshot.getKey());

                    status = true;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                sdHandler.userAccount.setMessage(firebaseError.getMessage());
                status = false;
            }
        });

        Invite invite = new Invite(uid,userName,groupId,false);

        //to remove dublicate items
        idLists = new ArrayList<>(new HashSet<>(idLists));
        for(String list : idLists) {
            inviteRef = firebase.child("users").child(list).child("invites").push();
            invite.setId(inviteRef.getKey());
            inviteRef.setValue(invite);
            status = true;
        }

        userNameInviteList.setUserNameList(null);



        return status;
    }



    public boolean acceptInvite(String inviteId){
        status = false;

        userRef = firebase.child("users").child(authData.getUid());
        userRef.child("invites").child(inviteId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Invite invite = dataSnapshot.getValue(Invite.class);
                if(invite!=null){
                enterRoom(invite.getGroupId());
                    //it will delete the invitation from the server
                userRef.child("invites").child(invite.getId()).setValue(null);
                status = true;
                }else
                    status =false;

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                status = false;
            }
        });
        return status;
    }


    // for initialising it in the inner class and using it in outside
    private Invite invite;

    public boolean declineInvite(String inviteId){
        status = false;
        invite = null;
        userRef = firebase.child("users").child(authData.getUid());
        userRef.child("invites").child(inviteId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                invite = dataSnapshot.getValue(Invite.class);
                if(invite!=null){
                    userRef.child("invites").child(invite.getId()).setValue(null);
                    status = true;
                }else
                    status =false;

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                throw new FirebaseException("Server went down!!!!");
            }
        });
        if(!status)
            return false;
        groupRef = firebase.child("groups").child(invite.getGroupId()).child("members");
        Map<String,Object> map = new HashMap<>();

        // This statement will remove its entry from the group for thewhich the
        //invitation notificcation is there
        map.put(firebase.getAuth().getUid(),null);

        groupRef.updateChildren(map);
        return status;
    }


    public void enterRoom(String roomId){

        if(userRef==null)
            userRef = firebase.child("users").child(authData.getUid());

        userRef.child("groups").push().setValue(roomId);

    }


    public List<Invite> getInvitations() {
        inviteList = new ArrayList<>();
        inviteRef = this.firebase.child("users").child(firebase.getAuth().getUid()).child("invite");

        inviteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()) {

                    Invite invite = post.getValue(Invite.class);

                    inviteList.add(invite);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                userAccount.setMessage(firebaseError.getMessage());
                status = false;            }
        });
        return inviteList;
    }




}