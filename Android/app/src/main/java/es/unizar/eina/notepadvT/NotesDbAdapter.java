package es.unizar.eina.notepadvT;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * <p>
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class NotesDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_CATEGORY = "category";

    public static final String KEY_NAME = "name";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE_NOTES =
            "create table notes (_id integer primary key autoincrement, "
                    + "title text not null, body text not null, category integer);";
    private static final String DATABASE_CREATE_CATEGORIES =
            "create table categories (_id integer primary key autoincrement, "
                    + "name text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE_NOTES = "notes";
    private static final String DATABASE_TABLE_CATEGORIES = "categories";
    private static final int DATABASE_VERSION = 4;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_NOTES);
            db.execSQL(DATABASE_CREATE_CATEGORIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes;");
            db.execSQL("DROP TABLE IF EXISTS categories;");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Closes the notes database.
     */
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the note
     * @param body  the body of the note
     * @param category the category id (null for no category)
     * @return rowId or -1 if failed
     */
    public long createNote(String title, String body, Integer category) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_CATEGORY, category);

        return mDb.insert(DATABASE_TABLE_NOTES, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {
        return mDb.delete(DATABASE_TABLE_NOTES, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database.
     * Can be sorted by a data column (KEY_TITLE, KEY_CATEGORY, ...)
     * Can be filtered by category name
     *
     * @param orderBy if null not ordered. if not, order by that column name
     * @param category if null show all, if not, show only notes with that category name (not id)
     * @return a cursor with the notes
     */
    public Cursor fetchAllNotes(String orderBy, String category) {
//        if (category != null) {
//            category = KEY_CATEGORY + "=" + fetchCategory(category);
//        }
        //return mDb.rawQuery("SELECT " + DATABASE_TABLE_NOTES + "." + KEY_ROWID + " AS " + KEY_ROWID + "," + KEY_TITLE + "," + KEY_BODY + "," + KEY_NAME + " FROM " + DATABASE_TABLE_NOTES + " LEFT JOIN " + DATABASE_TABLE_CATEGORIES + " ON " + DATABASE_TABLE_NOTES + '.' + KEY_CATEGORY + '=' + DATABASE_TABLE_CATEGORIES+"."+KEY_ROWID,null);
        return mDb.rawQuery(
                "SELECT " + DATABASE_TABLE_NOTES + "." + KEY_ROWID + " AS " + KEY_ROWID + ", " +
                        KEY_TITLE + ", " +
                        KEY_BODY + ", " +
                        DATABASE_TABLE_CATEGORIES + "." + KEY_NAME + " AS " + KEY_NAME +
                        " FROM " + DATABASE_TABLE_NOTES + "" +
                        " LEFT JOIN " + DATABASE_TABLE_CATEGORIES + " ON " + DATABASE_TABLE_NOTES + '.' + KEY_CATEGORY + '=' + DATABASE_TABLE_CATEGORIES + "." + KEY_ROWID +
                        (category != null ? " WHERE " + KEY_NAME + "='" + category + '\'' : "") +
                        (orderBy != null ? " ORDER BY " + orderBy : "")
                , null);
//        return mDb.query(DATABASE_TABLE_NOTES, new String[]{KEY_ROWID, KEY_TITLE,
//                KEY_BODY, KEY_CATEGORY}, category, null, null, null, orderBy);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_NOTES, new String[]{KEY_ROWID,
                                KEY_TITLE, KEY_BODY, KEY_CATEGORY}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body  value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, String title, String body, Integer category) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_CATEGORY, category);

        return mDb.update(DATABASE_TABLE_NOTES, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param name the name of the category
     * @return rowId or -1 if failed
     */
    public long createCategory(String name) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);

        return mDb.insert(DATABASE_TABLE_CATEGORIES, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteCategory(long rowId) {

        return mDb.delete(DATABASE_TABLE_CATEGORIES, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllCategories() {
        return mDb.query(DATABASE_TABLE_CATEGORIES, new String[]{KEY_ROWID, KEY_NAME}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the category that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchCategory(long rowId) throws SQLException {
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE_CATEGORIES, new String[]{KEY_ROWID,
                                KEY_NAME}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    /**
     * Return a Cursor positioned at the category that matches the given name
     * If no category with that name is found, one is created
     * If null or empty string is passed, null is returned
     *
     * @param name name of category to retrieve
     * @return Cursor positioned to matching category (if not found is created first) or null if empty/null category
     */
    public Integer fetchCategory(String name) {
        if (name == null || name.isEmpty()) return null;

        Cursor mCursor = mDb.query(true, DATABASE_TABLE_CATEGORIES, new String[]{KEY_ROWID, KEY_NAME}, KEY_NAME + " like '" + name + '\'', null, null, null, null, null);

        if (mCursor.getCount() == 0) {
            createCategory(name);
            mCursor = mDb.query(true, DATABASE_TABLE_CATEGORIES, new String[]{KEY_ROWID, KEY_NAME}, KEY_NAME + " like '" + name + '\'', null, null, null, null, null);
        }

        mCursor.moveToFirst();
        return mCursor.getInt(mCursor.getColumnIndex(KEY_ROWID));
    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @param name  value to set category name to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateCategory(long rowId, String name) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);

        return mDb.update(DATABASE_TABLE_CATEGORIES, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}