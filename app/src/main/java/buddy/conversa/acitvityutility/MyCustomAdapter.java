package buddy.conversa.acitvityutility;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import buddy.conversa.InviteActivity;
import buddy.conversa.R;

public class MyCustomAdapter extends ArrayAdapter<Invite> {

    private ArrayList<Invite> frndList;
    private int resourceId;
    Activity context;
    View rowView;

    public ArrayList<Invite> getfrndList(){
        return frndList;
    }
    public MyCustomAdapter(Activity context, int textViewResourceId,
                           List<Invite> list) {
        super(context, textViewResourceId, list);
        this.context = context;
        this.resourceId = textViewResourceId;
        this.frndList = new ArrayList<>();
        this.frndList.addAll(list);

    }


    private class ViewHolder{
       // private TextView textView;
        private CheckBox checkBox;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       try {
           this.rowView = convertView;
           ViewHolder holder = new ViewHolder();


           if (rowView == null) {
               LayoutInflater vi = context.getLayoutInflater();
               rowView = vi.inflate(resourceId, null);

               holder.checkBox = (CheckBox) rowView.findViewById(R.id.checkBoxInvite);
               //holder.textView = (TextView) rowView.findViewById(R.id.textViewInvite);
               rowView.setTag(holder);

               holder.checkBox.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       CheckBox cb = (CheckBox) v;
                       Invite invite = (Invite) cb.getTag();
                       invite.setSelected(cb.isChecked());
                   }
               });

           }else
            holder = (ViewHolder) rowView.getTag();
           Invite invite = frndList.get(position);
           holder.checkBox.setText(invite.getName());
           holder.checkBox.setChecked(invite.isSelected());
           holder.checkBox.setTag(invite);
       }catch (Exception e){
           Log.getStackTraceString(e);
       }
       return rowView;

    }

}

