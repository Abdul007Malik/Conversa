package buddy.conversa;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import buddy.conversa.acitvityutility.Invite;
import firebase.ServerDataHandling;

public class InvitationActivity extends AppCompatActivity {
    Toolbar toolbar;
    ServerDataHandling sdHandler;
    Invite invite;
    Firebase firebase;
    ListView notfListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invitation);
        sdHandler = ServerDataHandling.getInstance();
        firebase = sdHandler.getFirebaseRef();

        toolbar = (Toolbar) findViewById(R.id.toolbarInv);
        notfListView = (ListView) findViewById(R.id.notif_ListView);

        if(firebase.getAuth()==null){
            Toast.makeText(this,"Login First",Toast.LENGTH_LONG).show();
        }else {
            initToolbar();
            getInvitations();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.general_menu, menu);
        return true;
    }

    public void clickHandlerContact(View target){
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(this, "Help", Toast.LENGTH_LONG);
                return true;
            case R.id.action_settings:
                Toast.makeText(this, "Action Settings", Toast.LENGTH_LONG);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
    public void initToolbar(){
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Invitation");
        getSupportActionBar().setLogo(R.drawable.ic_invitation);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }
    private class MyCustomAdapter extends ArrayAdapter<Invite> {
        private List<Invite> inviteList;
        private Context context;



        public MyCustomAdapter(Context context,int textViewResourceId,List<Invite> list) {
            super(context,textViewResourceId,list);
            this.inviteList = list;
            this.context = context;
        }

        private class ViewHolder {
            public TextView textl;
            public TextView text2;
            public Button btnAccept;
            public Button btnDecline;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                convertView = inflater.inflate(R.layout.custom_list_layout_invitation, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.textl = (TextView)convertView.findViewById(R.id.fromUserName);
                viewHolder.text2 = (TextView)convertView.findViewById(R.id.inviteId);
                viewHolder.btnDecline = (Button)convertView.findViewById(R.id.btnDecline);
                viewHolder.btnAccept = (Button)convertView.findViewById(R.id.btnAccept);
                convertView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            final Invite invite = inviteList.get(position);
            holder.textl.setText(invite.getId());

            //Handle buttons and add onClickListeners


            holder.btnDecline.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    declineInvite(invite.getId());
                    MyCustomAdapter.this.inviteList.remove(position);
                    notifyDataSetChanged();
                }
            });
            holder.btnAccept.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    acceptInvite(invite.getId());
                    MyCustomAdapter.this.inviteList.remove(position);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }


    public void acceptInvite(String inviteId){

        final Firebase userRef = firebase.child("users").child(firebase.getAuth().getUid());
         userRef.child("invites").child(inviteId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Invite invite = dataSnapshot.getValue(Invite.class);
                if(invite!=null) {
                    InvitationActivity.this.invite = invite;
                    enterRoom(invite.getGroupId());
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                try {
                    showDialog("Error",firebaseError.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }




    public void declineInvite(String inviteId){
        final Firebase userRef = firebase.child("users").child(firebase.getAuth().getUid());
        userRef.child("invites").child(inviteId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Invite invite = dataSnapshot.getValue(Invite.class);
                if(invite!=null){
                    userRef.child("invites").child(invite.getId()).setValue(null);

                    Map<String,Object> map = new HashMap<>();

                    // This statement will remove its entry from the group of the notification
                    map.put(firebase.getAuth().getUid(),null);
                    firebase.child("groups").child(invite.getGroupId()).child("members").updateChildren(map);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                try {
                    showDialog("Error",firebaseError.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


    public void enterRoom(final String roomId){

        final Firebase userRef = firebase.child("users").child(firebase.getAuth().getUid());

        userRef.child("groups").push().setValue(roomId, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                //it will delete the invitation from the server
                userRef.child("invites").child(invite.getId()).setValue(null);

            }
        });

    }



    public void getInvitations() {

        Firebase inviteRef = this.firebase.child("users").child(firebase.getAuth().getUid()).child("invite");

        inviteRef.addValueEventListener(new ValueEventListener() {
            List<Invite> inviteList = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()) {

                    Invite invite = post.getValue(Invite.class);
                    inviteList.add(invite);



                }
                ArrayAdapter<Invite> adapter =
                        new MyCustomAdapter(InvitationActivity.this,R.layout.custom_list_layout_invitation,inviteList);
                notfListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                try {
                    showDialog("Error",firebaseError.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


/*Show dialog */

    private void showDialog(String title,String message) throws Exception{
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_alert)
                .show();
    }
}
