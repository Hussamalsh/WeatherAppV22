package com.example.hussam.weatherappv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.hussam.weatherappv2.Model.UserLocation;
import com.example.hussam.weatherappv2.Model.UserM;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    static String DATABASE_NAME="UserDataBase";

    //UserTable

    public static final String TABLE_NAME="UserTable";

    public static final String Table_Column_UserID="userid";

    public static final String Table_Column_1_Name="name";

    public static final String Table_Column_2_Email="email";

    public static final String Table_Column_3_Password="password";

    //LocationTable

    public static final String TABLE_NAME2="LocTable";

    public static final String Table_Column_LocID="locid";
    public static final String Table_Column_1_LocationName="locationName";
    public static final String Table_Column_2_Latitude="lat";
    public static final String Table_Column_3_Longitude="long";


    //UserLocationTable
    public static final String TABLE_NAME3="UserLocationTable";
    public static final String Table_Column_ID="id";

    public SQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("
                +Table_Column_UserID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                +Table_Column_1_Name+" VARCHAR NOT NULL, "
                +Table_Column_2_Email+" VARCHAR NOT NULL, "
                +Table_Column_3_Password+" VARCHAR NOT NULL);";
        database.execSQL(CREATE_TABLE);


        CREATE_TABLE= ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME2 + "("
                + Table_Column_LocID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + Table_Column_1_LocationName+ " VARCHAR NOT NULL, "
                + Table_Column_2_Latitude + " VARCHAR, "
                + Table_Column_3_Longitude+ " VARCHAR);");
        database.execSQL(CREATE_TABLE);


        CREATE_TABLE= ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME3 + "("
                +Table_Column_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + Table_Column_UserID + " INTEGER  NOT NULL, "
                + Table_Column_LocID + " INTEGER  NOT NULL);");
        database.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME2);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME3);
        onCreate(db);
    }

    /*
    * Creating a new location for the user
    */
    public long createNewLoc(UserLocation obj, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();

        //get location by name
        String city = getLoctionByName(obj.getUserLocName());
        if(!city.isEmpty())
        {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(Table_Column_1_LocationName, obj.getUserLocName());
        values.put(Table_Column_2_Latitude, obj.getUserLat());
        values.put(Table_Column_3_Longitude, obj.getUserLang());
        // insert row
        long loc_id = db.insert(TABLE_NAME2, null, values);
        //getUserId
        long userID = getUserByEmail(userEmail.trim());
        // assigning location to user
        createUserLoc(userID, loc_id);

        return loc_id;
    }

    /*
        * Creating user_location
    */
    public long createUserLoc(long user_id, long loc_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Table_Column_UserID, user_id);
        values.put(Table_Column_LocID, loc_id);
        long id = db.insert(TABLE_NAME3, null, values);
        return id;
    }


    /*
     * get single location
    */
    public UserLocation getLoction(long loc_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME2 + " WHERE " + Table_Column_LocID + " = " + loc_id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        UserLocation loc = new UserLocation();
        loc.setUserLocName(""+c.getInt(c.getColumnIndex(Table_Column_1_LocationName)));
        loc.setUserLat((c.getString(c.getColumnIndex(Table_Column_2_Latitude))));
        loc.setUserLang(c.getString(c.getColumnIndex(Table_Column_3_Longitude)));
        return loc;
    }

    /*
     * get single location
    */
    public String getLoctionByName(String loc_name)
    {
        String cityname = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME2 + " WHERE " + Table_Column_1_LocationName + " = '" + loc_name+"'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
        {
            if(c.moveToFirst()){
                cityname=""+c.getInt(c.getColumnIndex(Table_Column_1_LocationName));
            }
        }
        return cityname;
    }

    /*
 * getting all locations under single user
 * */
    public List<UserLocation> getAllLocationsByEmail(String user_email)
    {
        List<UserLocation> locations = new ArrayList<UserLocation>();

        String selectQuery = "SELECT  td."+Table_Column_LocID+",td."+Table_Column_1_LocationName+",td."
                +Table_Column_2_Latitude+",td."+Table_Column_3_Longitude
                +" FROM " + TABLE_NAME2 + " td, "
                + TABLE_NAME + " tg, "
                + TABLE_NAME3 + " tt WHERE tg." + Table_Column_2_Email
                + " = '" + user_email + "'" + " AND tg." + Table_Column_UserID
                + " = " + "tt." + Table_Column_UserID + " AND td." + Table_Column_LocID + " = "
                + "tt." + Table_Column_LocID;

        Log.e("database", selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        Log.e("database", "noooooooooooooooooooooooo..............."+ c.getCount());

        // looping through all rows and adding to list
        if (c != null) {
            //Start from beginning
           if( c.moveToFirst()){
               do {
                   UserLocation loc = new UserLocation();
                   loc.setId(c.getString(c.getColumnIndex(Table_Column_LocID)));
                   loc.setUserLocName(c.getString(c.getColumnIndex(Table_Column_1_LocationName)));
                   loc.setUserLat((c.getString(c.getColumnIndex(Table_Column_2_Latitude))));
                   loc.setUserLang(c.getString(c.getColumnIndex(Table_Column_3_Longitude)));
                   // adding to location list
                   Log.e("database", loc.getUserLocName() + " hhhhhhhhhhh " + loc.getId());
                   locations.add(loc);
               } while (c.moveToNext());
           }
        }else{
            Log.e("database", "noooooooooooooooooooooooo...............");

        }
        return locations;
    }

    /*
     * Deleting a location by ID
     */
    public void deleteLocation(String loc_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME2, Table_Column_LocID + " = ?", new String[] { String.valueOf(loc_id) });
    }


    /*
 * Deleting all locations
 */

    public void deleteLocations(UserM tag, boolean should_delete_all_tag_todos)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // before deleting tag
        // check if todos under this tag should also be deleted
        if (should_delete_all_tag_todos)
        {
            // get all todos under this tag
            List<UserLocation> allUserLocations = getAllLocationsByEmail(tag.geteMail());

            // delete all todos
            for (UserLocation loc : allUserLocations)
            {
                // delete todo
                deleteLocation(loc.getId());
            }
        }
    }

    /*
      * Creating user
    */
    public long createUser(UserM user, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Table_Column_1_Name, user.getName());
        values.put(Table_Column_2_Email, user.geteMail());
        values.put(Table_Column_3_Password, password);
        // insert row
        long tag_id = db.insert(TABLE_NAME, null, values);

        return tag_id;
    }

    public long getUserByEmail(String user_Email)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + Table_Column_2_Email + " = '" + user_Email+"'";

        Cursor c = db.rawQuery(selectQuery, null);

        long id =0;

        if (c != null)
        {
            //Start from beginning
            c.moveToFirst();
            id = c.getInt(c.getColumnIndex(Table_Column_UserID));
        }
        return id;
    }

    // closing database
    public void closeDB()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}