package com.shopoholic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class for database provider
 */

public class DataBaseProvider {

    private DataBaseHelper baseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private final String DATA_BASE_NAME = "chat.db";
    private final int VERSION = 1;

    public DataBaseProvider(Context context) {
        baseHelper = new DataBaseHelper(context, DATA_BASE_NAME, null, VERSION);
        openDBConnection();
    }

    public final void openDBConnection() {
        try {
            sqLiteDatabase = baseHelper.getWritableDatabase();
        } catch (SQLException exception) {
            Log.e("DBERROR", "CONNECTION_NOT_OPEN");
        }
    }

    public boolean isOpen() {
        if (sqLiteDatabase != null) {
            return false;
        }
        return sqLiteDatabase.isOpen();
    }

    public void closeDBConnection() {
        if (sqLiteDatabase != null && isOpen()) {
            sqLiteDatabase.close();
        }
    }

    public void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

    }

    public long insert(String tableName, ContentValues contentValue) {
        return sqLiteDatabase.insert(tableName, null, contentValue);
    }

    public void insertAll(String tableName, List<ContentValues> contentValues) {
        Iterator<ContentValues> iterator = contentValues.iterator();
        while (iterator.hasNext()) {
            ContentValues contentValue = (ContentValues) iterator.next();

            sqLiteDatabase.insert(tableName, null, contentValue);
        }
    }

    public List<ContentValues> getAllReminders(String table, String query){
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
     * Gets the columns label in a particular table.
     *
     * @param tableName
     * @return
     */
    public String[] getTableColumns(String tableName) {
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

    public List<ContentValues> select(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
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
        return sqLiteDatabase.update(tableName, values, whereClause, whereArgs);
    }

    /**
     * Delete all the rows in a particular table.
     *
     * @param tableName
     */
    public int deleteAllRows(String tableName) {
        return sqLiteDatabase.delete(tableName, null, null);
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

    public void deleteReminders() {
        String query1="delete from " + SqlConstant.TABLE_REMINDERS ;
        sqLiteDatabase.execSQL(query1);
    }

    public void deleteContacts() {
        String query1="delete from " + SqlConstant.TABLE_CONTACTS ;
        sqLiteDatabase.execSQL(query1);
    }

    public void clearDatabase() {
        sqLiteDatabase.execSQL("delete from " + SqlConstant.TABLE_MEDIA_FILES);
    }
}
