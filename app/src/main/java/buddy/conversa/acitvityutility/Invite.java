package buddy.conversa.acitvityutility;

/**
 * Created by AM on 16-05-2016.
 */
public class Invite {

        String code = null;
        String name = null;
        boolean selected = false;

        public Invite(String code, String name, boolean selected)
        {
            super();
            this.code = code;
            this.name = name;
            this.selected = selected;
        }

        public String getCode()
        {
            return code;
        }

        public void setCode(String code)
        {
            this.code = code;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public boolean isSelected()
        {
            return selected;
        }

        public void setSelected(boolean selected)
        {
            this.selected = selected;
        }


}
