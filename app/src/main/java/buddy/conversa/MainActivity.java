package buddy.conversa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import buddy.conversa.chat.ChatBubbleActivity;
import buddy.conversa.acitvityutility.ConnectionDetector;
import firebase.ServerDataHandling;
import sqliteDB.Group;
import sqliteDB.MyDB;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listView;
    private ImageView imageEmpty;
    ProgressDialog progressDialog;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CREATE_GROUP_INTENT = 10;
    private static final int LOGIN_INTENT = 1;
    private ServerDataHandling sdHandler;
    private ConnectionDetector check;
    Firebase firebase;


    private List<Group> groupList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        try{
            sdHandler = ServerDataHandling.getInstance();
            firebase = sdHandler.getFirebaseRef();
            check = new ConnectionDetector(this);
            check.isConnectingToInternet();
            imageEmpty = (ImageView) findViewById(R.id.imageEmpty);


            initToolBar();
            if(firebase.getAuth()!=null){
                getMyInfo();
                listenToGroupList();
            }else{
            Toast.makeText(this,"Login First",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Log.e(TAG,"exception",e);
        }

    }


    public void initToolBar() throws Exception{

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbarTitle);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.contacts:
                intent = new Intent(this,ContactListActivity.class);
                startActivity(intent);
                return true;
            case R.id.createGroup:
                intent = new Intent(this,CreateGroupActivity.class);
                startActivityForResult(intent,CREATE_GROUP_INTENT);
                return true;
            case R.id.notfication:
                intent = new Intent(this,InvitationActivity.class);
                startActivity(intent);
                return true;
            case R.id.refresh:
                intent = getIntent();
                finish();
                startActivity(intent);
                listenToGroupList();
                return true;
            case R.id.login:
                performLogin();
                return true;
            case R.id.logout:
                sdHandler.logout(sdHandler.getFirebaseRef().getAuth());
                intent = getIntent();
                finish();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void performLogin(){

        Intent intent = new Intent(this,LoginActivity.class);
        startActivityForResult(intent,LOGIN_INTENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
        if (resultCode == RESULT_OK && requestCode == LOGIN_INTENT) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }else if (resultCode == RESULT_CANCELED && requestCode == LOGIN_INTENT){
            sdHandler.logout(firebase.getAuth());
            showDialog("Error","You are not logged in yet!!");

        }
        if (resultCode == RESULT_OK && requestCode == CREATE_GROUP_INTENT){
            try{
                showDialog("Congrats","Group Created");
            }catch (Exception e){
                e.printStackTrace();
            }
            Intent refresh = getIntent();
            finish();
            startActivity(refresh);

        }else if (resultCode == RESULT_CANCELED && requestCode == CREATE_GROUP_INTENT){
            showDialog("Sorry","Group cannot be Created");

        }


        }catch (Exception e){
            Log.e(TAG,"exception",e);
        }
    }


    public void listenToGroupList(){

        groupList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Searching for Groups");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();

        timerDelayRemoveDialog(10000,progressDialog);
        Firebase groupRef = this.firebase.child("groups");
        if(groupRef!=null){
            //System.out.print(sdHandler.myInfo.getCity());
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{
                    for(DataSnapshot post : dataSnapshot.getChildren()) {

                        Group group = post.getValue(Group.class);
                        if(group!=null &&sdHandler.myInfo!=null &&sdHandler.myInfo.getCity().equals(group.getGroupCity()) ){
                            groupList.add(group);
                        }
                    }

                         progressDialog.dismiss();
                    displayGroupList();
                }catch (NullPointerException e){
                Log.e(TAG,"exception",e);
            }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                try {
                    showDialog("Error", firebaseError.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "exception", e);
                }
            }
        });
        }else{
            listView.setEmptyView(imageEmpty);
        }



    }

    public void getMyInfo(){
        firebase.child("users").child(firebase.getAuth().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MyDB info = dataSnapshot.getValue(MyDB.class);
                if(info!=null){
                    sdHandler.myInfo = info;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void displayGroupList() {
        try{

            if (sdHandler != null && groupList != null) {

                ArrayAdapter<Group> arrayAdapter = new ArrayAdapterForGroupList
                    (this, R.layout.custom_activity_grouplist, groupList);
                listView.setAdapter(arrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Group group = (Group) parent.getItemAtPosition(position);
                    Intent intent = new Intent(MainActivity.this, ChatBubbleActivity.class);
                    intent.putExtra("groupDetails", group);
                    startActivity(intent);
                }
            });

            }
        }catch(Exception e)
        {
            Log.e(TAG,"exception",e);
        }
    }


    private class ArrayAdapterForGroupList extends ArrayAdapter<Group> {
        private final Activity context;
        private final List<Group> groups;
        private int resourceId;

        private class ViewHolder {
            public TextView textl;
            public TextView text2;
            public ImageView image;
        }

        public ArrayAdapterForGroupList(Activity context, int resourceId, List<Group> groupList) {
            super(context, resourceId, groupList);
            this.context = context;
            this.resourceId = resourceId;
            this.groups = groupList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(resourceId, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.textl= (TextView) rowView.findViewById(R.id.firstLine);
            holder.text2 = (TextView) rowView.findViewById(R.id.secondLine);
            holder.image = (ImageView) rowView
                    .findViewById(R.id.icon);

            Group group = groups.get(position);
            holder.textl.setText(group.getGroupName());
            holder.text2.setText(group.getDesc());

            holder.image.setImageResource(R.drawable.ic_group);


            return rowView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.login);
        MenuItem item1 = menu.findItem(R.id.logout);
        if(sdHandler.getFirebaseRef().getAuth()!=null) {
            item.setEnabled(false);
            item1.setEnabled(true);
        }
       else {
            item.setEnabled(true);
            item1.setEnabled(false);
        }
        return true;
    }


    private void showDialog(String title,String message) throws Exception{
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_alert)
                .show();
    }


    public static void timerDelayRemoveDialog(long time, final ProgressDialog d){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(d.isShowing())
                d.dismiss();
            }
        }, time);
    }
}
