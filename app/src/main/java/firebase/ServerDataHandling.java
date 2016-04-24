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
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ServerDataHandling {
    private Firebase firebase;
    private Firebase messageRef;
    private Firebase groupRef;
    private Firebase moderatorRef;
    private Firebase userOnlineRef;

    /* Data from the authenticated user */
    private AuthData authData;
    private UserAccount userAccount;

    private Iterator<DataSnapshot> iterator;


    String user,userId,userName,sessionId,city,mobileNo,displayName;
    Boolean isModerator;

    // A mapping of room IDs to a boolean indicating presence.
    List<String> rooms;

    public ServerDataHandling(){
        // User-specific instance variables.
        this.user = null;
        this.userId = null;
        this.userName = null;
        this.isModerator = false;

        // A unique id generated for each session.
        this.sessionId = null;

        userAccount = new UserAccount();
        this.firebase = userAccount.getFirebaseRef();
    }
    public void setReference() {
        if(firebase!=null) {
            if (this.firebase.child("group_messages") != null)
                this.messageRef = this.firebase.child("group_messages");
            if (this.firebase.child("groups") != null)
                this.groupRef = this.firebase.child("groups");
            if (this.firebase.child("moderators") != null)
                this.moderatorRef = this.firebase.child("moderators");
            if (this.firebase.child("user_names_online") != null)
                this.userOnlineRef = this.firebase.child("user_names_online");
        }
        else
            System.out.println("setReference() method in ServerDataHandling firebase ref is null");
    }



    public void createAccount(String userName, String password){

        try {
            if(userAccount.createUserAccount(userName,password)){
                login(userName,password);
            }
        }
        catch(FirebaseException fe){
            fe.printStackTrace();
        }

    }

    public void login(String username, String password){
        try{
            authData = userAccount.userLogin(username,password);
            setAuthenticatedUser(authData);

        }catch (FirebaseException fe){
            fe.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /* *
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.*/
         private Boolean setAuthenticatedUser(AuthData authData) throws FirebaseException {
        Boolean status;

        if (authData != null) {
            status=true;
            userId = authData.getUid();
            //instead put complete class of user
            if (userId != null && (firebase.child("users").child(userId).child("username")==null)) {
                Map<String,String> map = new HashMap<>();
                map.put("username",userName);
                map.put("displayName",displayName);
                map.put("city",city);
                map.put("mob",mobileNo);
                map.put("provider","password");
                firebase.child("users").child(userId).setValue(map);
            }
        } else {
            status = false;
        }

        return status;
    }

    public Boolean createGroup(String groupName,String city, String desc,List<String> list){
Boolean status=false;
this.groupRef = this.firebase.child("groups");
        Map<String,String> map = new HashMap<>();
        map.put("name",groupName);
        map.put("desc",desc);
        map.put("city",city);
       // if(authData!=null)
        map.put("admin",userId);
        //else
          //  Intent intent = new Intent(this,Login.class);
        map.put("creation_date",userAccount.getCurrentDate());
        map.put("members",list.toString());
        if(authData!=null && this.groupRef!=null){
            this.firebase.child(groupRef.getKey()).push().setValue(map);
            status = true;
        }


return status;
    }

    public List<String> getGroups(){
        if(groupRef==null)
            groupRef = this.firebase.child("groups");
        final List<String> groupList = new ArrayList<>();

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot post:dataSnapshot.getChildren()){
                    groupList.add(post.getKey());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return groupList;
    }
}
