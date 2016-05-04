package buddy.conversa;

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

import firebase.ServerDataHandling;
import sqliteDB.Group;

public class CreateGroupActivity extends AppCompatActivity {

    Button createButton;
    EditText groupName;
    EditText city;
    EditText desc;
    Toolbar toolbar;

    Group group;
    boolean status;
    ServerDataHandling sdHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
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
        getSupportActionBar().setTitle("New Group");
        getSupportActionBar().setLogo(R.drawable.ic_contacts);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 1
                && data.getExtras()!=null
                &&data.getExtras().containsKey("flag")
                && data.getExtras().getBoolean("flag")){
            MyAsyncTask task = new MyAsyncTask(CreateGroupActivity.this
                    , new MyAsyncTask.AsyncResponse() {
                @Override
                public void processFinish(boolean status, ServerDataHandling sdHandlerOutput) {
                    sdHandler = sdHandlerOutput;
                    CreateGroupActivity.this.status = status;
                }
            }
                    ,group);
            task.execute("createGroup");
            if(!status){

                try {
                    showErrorDialog("OOPS!!\n" + sdHandler.userAccount.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("flag",true);
                setResult(RESULT_OK,resultIntent);
                finish();
        }

        }


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
            startActivityForResult(intent, 1);

        }
}

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) throws Exception{
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
