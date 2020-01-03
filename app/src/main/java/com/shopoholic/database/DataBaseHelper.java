package com.shopoholic.database;

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
        if(!isExist(SqlConstant.TABLE_MEDIA_FILES, sqLiteDatabase))
        {
            sqLiteDatabase.execSQL(SqlConstant.CREATE_TABLE_MEDIA_FILES);

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * method to check if table exists or not
     * @param tableName
     * @param database
     * @return
     */
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
