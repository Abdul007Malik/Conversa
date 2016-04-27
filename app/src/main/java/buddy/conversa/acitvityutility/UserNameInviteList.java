package buddy.conversa.acitvityutility;

import java.util.List;

/**
 * Created by AM on 26-04-2016.
 */
public class UserNameInviteList {
    private static UserNameInviteList ourInstance = new UserNameInviteList();



    private List<String> UserNames;

    public static UserNameInviteList getInstance() {
        return ourInstance;
    }

    private UserNameInviteList() {
    }

    public void setUserNameList(List<String> list){
        this.UserNames = list;
    }

    public List<String> getUserNames() {
        return UserNames;
    }
}
