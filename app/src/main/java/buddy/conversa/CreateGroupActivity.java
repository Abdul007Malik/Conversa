package buddy.conversa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import buddy.conversa.acitvityutility.Invite;
import buddy.conversa.acitvityutility.UserNameInviteList;
import firebase.ServerDataHandling;
import sqliteDB.Group;
import sqliteDB.MyDB;

public class CreateGroupActivity extends AppCompatActivity {

    Button createButton;
    EditText groupName;
    EditText city;
    EditText desc;
    Toolbar toolbar;

    Group group;
    boolean createGroupStatus;
    ServerDataHandling sdHandler;
    Firebase firebase;
    
    private static final int INVITE_ACTIVITY = 11;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        
        sdHandler = ServerDataHandling.getInstance();
        firebase = sdHandler.getFirebaseRef();
        
        group = new Group();
        toolbar = (Toolbar) findViewById(R.id.toolbarGrp);
        initToolbar();
        createButton =(Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.general_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(this, "Help", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(this, "Action Settings", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    public void initToolbar(){
        try{
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Group");
        getSupportActionBar().setLogo(R.drawable.ic_contacts);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode ==RESULT_CANCELED && requestCode ==INVITE_ACTIVITY){
            try {
                showErrorDialog("Invitation cannot be sended but will create your group for you");
            } catch (Exception e) {
                e.printStackTrace();
            }
            createGroup(groupName.getText().toString()
                    ,city.getText().toString(),desc.getText().toString());
        }
        if(resultCode == RESULT_OK && requestCode == INVITE_ACTIVITY
                && data.getExtras()!=null
                && data.getExtras().containsKey("flag")
                && data.getExtras().getBoolean("flag")
                && data.getExtras().containsKey("noFriend")
                && !data.getExtras().getBoolean("noFriend")
                ){

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Creating Group");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

            List<String> list = UserNameInviteList.getInstance().getUserNames();
            createGroup(groupName.getText().toString()
            ,city.getText().toString(),desc.getText().toString(),list);

        } else if(resultCode == RESULT_OK && requestCode == INVITE_ACTIVITY
                && data.getExtras()!=null
                && data.getExtras().containsKey("flag")
                && data.getExtras().getBoolean("flag")
                && data.getExtras().containsKey("noFriend")
                && data.getExtras().getBoolean("noFriend")
                ){
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Creating Group");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
            createGroup(groupName.getText().toString()
                    ,city.getText().toString(),desc.getText().toString());

        }

    }

    void returnResult(Boolean val){
        Intent intent = new Intent();
        if(val)
        setResult(RESULT_OK,intent);
        else
        setResult(RESULT_CANCELED,intent);
        finish();
    }


    public void doAction(){

   groupName = (EditText) findViewById(R.id.groupName);
    city = (EditText) findViewById(R.id.groupCity);
    desc = (EditText) findViewById(R.id.desc);
        if (groupName.getText().toString().length() == 0
                || groupName.getText().toString().length() > 20) {
            groupName.setError("Specify Group Name and not more than 20 characters");
        } else if (desc.getText().toString().length() == 0) {
            desc.setError("Specify the Description");
        } else if (city.getText().toString().length() == 0) {
            city.setError("Specify the City");
        } else {

            group.setGroupName(groupName.getText().toString());
            group.setGroupCity(city.getText().toString());
            group.setDesc(desc.getText().toString());

            Intent intent = new Intent(this, InviteActivity.class);
            startActivityForResult(intent, INVITE_ACTIVITY);

        }
}

    
      /*This method create group with no friends*/

    public void createGroup(
            String groupName, final String groupCity, String desc ) throws FirebaseException {

        final Firebase groupRef;
        if(firebase.getAuth() == null) {
            Toast.makeText(this,"Login First",Toast.LENGTH_LONG).show();
            returnResult(false);
        }else {
            groupRef = this.firebase.child("groups").push();
            
            final Group group = new Group(groupRef.getKey(), groupName, groupCity
                        , desc, firebase.getAuth().getUid(), sdHandler.getCurrentDate()
                        , null, 1);

            groupRef.setValue(group, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, final Firebase firebase) {
                    if (firebaseError != null) {
                        try {
                            showErrorDialog(firebaseError.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        returnResult(false);
                        } 
                    else {
                        firebase.child("users").child(firebase.getAuth().getUid()).child("groups")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                        
                                            GenericTypeIndicator<List<String>> t =
                                                    new GenericTypeIndicator<List<String>>() {
                                                    };

                                            List<String> userGroupList = null;
                                            userGroupList = dataSnapshot.getValue(t);
                                            userGroupList.add(groupRef.getKey());
                                            firebase.child("users").child(firebase.getAuth()
                                                    .getUid()).child("groups")
                                                    .setValue(userGroupList, new Firebase.CompletionListener() {
                                                        @Override
                                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                            if(firebaseError!=null)
                                                            {   try {
                                                                    showErrorDialog(firebaseError.getMessage());
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                                rollback();
                                                            
                                                            }
                                                            else{
                                                                returnResult(true);
                                                            }
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                            try {
                                                showErrorDialog(firebaseError.getMessage());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            returnResult(false);
                                        }
                                });
                    }
                }
            });
        }
    }

    private void rollback() {
    }


/* Create Group with friends*/
    public void createGroup(String groupName, final String groupCity
            , String desc, List<String> memberList ) throws FirebaseException{

        if(firebase.getAuth() == null) {
            Toast.makeText(this,"Login First",Toast.LENGTH_LONG).show();
        }else {
            final Firebase groupRef = this.firebase.child("groups").push();
            final Group group = new Group(groupRef.getKey(), groupName, groupCity
                        , desc, firebase.getAuth().getUid(), sdHandler.getCurrentDate()
                        , memberList, memberList.size());

            groupRef.setValue(group, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, final Firebase firebase) {
                    if (firebaseError != null) {
                        try {
                            showErrorDialog(firebaseError.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        returnResult(false);
                        }
                    else {
                        firebase.child("users").child(firebase.getAuth().getUid()).child("groups")
                          .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<String> userGroupList ;
                                GenericTypeIndicator<List<String>> t =
                                        new GenericTypeIndicator<List<String>>() {};
                                userGroupList = dataSnapshot.getValue(t);
                                userGroupList.add(groupRef.getKey());
                                firebase.child("users").child(firebase.getAuth().getUid())
                                  .child("groups").setValue(userGroupList, new Firebase.CompletionListener() {
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                        if(firebaseError!=null){
                                            try {
                                                showErrorDialog(firebaseError.getMessage());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            rollback();
                                        }
                                        else{
                                            sendInvitationToFriends(firebase.getAuth().getUid(), sdHandler.myInfo.getUsername(), groupRef.getKey());
                                            returnResult(true);
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                rollback();
                                returnResult(false);
                            }
                        });
                    }
                }
            });
        }
    }



    public void sendInvitationToFriends(final String uid, final String userName, final String groupId){


        final UserNameInviteList userNameInviteList = UserNameInviteList.getInstance();

        final Firebase userRef = this.firebase.child("users");
        userRef.addValueEventListener(new ValueEventListener() {
            List<String> idList = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    MyDB userInfo = postSnapshot.getValue(MyDB.class);
                    if(userNameInviteList.getUserNames().contains(userInfo.getUsername())) {

                        idList.add(postSnapshot.getKey());
                        Invite invite = new Invite(uid,userName,groupId,false);

                        //to remove dublicate items
                        idList = new ArrayList<>(new HashSet<>(idList));
                        for(String list : idList) {
                            Firebase inviteRef = firebase.child("users").child(list).child("invites").push();
                            invite.setId(inviteRef.getKey());
                            inviteRef.setValue(invite, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if(firebaseError!=null){
                                        try {
                                            showErrorDialog(firebaseError.getMessage());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            });

                        }

                        userNameInviteList.setUserNameList(null);

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                try {
                    showErrorDialog(firebaseError.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) throws Exception{
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
    
}
