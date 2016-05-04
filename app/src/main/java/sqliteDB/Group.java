package sqliteDB;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Saad on 22-04-2016.
 */
@SuppressWarnings("serial")
public class Group implements Serializable{
        private String id;
        private String groupName;
        private String groupCity;
        private String desc;
        private String admin;
        private String creationDate;
        private List<String> members;
        private int membersRegistered;
        public Group() {

        }
        public Group(String id, String groupName, String groupCity, String desc
                , String admin, String creationDate, List<String> members, int membersRegistered){
            this.id = id;
            this.groupName = groupName;
            this.groupCity = groupCity;
            this.desc = desc;
            this.admin = admin;
            this.creationDate = creationDate;
            this.members = members;
            this.membersRegistered = membersRegistered;


        }




      /*  public MyGroups(int group_id, String groupName, String groupCity, String desc
      , String admin, String creationDate, String membersRegistered){
            this.group_id = group_id;
            this.groupName = groupName;
            this.groupCity = groupCity;
            this.desc = desc;
            this.admin = admin;
            this.creationDate = creationDate;
            this.membersRegistered = membersRegistered;

        }*/

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }


        public String getGroupName() {
        return groupName;
    }

        public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

        public String getGroupCity() {
            return groupCity;
        }

        public void setGroupCity(String groupCity) {
            this.groupCity = groupCity;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getAdmin() {
        return admin;
    }

        public void setAdmin(String admin) {
        this.admin = admin;
    }

        public String getCreationDate() {
        return creationDate;
    }

        public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

        public int getMembersRegistered() {
        return membersRegistered;
    }

        public void setMembersRegistered(int membersRegistered) {this.membersRegistered = membersRegistered;}
}
