package buddy.conversa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import buddy.conversa.acitvityutility.UserNameInviteList;
import firebase.ServerDataHandling;
import sqliteDB.FriendListInfoDatabaseHandler;
import sqliteDB.MyDB;

public class InviteActivity extends AppCompatActivity {
    MyCustomAdapter dataAdapter = null;
    ListIterator<String> iterator;
    ClickListener clickListener;
    Map<String, Boolean> map;
    List<String> inviteList;
    Boolean status;
    ImageView image;


    ServerDataHandling sdHandler;
    Firebase firebase;
    List<String> friendList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        sdHandler = ServerDataHandling.getInstance();
        getFriends();
        //Generate list View from ArrayList
        Button btnInvite = (Button) findViewById(R.id.btnInvite);



    }


    private void displayListView() {

        if(friendList == null){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("flag",true);
            resultIntent.putExtra("noFriend",true);
            setResult(RESULT_OK,resultIntent);
            finish();
        }else{
            map = new HashMap<>();
            iterator = friendList.listIterator();

            //By default let all the friends in list is unchecked
            while (iterator.hasNext()) {
                map.put(iterator.next(), Boolean.FALSE);
            }


            //create an ArrayAdaptar from the String Array
            dataAdapter = new MyCustomAdapter(this,
                R.layout.custom_list_layout, friendList);
            ListView listView = (ListView) findViewById(R.id.friendListView);
            if (listView != null) {
                listView.setItemsCanFocus(false);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                // Assign adapter to ListView
                listView.setAdapter(dataAdapter);
                clickListener = new ClickListener(map);
                listView.setOnItemClickListener(clickListener);
                map = clickListener.getMap();

            }
        }

    }

    //Implements OnItemClickListener
    private class ClickListener implements AdapterView.OnItemClickListener {

        Map<String, Boolean> temp;

        public ClickListener(Map<String, Boolean> map) {
            this.temp = map;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CheckedTextView ctv = (CheckedTextView) view;
            if (ctv.isChecked())
                temp.put(ctv.getText().toString(), true);
            else
                temp.put(ctv.getText().toString(), false);
        }

        Map<String, Boolean> getMap(){
            return temp;
        }
    }


    //This class is used to set the Invite layout with the List of friends each setting
    // in a checkedtextview

    private class MyCustomAdapter extends ArrayAdapter<String> {

        private ArrayList<String> frndList;
        private int resourceId;
        Activity context;
        View rowView;

        public MyCustomAdapter(Activity context, int textViewResourceId,
                               List<String> list) {
            super(context, textViewResourceId, list);
            this.context = context;
            this.resourceId = textViewResourceId;
            this.frndList = new ArrayList<>();
            this.frndList.addAll(list);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            this.rowView = convertView;
            CheckedTextView name;

            if (rowView == null) {
                LayoutInflater vi = context.getLayoutInflater();
                rowView = vi.inflate(resourceId, null);
                name = (CheckedTextView) rowView.findViewById(R.id.checkboxTextView);
                rowView.setTag(name);
            }
            name = (CheckedTextView) rowView.getTag();
            name.setText(frndList.get(position));

            return convertView;

        }
    }

    public void checkButtonClick(View v) {

        Boolean flag = false;
        for(Boolean values: map.values()){
            if(!values){
                flag  = true;
            }
        }

        if(!flag)
        {
            Toast.makeText(getBaseContext(),"Atleast invite to one friend", Toast.LENGTH_SHORT).show();

        }else {


            UserNameInviteList userNameInviteList = UserNameInviteList.getInstance();
            inviteList = new ArrayList<>(getKeysByValue(map,true));
            userNameInviteList.setUserNameList(inviteList);
            // This code is due to its(this activity) child nature.
            Intent resultIntent = new Intent();
            resultIntent.putExtra("flag",true);
            resultIntent.putExtra("noFriend",false);
            setResult(RESULT_OK,resultIntent);
            finish();

        }




    }
    public <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value == entry.getValue()) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    public void getFriends() throws FirebaseException{

        friendList = new ArrayList<>();
        firebase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot post : dataSnapshot.getChildren()) {

                    MyDB my = post.getValue(MyDB.class);
                    friendList.add(my.getUsername());

                }
                displayListView();
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {
                try {
                    showErrorDialog(firebaseError.getMessage());
                    Intent intent = getIntent();
                    setResult(RESULT_CANCELED,intent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) throws Exception{
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_dialog_alert)
                .show();
    }

}
