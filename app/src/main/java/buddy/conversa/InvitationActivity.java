package buddy.conversa;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import buddy.conversa.acitvityutility.Invite;
import firebase.ServerDataHandling;

public class InvitationActivity extends AppCompatActivity {
    Toolbar toolbar;
    ServerDataHandling sdHandler = ServerDataHandling.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invitation);
        toolbar = (Toolbar) findViewById(R.id.toolbarInv);
        initToolbar();
        ArrayAdapter<Invite> adapter = new MyCustomAdapter(this,R.layout.custom_list_layout_invitation,sdHandler.getInvitations());

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
                    sdHandler.declineInvite(invite.getId());
                    notifyDataSetChanged();
                }
            });
            holder.btnAccept.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    sdHandler.acceptInvite(invite.getId());
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }
}
