package br.edu.ifsuldeminas.mch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shopping_lists.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LISTS = "lists";
    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LIST_ID = "list_id";
    public static final String COLUMN_ITEM = "item";
    public static final String COLUMN_COMPLETED = "completed"; // Added column for item completion

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createListsTableQuery = "CREATE TABLE " + TABLE_LISTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT)";
        db.execSQL(createListsTableQuery);

        String createItemsTableQuery = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LIST_ID + " INTEGER, " +
                COLUMN_ITEM + " TEXT, " +
                COLUMN_COMPLETED + " INTEGER DEFAULT 0, " + // Added column for item completion
                "FOREIGN KEY(" + COLUMN_LIST_ID + ") REFERENCES " + TABLE_LISTS + "(" + COLUMN_ID + "))";
        db.execSQL(createItemsTableQuery);
    }

    public void showDatabaseInfo() {
        SQLiteDatabase db = getReadableDatabase();

        // Mostrar informações da tabela "Lists"
        Cursor cursorLists = db.rawQuery("SELECT * FROM " + TABLE_LISTS, null);
        if (cursorLists.moveToFirst()) {
            Log.i("DatabaseInfo", "Table: " + TABLE_LISTS);
            do {
                int id = cursorLists.getInt(cursorLists.getColumnIndex(COLUMN_ID));
                String title = cursorLists.getString(cursorLists.getColumnIndex(COLUMN_TITLE));
                Log.i("DatabaseInfo", "ID: " + id + ", Title: " + title);
            } while (cursorLists.moveToNext());
        }
        cursorLists.close();

        // Mostrar informações da tabela "Items"
        Cursor cursorItems = db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);
        if (cursorItems.moveToFirst()) {
            Log.i("DatabaseInfo", "Table: " + TABLE_ITEMS);
            do {
                int id = cursorItems.getInt(cursorItems.getColumnIndex(COLUMN_ID));
                int listId = cursorItems.getInt(cursorItems.getColumnIndex(COLUMN_LIST_ID));
                String item = cursorItems.getString(cursorItems.getColumnIndex(COLUMN_ITEM));
                int completed = cursorItems.getInt(cursorItems.getColumnIndex(COLUMN_COMPLETED));
                Log.i("DatabaseInfo", "ID: " + id + ", List ID: " + listId + ", Item: " + item + ", Completed: " + completed);
            } while (cursorItems.moveToNext());
        }
        cursorItems.close();
    }

    public void showDatabaseStructure() {
        SQLiteDatabase db = getReadableDatabase();

        // Mostrar estrutura da tabela "Lists"
        Cursor cursorLists = db.rawQuery("PRAGMA table_info(" + TABLE_LISTS + ")", null);
        Log.i("DatabaseStructure", "Table: " + TABLE_LISTS);
        if (cursorLists.moveToFirst()) {
            do {
                int cid = cursorLists.getInt(cursorLists.getColumnIndex("cid"));
                String name = cursorLists.getString(cursorLists.getColumnIndex("name"));
                String type = cursorLists.getString(cursorLists.getColumnIndex("type"));
                int notNull = cursorLists.getInt(cursorLists.getColumnIndex("notnull"));
                String defaultValue = cursorLists.getString(cursorLists.getColumnIndex("dflt_value"));
                int primaryKey = cursorLists.getInt(cursorLists.getColumnIndex("pk"));
                Log.i("DatabaseStructure", "CID: " + cid + ", Name: " + name + ", Type: " + type +
                        ", NotNull: " + notNull + ", DefaultValue: " + defaultValue + ", PrimaryKey: " + primaryKey);
            } while (cursorLists.moveToNext());
        }
        cursorLists.close();

        // Mostrar estrutura da tabela "Items"
        Cursor cursorItems = db.rawQuery("PRAGMA table_info(" + TABLE_ITEMS + ")", null);
        Log.i("DatabaseStructure", "Table: " + TABLE_ITEMS);
        if (cursorItems.moveToFirst()) {
            do {
                int cid = cursorItems.getInt(cursorItems.getColumnIndex("cid"));
                String name = cursorItems.getString(cursorItems.getColumnIndex("name"));
                String type = cursorItems.getString(cursorItems.getColumnIndex("type"));
                int notNull = cursorItems.getInt(cursorItems.getColumnIndex("notnull"));
                String defaultValue = cursorItems.getString(cursorItems.getColumnIndex("dflt_value"));
                int primaryKey = cursorItems.getInt(cursorItems.getColumnIndex("pk"));
                Log.i("DatabaseStructure", "CID: " + cid + ", Name: " + name + ", Type: " + type +
                        ", NotNull: " + notNull + ", DefaultValue: " + defaultValue + ", PrimaryKey: " + primaryKey);
            } while (cursorItems.moveToNext());
        }
        cursorItems.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        onCreate(db);
    }

    public long saveList(String title) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);

        long listId = db.insert(TABLE_LISTS, null, values);
        db.close();

        return listId;
    }

    public void deleteList(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTS, COLUMN_TITLE + " = ?", new String[]{title});
        db.close();
    }

    public Cursor getAllLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_LISTS, null, null, null, null, null, null);
    }

    public String getListTitle(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
        }
        return null;
    }

    public int getListIdByTitle(String title) {
        this.showDatabaseStructure();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_TITLE + " = ?";
        String[] selectionArgs = {title};
        Cursor cursor = db.query(TABLE_LISTS, columns, selection, selectionArgs, null, null, null);
        Log.i("get","444");
        int listId = -1;
        if (cursor.moveToFirst()) {
            listId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        }
        cursor.close();
        return listId;
    }

    public void saveItem(long listId, String item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_LIST_ID, listId);
        values.put(COLUMN_ITEM, item);

        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    public Cursor getItemsByListId(long listId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ITEM, COLUMN_COMPLETED};
        Log.i("getItemsByListId","columns");
        Log.i("getItemsByListId",columns.toString());
        String selection = COLUMN_LIST_ID + " = ?";
        Log.i("getItemsByListId","selection");
        Log.i("getItemsByListId",selection);
        String[] selectionArgs = {String.valueOf(listId)};
        Log.i("getItemsByListId","selectionArgs");
        return db.query(TABLE_ITEMS, columns, selection, selectionArgs, null, null, null);
    }

    public void updateItemCompletion(long listId, String item, boolean completed) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETED, completed ? 1 : 0);

        String whereClause = COLUMN_LIST_ID + " = ? AND " + COLUMN_ITEM + " = ?";
        String[] whereArgs = {String.valueOf(listId), item};

        db.update(TABLE_ITEMS, values, whereClause, whereArgs);
        db.close();
    }
}
