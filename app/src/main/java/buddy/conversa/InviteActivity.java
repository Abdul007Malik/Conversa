package buddy.conversa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import buddy.conversa.acitvityutility.UserName;
import firebase.ServerDataHandling;
import sqliteDB.FriendListInfoDatabaseHandler;

public class InviteActivity extends AppCompatActivity {
    MyCustomAdapter dataAdapter = null;
    ListIterator<String> iterator;
    FriendListInfoDatabaseHandler infoDatabaseHandler;
    ClickListener clickListener;
    Map<String, Boolean> map;
    List<String> inviteList;
    Boolean status;
    ServerDataHandling sdHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        //Generate list View from ArrayList
        displayListView();
        Button btnInvite = (Button) findViewById(R.id.btnInvite);



    }


    private void displayListView() {

        //Array list of countries
        List<String> friendList;
        infoDatabaseHandler = new FriendListInfoDatabaseHandler(this, null, null, 1);
        friendList = infoDatabaseHandler.getAll();

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
        Context context;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               List<String> list) {
            super(context, textViewResourceId, list);
            this.context = context;
            this.frndList = new ArrayList<>();
            this.frndList.addAll(list);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheckedTextView name;

            Log.v("ConvertView", String.valueOf(position));
            LayoutInflater vi = ((Activity) context).getLayoutInflater();
            name = (CheckedTextView) findViewById(R.id.checkboxTextView);

            assert name != null;
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
            MyAsyncTask task = new MyAsyncTask(InviteActivity.this
                    , new MyAsyncTask.AsyncResponse() {
                @Override
                public void processFinish(Boolean status, ServerDataHandling sdHandlerOutput) {
                    sdHandler = sdHandlerOutput;
                    InviteActivity.this.status = status;
                }
            });
            task.execute("createGroup");
            UserName userName =UserName.getInstance();
            inviteList = new ArrayList<>(InviteActivity.getKeysByValue(map,true));
            userName.setUserNameList(inviteList);
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }




    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value == entry.getValue()) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }
}