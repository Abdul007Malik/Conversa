package sqliteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Saad on 22-04-2016.
 */
public class Group_DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ConversaDB.db";
    private static final String TABLE_MYGROUPS = "mygroups";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GROUP_NAME = "group_name";
    public static final String COLUMN_GROUP_CITY = "group_city";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_ADMIN = "admin";
    public static final String COLUMN_CREATION_DATE = "creation_date";
    public static final String COLUMN_MEMBERS_REGISTERED = "members_registered";

    public Group_DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MYGROUPS_TABLE = "CREATE TABLE" + TABLE_MYGROUPS + "("
                + COLUMN_ID + "INTEGER PRIMARY KEY,"  + "INTEGER,"
                + COLUMN_GROUP_NAME + "TEXT,"
                + COLUMN_GROUP_CITY + "TEXT,"
                + COLUMN_DESCRIPTION + "TEXT,"
                + COLUMN_ADMIN + "TEXT,"
                + COLUMN_CREATION_DATE + "TEXT,"
                + COLUMN_MEMBERS_REGISTERED + "INTEGER" + ")";
        db.execSQL(CREATE_MYGROUPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_MYGROUPS);
        onCreate(db);
    }


    public void addMyGroups(Group group) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, group.getGroupName());
        values.put(COLUMN_GROUP_CITY, group.getGroupCity());
        values.put(COLUMN_DESCRIPTION, group.getDesc());
        values.put(COLUMN_ADMIN, group.getAdmin());
        values.put(COLUMN_CREATION_DATE, group.getCreationDate());
        values.put(COLUMN_MEMBERS_REGISTERED, group.getMembersRegistered());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_MYGROUPS, null, values);
        db.close();
    }

    public Group findMyGroups(String COLUMN_GROUP_NAME) {
        Group mygroups = new Group();
        String query = "SELECT * FROM" + TABLE_MYGROUPS + "WHERE" + COLUMN_GROUP_NAME + "=\"" + mygroups.getGroupName() + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            mygroups.setId(cursor.getString(0));

            mygroups.setGroupName(cursor.getString(1));
            mygroups.setGroupCity(cursor.getString(2));
            mygroups.setDesc(cursor.getString(3));
            mygroups.setAdmin(cursor.getString(4));
            mygroups.setCreationDate(cursor.getString(5));
            mygroups.setMembersRegistered(cursor.getInt(6));
            cursor.close();
        } else {
            mygroups = null;
        }
        db.close();
        return mygroups;
    }

    public boolean deleteMyGroups(int group_name) {
        boolean result = false;
        String query = "SELECT * FROM" + TABLE_MYGROUPS + "WHERE" + COLUMN_GROUP_NAME + "= \"" + group_name + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Group group = new Group();
        if (cursor.moveToFirst()) {
            group.setId(cursor.getString(0));
            db.delete(TABLE_MYGROUPS, COLUMN_ID + "=?", new String[]{String.valueOf(group.getId())});
            cursor.close();
            result = true;
        }
        db.close();
        return result;


    }}
