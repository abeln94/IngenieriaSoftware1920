package es.unizar.eina.notepadvT;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Actividad para editar las notas
 */
public class NoteEdit extends AppCompatActivity {

    private EditText mTitleText;
    private EditText mBodyText;
    private AutoCompleteTextView mCategory;
    private Long mRowId;
    private String title;
    private String body;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);
        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mCategory = (AutoCompleteTextView) findViewById(R.id.category);
        Button confirmButton = (Button) findViewById(R.id.confirm);
        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                    : null;
        }

        EditText mIdText = (EditText) findViewById(R.id.id);
        mIdText.setText("***");
        if(mRowId!=null) {

            mIdText.setText(Long.toString(mRowId));
        }

        Cursor c = mDbHelper.fetchAllCategories();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.categories_row,
                c,
                new String[]{NotesDbAdapter.KEY_NAME},
                new int[]{R.id.text1}
        );
        adapter.setStringConversionColumn(c.getColumnIndex(NotesDbAdapter.KEY_NAME));
        mCategory.setAdapter(adapter);

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(NotesDbAdapter.KEY_TITLE, mTitleText.getText().toString());
                bundle.putString(NotesDbAdapter.KEY_BODY, mBodyText.getText().toString());
                bundle.putString(NotesDbAdapter.KEY_CATEGORY, mCategory.getText().toString());
                if (mRowId != null) {
                    bundle.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
                }

                Intent mIntent = new Intent();
                mIntent.putExtras(bundle);
                setResult(RESULT_OK, mIntent);
                finish();
            }

        });
    }

    /**
     * Fills the elements with the note data
     */
    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            Cursor category = mDbHelper.fetchCategory(note.getInt(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_CATEGORY)));
            if(category.getCount() != 0) {
                mCategory.setText(category.getString(
                        category.getColumnIndexOrThrow(NotesDbAdapter.KEY_NAME)
                ));
            }else{
                mCategory.setText("");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    /**
     * Saves the current note
     */
    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        Integer category = mDbHelper.fetchCategory(mCategory.getText().toString());
        if (mRowId == null) {
            long id = mDbHelper.createNote(title, body, category);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body, category);
        }
    }

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
