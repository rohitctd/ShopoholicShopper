package com.dnitinverma.mylibrary.databaseLibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_PHONE_WITH_CODE;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.TABLE_CONTACTS;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.TABLE_EMAIL;

/**
 * Created by appinventiv on 30/3/17.
 */

public class DataBaseProvider {

    private DataBaseHelper baseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private final String DATA_BASE_NAME = "shopoholic_contacts.db";
    private final int VERSION = 1;


    private DataBaseProvider(Context context) {
        baseHelper = new DataBaseHelper(context, DATA_BASE_NAME, null, VERSION);
        openDBConnection();
    }

    public static DataBaseProvider getInstance(Context mContext) {
        return new DataBaseProvider(mContext);
    }

    /**
     * method to open the database connection
     */
    private void openDBConnection() {
        try {
            sqLiteDatabase = baseHelper.getWritableDatabase();
        } catch (SQLException exception) {
            Log.e("DB_ERROR", "CONNECTION_NOT_OPEN");
        }
    }

    /**
     * method to check if the database is open or not
     * @return
     */
    private boolean isOpen() {
        return sqLiteDatabase!=null && sqLiteDatabase.isOpen();
    }


    /**
     * method to close the database connection
     */
    public void closeDBConnection() {
        if (sqLiteDatabase != null && isOpen()) {
            sqLiteDatabase.close();
        }
    }

    /**
     * method to close the cursor
     * @param cursor
     */
    public void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

    }

    /**
     * method to insert data one by for the provided table name and values
     * @param tableName
     * @param contentValue
     * @return
     */
    public long insert(String tableName, ContentValues contentValue) {
        return sqLiteDatabase.insert(tableName, null, contentValue);
    }

    /**
     * method to insert values in the database for the provided table and values
     *
     * @param tableName
     * @param contentValues
     */
    void insertAll(String tableName, List<ContentValues> contentValues) {
        sqLiteDatabase=baseHelper.getWritableDatabase();
        for (ContentValues contentValue:contentValues) {
            sqLiteDatabase.insert(tableName, null, contentValue);
        }
        //sqLiteDatabase.close();
    }




    /**
     * Gets the columns label in a particular table.
     *
     * @param tableName
     * @return
     */
    String[] getTableColumns(String tableName) {
        sqLiteDatabase=baseHelper.getWritableDatabase();
        List<String> columns = new ArrayList<String>();
        Cursor cursor = sqLiteDatabase.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        if (cursor.moveToFirst()) {
            do {
                columns.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return columns.toArray(new String[columns.size()]);
    }

    /**
     * method to fecth contacts from the db
     *
     * @param table
     * @param query
     * @return
     */
    List<ContentValues> getAllContacts(String table, String query){
        List<ContentValues> contentValues=new ArrayList<ContentValues>();
        Cursor cursor=null;
        cursor =sqLiteDatabase.rawQuery(query,null);
        String[] columns = null;
        if(columns==null)
        {
            columns=getTableColumns(table);
        }if(cursor!=null && cursor.moveToFirst())
        {
            do {
                ContentValues values=new ContentValues();
                for(int j=0;j<columns.length;j++)
                {
                    values.put(columns[j], cursor.getString(j));
                }
                contentValues.add(values);
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);

        return contentValues;
    }

    /**
     * method to fecth data from the db
     *
     * @param table
     * @param query
     * @return
     */
    List<ContentValues> getData(String table, String query){
        sqLiteDatabase=baseHelper.getReadableDatabase();
        List<ContentValues> contentValues=new ArrayList<ContentValues>();
        Cursor cursor=null;
        cursor =sqLiteDatabase.rawQuery(query,null);
        String[] columns = null;
        if(columns==null)
        {
            columns=getTableColumns(table);
        }if(cursor!=null && cursor.moveToFirst())
        {
            do {
                ContentValues values=new ContentValues();
                for(int j=0;j<columns.length;j++)
                {
                    values.put(columns[j], cursor.getString(j));
                }
                contentValues.add(values);
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        //sqLiteDatabase.close();

        return contentValues;
    }

    /**
     * this method is used to get the count of contacts
     * @return
     */
    public long getContactsCount() {
        SQLiteDatabase db = baseHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_CONTACTS);
        db.close();
        return count;
    }

    /**
     * this method is used to get the count of emails
     * @return
     */
    public long getEmailsCount() {
        SQLiteDatabase db = baseHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_EMAIL);
        db.close();
        return count;
    }


    /**
     * method to fetch data from the database provided the conditions and parameters
     *
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    public List<ContentValues> select(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        sqLiteDatabase=baseHelper.getWritableDatabase();
        List<ContentValues> contentValues=new ArrayList<ContentValues>();
        Cursor cursor=null;
        cursor=sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        if(columns==null)
        {
            columns=getTableColumns(table);
        }if(cursor!=null && cursor.moveToFirst())
        {
            do {
                ContentValues values=new ContentValues();
                for(int j=0;j<columns.length;j++)
                {
                    values.put(columns[j], cursor.getString(j));
                }
                contentValues.add(values);
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        //sqLiteDatabase.close();

        return contentValues;
    }

    /**
     * updates a specific table with the ContentValues provided. If whereClause has is null it will
     * update all the rows. Pass the whereClause and the String [] whereArgs when you need specific
     * rows to be updated.
     *
     * @param tableName
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public int update(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        sqLiteDatabase=baseHelper.getWritableDatabase();
        int index=sqLiteDatabase.update(tableName, values, whereClause, whereArgs);
        //sqLiteDatabase.close();
        return index;
    }

    /**
     * updates a specific table with the ContentValues provided. If whereClause has is null it will
     * update all the rows. Pass the whereClause and the String [] whereArgs when you need specific
     * rows to be updated.
     *
     * @param tableName
     * @param values
     * @return
     */
    public void updateAll(String tableName, List<ContentValues> values) {
        sqLiteDatabase=baseHelper.getWritableDatabase();
        for (int i=0;i<values.size();i++)
            sqLiteDatabase.update(tableName, values.get(i), CONTACT_PHONE_WITH_CODE + "=\"" + values.get(i).getAsString(CONTACT_PHONE_WITH_CODE)+ "\"", null);
        //sqLiteDatabase.close();
    }


    /**
     * Delete a specific row in the table tableName.
     *
     * @param tableName
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public int delete(String tableName, String whereClause, String[] whereArgs) {
        return sqLiteDatabase.delete(tableName, whereClause, whereArgs);
    }

    public void deleteData(String tableName) {
        sqLiteDatabase = baseHelper.getWritableDatabase();
        String query1="delete from " + tableName ;
        sqLiteDatabase.execSQL(query1);
        //sqLiteDatabase.close();
    }

    public int getMaxColumnData(String tableName,String columnName) {

        sqLiteDatabase = baseHelper.getReadableDatabase();
        final SQLiteStatement stmt = sqLiteDatabase
                .compileStatement("SELECT MAX("+columnName+") FROM "+tableName);
       // sqLiteDatabase.close();
        return (int) stmt.simpleQueryForLong();
    }
}
