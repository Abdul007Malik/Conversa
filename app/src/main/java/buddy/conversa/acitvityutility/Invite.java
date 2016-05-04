package buddy.conversa.acitvityutility;

/**
 * Created by AM on 27-04-2016.
 */
public class Invite {

    private String id;
    private String fromUserId;
    private String fromUserName;
    private String groupId;
    private Boolean status;

    public Invite() {
    }

    public Invite(String id, String fromUserId, String fromUserName, String groupId) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.groupId = groupId;
    }

    public Invite(String fromUserId, String fromUserName, String groupId,Boolean status) {
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.groupId = groupId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
