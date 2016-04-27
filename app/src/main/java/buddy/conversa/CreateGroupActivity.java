package buddy.conversa;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import firebase.ServerDataHandling;
import sqliteDB.Group;

public class CreateGroupActivity extends AppCompatActivity {

    Button btnGroups;
    EditText groupName;
    EditText city;
    EditText desc;

    Group group;
    Boolean status;
    ServerDataHandling sdHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        group = new Group();
        btnGroups =(Button) findViewById(R.id.btnGroups);
        btnGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction();
            }
        });
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
                public void processFinish(Boolean status, ServerDataHandling sdHandlerOutput) {
                    sdHandler = sdHandlerOutput;
                    CreateGroupActivity.this.status = status;
                }
            }
                    ,group);
            task.execute("createGroup");

            if(!status && sdHandler.userAccount.getMessage().equals("login first")){

                Intent intent = new Intent(this,LoginActivity.class);
                startActivityForResult(intent,2);
            }
            else if(!status){
                try {
                    showErrorDialog("OOPS!!\n" + sdHandler.userAccount.getMessage());
                } catch (Exception e) {

                    e.printStackTrace();
                    }
            } else {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        }

        }
        else if(resultCode == RESULT_OK && requestCode == 2
                && data.getExtras()!=null
                &&data.getExtras().containsKey("flag")
                && data.getExtras().getBoolean("flag")){

            }


    }

    public void doAction(){
   groupName = (EditText) findViewById(R.id.groupName);
    city = (EditText) findViewById(R.id.groupCity);
    desc = (EditText) findViewById(R.id.desc);

    group.setGroup_name(groupName.getText().toString());
    group.setGroup_city(city.getText().toString());
    group.setDescription(desc.getText().toString());

    Intent intent = new Intent(this,InviteActivity.class);
    startActivityForResult(intent,1);


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