package buddy.conversa;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import buddy.conversa.acitvityutility.Invite;
import buddy.conversa.acitvityutility.MyCustomAdapter;
import firebase.ServerDataHandling;
import sqliteDB.MyDB;

public class InviteActivity extends AppCompatActivity {
    //ClickListener clickListener;
    List<Invite> inviteList;
    MyCustomAdapter adapter;
    Button btnInvite;
    final static String TAG = InviteActivity.class.getSimpleName();


    ServerDataHandling sdHandler;
    Firebase firebase;
    List<String> friendList;
    Map<String,String> map;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.invite_activity);
            sdHandler = ServerDataHandling.getInstance();
            firebase = new Firebase("https://conversa.firebaseIO.com");
            getFriends();
            //Generate list View from ArrayList
            btnInvite = (Button) findViewById(R.id.btnInvite);


        }catch(Exception e){
            Log.e(TAG,e.getMessage(),e);
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED,resultIntent);
            finish();
        }


    }


    private void displayListView() throws Exception{
        inviteList = new ArrayList<>();
        Iterator <String> iterator = friendList.listIterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            Invite invite = new Invite(map.get(str), str, false);
            inviteList.add(invite);
        }
        adapter = new MyCustomAdapter(InviteActivity.this,
                    R.layout.custom_list_layout, inviteList);

            // Assign adapter to ListView
        listView.setAdapter(adapter);

    }


    public void checkButtonClick(View v) {
        try { ArrayList<String> list = new ArrayList<>();
            boolean flag = false;
            Intent resultIntent = new Intent();
            if(v.getId() == R.id.btnInvite) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Invite> invites = adapter.getfrndList();

                for(int i=0;i<invites.size();i++)
                {
                    Invite invite = invites.get(i);

                    if(invite.isSelected())
                    {   flag = true;
                        list.add(invite.getCode());
                        responseText.append("\n" + invite.getName());
                    }
                }

                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();

                if (!flag) {
                    Toast.makeText(getBaseContext(), "Atleast invite to one friend", Toast.LENGTH_SHORT).show();

                } else {

                    // This code is due to its(this activity) child nature.

                    resultIntent.putStringArrayListExtra("inviteList",list);
                    resultIntent.putExtra("noFriend", false);
                    setResult(RESULT_OK, resultIntent);
                    finish();

                }


            }
        } catch (Exception e) {
            Intent intent = getIntent();
            setResult(RESULT_CANCELED, intent);
            finish();
        Log.e(TAG, "exception", e);
    }


    }

    public void getFriends(){
        friendList = new ArrayList<>();
        map = new HashMap<>();
        firebase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot post : dataSnapshot.getChildren()) {

                        MyDB my = post.getValue(MyDB.class);
                        if(firebase.getAuth().getUid()!=my.getId()) {
                            friendList.add(my.getUsername());
                            map.put(my.getUsername(),my.getId());
                        }


                    }

                        if (friendList == null || friendList.size() == 0) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("flag", true);
                            resultIntent.putExtra("noFriend", true);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            listView = (ListView) findViewById(R.id.friendListView);

                            listView.setItemsCanFocus(false);
                            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                            //handler.sendEmptyMessage(0);
                            friendList = new ArrayList<>(new HashSet<>(friendList));

                            displayListView();
                            /*dataAdapter = new MyCustomAdapter(InviteActivity.this,
                                    R.layout.custom_list_layout, friendList);*/


                        }


                }catch (FirebaseException fe){
                    Log.e(TAG,"exception",fe);
                    Intent intent = getIntent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }catch (Exception e){
                Log.e(TAG,"exception",e);
                    Intent intent = getIntent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
            }
            }





            @Override
            public void onCancelled(FirebaseError firebaseError) {

                try {
                    Toast.makeText(InviteActivity.this,firebaseError.getMessage(),Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e(TAG,e.getMessage(),e);
                }
                Intent intent = getIntent();
                setResult(RESULT_CANCELED, intent);
                finish();

            }

        });
    }


}
