package buddy.conversa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ContactListActivity extends AppCompatActivity {
    Button btnGroup,btnSearchContact;
    ListView listViewContact;
    EditText contactNameForSearch;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);

        toolbar = (Toolbar) findViewById(R.id.toolbarContact);
        initToolbar();

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
                Toast.makeText(this,"Help",Toast.LENGTH_LONG);
                return true;
            case R.id.action_settings:
                Toast.makeText(this,"Action Settings",Toast.LENGTH_LONG);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }


    public void initToolbar(){
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contacts");
        getSupportActionBar().setLogo(R.drawable.ic_contacts);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }
}
