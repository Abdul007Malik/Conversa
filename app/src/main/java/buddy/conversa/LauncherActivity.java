package buddy.conversa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import firebase.ServerDataHandling;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        ServerDataHandling sd = ServerDataHandling.getInstance();

        if(sd!=null && sd.userAccount.getFirebaseRef().getAuth()==null)
            intent = new Intent(this,LoginActivity.class);
        else {
            intent = new Intent(this,MainActivity.class);
        }
        startActivity(intent);
    }
}
