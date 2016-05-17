package buddy.conversa;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

import buddy.conversa.acitvityutility.Invitation;
import firebase.ServerDataHandling;

public class InvitationActivity extends AppCompatActivity {
    private static final String TAG = InvitationActivity.class.getSimpleName() ;
    Toolbar toolbar;
    ServerDataHandling sdHandler;
    Invitation invite;
    Firebase firebase;
    ListView notfListView;
    private ImageView imageEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invitation);
        sdHandler = ServerDataHandling.getInstance();
        firebase = sdHandler.getFirebaseRef();

        imageEmpty = (ImageView) findViewById(R.id.imageEmptyInv);
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
    private class MyCustomAdapter extends ArrayAdapter<Invitation> {
        private List<Invitation> inviteList;
        private Context context;



        public MyCustomAdapter(Context context,int textViewResourceId,List<Invitation> list) {
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
            final Invitation invite = inviteList.get(position);
            holder.textl.setText(invite.getId());

            //Handle buttons and add onClickListeners


            holder.btnDecline.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    try {
                        declineInvite(invite.getId());
                        MyCustomAdapter.this.inviteList.remove(position);
                        notifyDataSetChanged();
                    }catch (Exception e){
                        Log.e(TAG,"exception",e);
                    }
                }
            });
            holder.btnAccept.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    try {
                        acceptInvite(invite.getId());
                        MyCustomAdapter.this.inviteList.remove(position);
                        notifyDataSetChanged();
                    }catch (Exception e){
                        Log.e(TAG,"exception",e);
                    }
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
                Invitation invite = dataSnapshot.getValue(Invitation.class);
                try {
                    InvitationActivity.this.invite = invite;
                    enterRoom(invite.getGroupId());
                }catch (Exception e){
                    Log.e(TAG,"exception",e);
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                try {
                    showDialog("Error",firebaseError.getMessage());
                } catch (Exception e) {
                    Log.e(TAG,"exception",e);
                }

            }
        });
    }




    public void declineInvite(String inviteId){
        final Firebase userRef = firebase.child("users").child(firebase.getAuth().getUid());
        userRef.child("invites").child(inviteId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Invitation invite = dataSnapshot.getValue(Invitation.class);
                try {
                    userRef.child("invites").child(invite.getId()).setValue(null);

                    Map<String, Object> map = new HashMap<>();

                    // This statement will remove its entry from the group of the notification
                    map.put(firebase.getAuth().getUid(), null);
                    firebase.child("groups").child(invite.getGroupId()).child("members").updateChildren(map);

                }catch (Exception e){
                    Log.e(TAG,"exception",e);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG,"exception",firebaseError.toException());
                try {
                    showDialog("Error",firebaseError.getMessage());
                } catch (Exception e) {
                    Log.e(TAG,"exception",e);
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
                if(firebaseError!=null){
                    Log.e(TAG,"exception",firebaseError.toException());
                    Toast.makeText(InvitationActivity.this,firebaseError.getMessage(),Toast.LENGTH_LONG);
                }else {
                    userRef.child("invites").child(invite.getId()).setValue(null);
                }
            }
        });

    }



    public void getInvitations() {

        final Firebase inviteRef = this.firebase.child("users").child(firebase.getAuth().getUid()).child("invite");

        inviteRef.addValueEventListener(new ValueEventListener() {
            List<Invitation> inviteList = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot post : dataSnapshot.getChildren()) {

                        Invitation invite = post.getValue(Invitation.class);
                        inviteList.add(invite);

                    }
                    if (inviteList != null) {
                        ArrayAdapter<Invitation> adapter =
                                new MyCustomAdapter(InvitationActivity.this, R.layout.custom_list_layout_invitation, inviteList);
                        notfListView.setAdapter(adapter);
                    } else {
                        notfListView.setEmptyView(imageEmpty);
                    }
                }catch (Exception e){
                    Log.e(TAG,"exception",e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                try {
                    Log.e(TAG,"exception",firebaseError.toException());
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
