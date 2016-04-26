package buddy.conversa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import sqliteDB.Group;

public class CreateGroupActivity extends AppCompatActivity {

    Button btnGroups;
    EditText groupName;
    EditText city;
    EditText desc;

    Group group;



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


public void doAction(){
   groupName = (EditText) findViewById(R.id.groupName);
    city = (EditText) findViewById(R.id.groupCity);
    desc = (EditText) findViewById(R.id.desc);

    group.setGroup_name(groupName.getText().toString());
    group.setGroup_city(city.getText().toString());
    group.setDescription(desc.getText().toString());

    Intent intent = new Intent(this,InviteActivity.class);
    startActivity(intent);





}
}