package firebase;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseException;

import java.text.SimpleDateFormat;
import java.util.Date;

import sqliteDB.MyDB;


public class ServerDataHandling {
    private static final ServerDataHandling sdHandler = new ServerDataHandling();
    public MyDB myInfo;
    private Firebase firebase;
    final String firebase_url = "https://conversa.firebaseio.com";

    private ServerDataHandling() {
        // User-specific instance variables.
        myInfo = new MyDB();
        firebase = new Firebase(firebase_url);

    }
    public static synchronized ServerDataHandling getInstance(){
        return  sdHandler; }


    public void setFirebaseRef() {
         firebase = new Firebase(firebase_url);
    }

    public Firebase getFirebaseRef() {
        return firebase;
    }

    public String getFirebaseURL() {
        return firebase_url;
    }


    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    public void logout(AuthData authData) {
        try{
            if (authData != null) {

            /* logout of Firebase */
                firebase.unauth();

            }
        }catch(FirebaseException fe){
            fe.printStackTrace();
        }
    }


    public String getCurrentDate(){
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

        return ft.format(date);
    }

}