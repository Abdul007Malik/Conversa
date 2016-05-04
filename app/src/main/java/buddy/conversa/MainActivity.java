package buddy.conversa;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import buddy.conversa.chat.ChatBubbleActivity;
import firebase.ConnectionDetector;
import firebase.ServerDataHandling;
import firebase.UserAccount;
import sqliteDB.Group;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listView;
    private ImageView imageEmpty;
    private Boolean status;

    private static final String TAG = "MainActivity";
    private ServerDataHandling sdHandler;
    private ConnectionDetector check;
    private List<Group> groupList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        check = new ConnectionDetector(this);
        check.isConnectingToInternet();

        sdHandler = ServerDataHandling.getInstance();
        initToolBar();
        listenToGroupList();

        groupList = sdHandler.getGroupList();
        listView = (ListView) findViewById(R.id.mainListView);
        imageEmpty = (ImageView) findViewById(R.id.imageEmpty);
        if(sdHandler!=null && groupList!=null){
        ArrayAdapter<Group> arrayAdapter = new ArrayAdapterForGroupList
                        (this,R.layout.custom_activity_grouplist,groupList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Group group = (Group) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this,ChatBubbleActivity.class);
                intent.putExtra("groupDetails",group);
                startActivity(intent);
            }
        });
        listView.setEmptyView(imageEmpty);
        }else
            Toast.makeText(this,"Something is not right",Toast.LENGTH_LONG);

        Log.v(TAG,"All info");

    }


    public void initToolBar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbarTitle);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

    }

    public void performLogin(){

        Intent intent = new Intent(this,LoginActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == 1
                && data.getExtras() != null
                && data.getExtras().containsKey("flag")
                && data.getExtras().getBoolean("flag")) {
            listenToGroupList();
        }else{

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Error")
                    .setMessage("You are not logged in yet!!")
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            performLogin();
                        }
                    }).create().show();
        }
    }


    public void listenToGroupList(){
        status = false;

        MyAsyncTask task = new MyAsyncTask(MainActivity.this
                , new MyAsyncTask.AsyncResponse() {
            @Override
            public void processFinish(boolean asyncStatus, ServerDataHandling sdHandlerOutput) {
                sdHandler = sdHandlerOutput;
                status = asyncStatus;
            }
        });
        try {
            task.execute("main activity").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(!status){
            Toast.makeText(this,"Server is down please try after sometime",Toast.LENGTH_LONG);
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
                viewHolder.textl= (TextView) rowView.findViewById(R.id.firstLine);
                viewHolder.text2 = (TextView) rowView.findViewById(R.id.secondLine);
                viewHolder.image = (ImageView) rowView
                        .findViewById(R.id.icon);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            Group group = groups.get(position);
            holder.textl.setText(group.getGroupName());
            holder.textl.setText(group.getDesc());

            return rowView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.login);
        if(sdHandler.userAccount.getFirebaseRef()!=null && sdHandler.userAccount.getFirebaseRef().getAuth()!=null)
            item.setEnabled(false);
        else
            item.setEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.contacts:
                Intent intent = new Intent(this,ContactListActivity.class);
                startActivity(intent);
                return true;
            case R.id.createGroup:
                Intent intent1 = new Intent(this,CreateGroupActivity.class);
                startActivity(intent1);
                return true;
            case R.id.notfication:
                Intent intent2 = new Intent(this,InvitationActivity.class);
                startActivity(intent2);
                return true;
            case R.id.refresh:
                listenToGroupList();
                return status;
            case R.id.login:
                performLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
