package buddy.conversa.acitvityutility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AM on 26-04-2016.
 */
public class UserName {
    private static UserName ourInstance = new UserName();



    private List<String> UserName;

    public static UserName getInstance() {
        return ourInstance;
    }

    private UserName() {
    }

    public void setUserNameList(List<String> list){
        this.UserName = list;
    }

    public List<String> getUserName() {
        return UserName;
    }
}
