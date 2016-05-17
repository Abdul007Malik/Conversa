package buddy.conversa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.Iterator;
import java.util.List;

import buddy.conversa.acitvityutility.Invitation;
import firebase.ServerDataHandling;
import sqliteDB.Group;
import sqliteDB.MyDB;

public class CreateGroupActivity extends AppCompatActivity {

    Button createButton;
    EditText groupName;
    EditText city;
    EditText desc;
    private final static String TAG = LoginActivity.class.getSimpleName();
    Toolbar toolbar;

    Group group;
    //boolean createGroupStatus;
    ServerDataHandling sdHandler;
    Firebase firebase;
    
    private static final int INVITE_ACTIVITY = 11;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        try {
            sdHandler = ServerDataHandling.getInstance();
            firebase = new Firebase(sdHandler.getFirebaseURL());

            toolbar = (Toolbar) findViewById(R.id.toolbarGrp);

            initToolbar();
            createButton = (Button) findViewById(R.id.createButton);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doAction();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

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
        try {
            toolbar.showOverflowMenu();
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("New Group");
            getSupportActionBar().setLogo(R.drawable.ic_contacts);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }catch (Exception e){
            Log.e(TAG,"exception",e);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
        if(requestCode ==RESULT_CANCELED && requestCode ==INVITE_ACTIVITY){
            try {
                Toast.makeText(CreateGroupActivity.this,"Invitation cannot be sended but will create your group for you",Toast.LENGTH_LONG).show();
                returnResult(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            createGroup(groupName.getText().toString()
                    ,city.getText().toString(),desc.getText().toString());
        }
        if(resultCode == RESULT_OK && requestCode == INVITE_ACTIVITY
                && data.getExtras()!=null
                && data.getExtras().containsKey("noFriend")
                && !data.getExtras().getBoolean("noFriend")
                ){

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Creating Group");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

            List<String> list = data.getStringArrayListExtra("inviteList");
            System.out.println("1"+list);
            createGroup(groupName.getText().toString()
            ,city.getText().toString(),desc.getText().toString(),list);


        }else if(resultCode == RESULT_OK && requestCode == INVITE_ACTIVITY
                && data.getExtras()!=null
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
                        , city.getText().toString(), desc.getText().toString());

        }

    }catch (NullPointerException ne){
        ne.printStackTrace();
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
        try{
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

                Intent intent = new Intent(this, InviteActivity.class);
                startActivityForResult(intent, INVITE_ACTIVITY);
            }

        }catch(Exception e){
            Log.e(TAG,"exception",e);
        }

}

    
      /*This method create group with no friends*/

    public void createGroup (  String groupName, final String groupCity, String desc ) throws NullPointerException {
        try{
        final Firebase groupRef;
        if(firebase.getAuth() == null) {
            Toast.makeText(this,"Login First",Toast.LENGTH_LONG).show();
            returnResult(false);
        }else {
            groupRef = this.firebase.child("groups").push();
            List<String> list = new ArrayList<>();
            list.add(firebase.getAuth().getUid());
            Log.d(TAG,list.toString());
            final Group group = new Group(groupRef.getKey(), groupName, groupCity
                        , desc, firebase.getAuth().getUid(), sdHandler.getCurrentDate()
                        , list, list.size());

            groupRef.setValue(group, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, final Firebase firebase) {
                    if (firebaseError != null) {
                        try {
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                            Toast.makeText(CreateGroupActivity.this,firebaseError.getMessage(),Toast.LENGTH_LONG).show();
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
                                                                Toast.makeText(CreateGroupActivity.this,firebaseError.getMessage(),Toast.LENGTH_LONG).show();
                                                                } catch (Exception e) {
                                                                if(progressDialog.isShowing())
                                                                    progressDialog.dismiss();
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
                                                if(progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                                Toast.makeText(CreateGroupActivity.this,firebaseError.getMessage(),Toast.LENGTH_LONG).show();
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
        }catch (FirebaseException fe){
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            System.out.print(fe.getMessage());

        }
    }

    private void rollback() {
    }


/* Create Group with friends*/
    public void createGroup(String groupName, final String groupCity
            , String desc, final List<String> memberList ) throws NullPointerException{
        try{
        if(firebase.getAuth() == null) {
            Toast.makeText(this,"Login First",Toast.LENGTH_LONG).show();
        }else {

            memberList.add(firebase.getAuth().getUid());
            final Firebase groupRef = this.firebase.child("groups").push();
            final Group group = new Group(groupRef.getKey(), groupName, groupCity
                        , desc, firebase.getAuth().getUid(), sdHandler.getCurrentDate()
                        , memberList, memberList.size());
            System.out.println(group.getMembersRegistered());

            groupRef.setValue(group, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, final Firebase firebase) {
                    if (firebaseError != null) {
                        try {
                            Toast.makeText(CreateGroupActivity.this,firebaseError.getMessage(),Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        returnResult(false);
                        }
                    else {
                        memberList.remove(firebase.getAuth().getUid());
                        getUserGroupList(groupRef.getKey(),memberList);
                    }
                }
            });
        }
        }catch (FirebaseException fe){
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            System.out.print(fe.getMessage());
        }
    }



    public void sendInvitationToFriends(final String uid, final String userName
            , final String groupId, final List<String> list) throws FirebaseException{
            System.out.println(list);
        Invitation invite = new Invitation(uid, userName, groupId, false);
        Iterator<String> iterator = list.listIterator();
        while(iterator.hasNext()){
            firebase = sdHandler.getFirebaseRef();
            Firebase inviteRef = firebase.child("users").child(iterator.next()).child("invite").push();
            invite.setId(inviteRef.getKey());
            inviteRef.setValue(invite, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if(firebaseError!=null){
                        Log.d(TAG,firebaseError.getMessage(),firebaseError.toException());
                    }
                    else
                    Log.d(TAG,"invitation sent");
                }
            });

        }





    }


    public void getUserGroupList(final String key, final List<String> memberList){
        firebase = sdHandler.getFirebaseRef();
        final List<String> list = new ArrayList<>();
        firebase.child("users").child(firebase.getAuth().getUid()).child("groups")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> userGroupList ;
                        GenericTypeIndicator<List<String>> t =
                                new GenericTypeIndicator<List<String>>() {};
                        userGroupList = dataSnapshot.getValue(t);
                        System.out.print("2"+userGroupList);
                        if(userGroupList == null)
                            userGroupList = new ArrayList<>();
                        userGroupList.add(key);
                        list.addAll(userGroupList);

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        rollback();
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        returnResult(false);
                    }
                });
        firebase.child("users").child(firebase.getAuth().getUid())
                .child("groups").setValue(list, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError!=null){
                    try {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(CreateGroupActivity.this,firebaseError.getMessage(),Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    rollback();
                }
                else{
                    sendInvitationToFriends(firebase.getAuth().getUid(), sdHandler.myInfo.getUsername(), key ,memberList);
                    returnResult(true);
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    
}
