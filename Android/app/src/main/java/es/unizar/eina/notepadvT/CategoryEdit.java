package es.unizar.eina.notepadvT;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CategoryEdit extends AppCompatActivity {

    private EditText mNameText;
    private Long mRowId;
    private String name;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_edit);
        setTitle("Edit category");
        mNameText = (EditText) findViewById(R.id.name);
        Button confirmButton = (Button) findViewById(R.id.confirm);
        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                    : null;
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(NotesDbAdapter.KEY_NAME, mNameText.getText().toString());
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

    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchCategory(mRowId);
            startManagingCursor(note);
            mNameText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NAME)));
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

    private void saveState() {
        String name = mNameText.getText().toString();
        if (mRowId == null) {
            long id = mDbHelper.createCategory(name);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateCategory(mRowId, name);
        }
    }
}
