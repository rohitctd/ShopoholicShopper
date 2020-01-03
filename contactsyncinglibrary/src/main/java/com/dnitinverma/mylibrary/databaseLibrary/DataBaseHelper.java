package com.dnitinverma.mylibrary.databaseLibrary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper class
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        if(!isExist(SqlConstant.TABLE_CONTACTS, sqLiteDatabase))
        {
            sqLiteDatabase.execSQL(SqlConstant.CREATE_TABLE_CONTACTS);

        }
        if(!isExist(SqlConstant.TABLE_EMAIL, sqLiteDatabase))
        {
            sqLiteDatabase.execSQL(SqlConstant.CREATE_TABLE_EMAIL);

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private boolean isExist(String tableName, SQLiteDatabase database) {
        String query = SqlConstant.SQLITE_MASTER.replace("?", tableName);
        boolean returnValue = false;
        Cursor cursor = null;
        cursor = database.rawQuery(query, null);
        if (cursor != null) {
            final int rowCount = cursor.getCount();
            if (rowCount > 0) {
                cursor.close();
                returnValue = true;
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return returnValue;
    }
}
