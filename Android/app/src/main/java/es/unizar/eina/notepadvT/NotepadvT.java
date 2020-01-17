package es.unizar.eina.notepadvT;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import es.unizar.eina.notepadvT.categories.Categories;
import es.unizar.eina.notepadvT.database.NotesDbAdapter;
import es.unizar.eina.send.SendAbstractionImpl;

/**
 * Actividad principal. Muestra el listado de notas.
 */
public class NotepadvT extends AppCompatActivity {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    private static final int SEND_SMS_ID = Menu.FIRST + 3;
    private static final int SEND_EMAIL_ID = Menu.FIRST + 4;
    private static final int CATEGORIES_ID = Menu.FIRST + 5;

    private NotesDbAdapter mDbHelper;
    private ListView mList;
    private SendAbstractionImpl sendSMS = new SendAbstractionImpl(this, SendAbstractionImpl.TYPES.SMS);
    private SendAbstractionImpl sendEMAIL = new SendAbstractionImpl(this, SendAbstractionImpl.TYPES.EMAIL);

    private Spinner mCategories;
    private RadioGroup mSortBy;
    private CheckBox mFilter;

    private int lastItemPosition = 0;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepadvt);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mList = (ListView) findViewById(R.id.list);
        mFilter = (CheckBox) findViewById(R.id.filter);
        mCategories = (Spinner) findViewById(R.id.categories);
        mSortBy = (RadioGroup) findViewById(R.id.sortBy);

        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillData(0);
            }
        });
        mCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mFilter.isChecked()) {
                    fillData(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                fillData(0);
            }
        });

        fillData(0);
        fillCategories();

        registerForContextMenu(mList);

    }

    /**
     * Populates the list with the notes
     */
    private void fillData(int scrollTo) {
        // Get all of the notes from the database and create the item list
        String category = null;
        if (mFilter.isChecked()) {
            Cursor category_cursor = (Cursor) mCategories.getSelectedItem();
            if(category_cursor != null) {
                category = category_cursor.getString(category_cursor.getColumnIndex(NotesDbAdapter.KEY_NAME));
            }
        }
        String sortBy = null;
        switch (mSortBy.getCheckedRadioButtonId()) {
            case R.id.sort_title:
                sortBy = NotesDbAdapter.KEY_TITLE;
                break;
            case R.id.sort_category:
                sortBy = NotesDbAdapter.KEY_NAME;
                break;
        }
        Cursor mNotesCursor = mDbHelper.fetchAllNotes(sortBy, category);
        startManagingCursor(mNotesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.KEY_TITLE, NotesDbAdapter.KEY_NAME};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1, R.id.text2};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, mNotesCursor, from, to);
        mList.setAdapter(notes);

        mList.setSelection(scrollTo);
    }

    private void fillCategories() {
        Cursor c = mDbHelper.fetchAllCategories();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.categories_row,
                c,
                new String[]{NotesDbAdapter.KEY_NAME},
                new int[]{R.id.text1}
        );
        adapter.setStringConversionColumn(c.getColumnIndex(NotesDbAdapter.KEY_NAME));
        mCategories.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.menu_insert);
        menu.add(Menu.NONE, CATEGORIES_ID, Menu.NONE, R.string.menu_edit_categories);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
            case CATEGORIES_ID:
                editCategories();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_delete);
        menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.menu_edit);
        menu.add(Menu.NONE, SEND_SMS_ID, Menu.NONE, R.string.menu_send_sms);
        menu.add(Menu.NONE, SEND_EMAIL_ID, Menu.NONE, R.string.menu_send_email);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData(info.position - 1);
                return true;
            case EDIT_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                editNote(info.position, info.id);
                return true;
            case SEND_EMAIL_ID:
            case SEND_SMS_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Cursor cursor = mDbHelper.fetchNote(info.id);
                (item.getItemId() == SEND_EMAIL_ID ? sendEMAIL : sendSMS).send(
                        cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY))
                );
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Starts the activity to create a new note
     */
    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);

        lastItemPosition = mList.getCount();
    }

    /**
     * Starts the activity to edit an existing note
     * @param position unused
     * @param id identifier of the note that will be edited
     */
    protected void editNote(int position, long id) {
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);

        lastItemPosition = position;
    }


    private void editCategories() {
        startActivityForResult(new Intent(this, Categories.class), ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData(lastItemPosition);
        fillCategories();
    }
}

