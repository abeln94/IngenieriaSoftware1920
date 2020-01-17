package es.unizar.eina.notepadvT;

import android.database.Cursor;
import android.test.AndroidTestCase;

import es.unizar.eina.notepadvT.database.NotesDbAdapter;

public class NotesDbAdapterUtils extends AndroidTestCase {

    public void testCreateLotsOfNotes(){
        for (int i = 0; i < 1000; ++i) {
            adapter.createNote("volume_" + i, "n" + i, null);
        }
    }

    public void testDeleteAllNotes(){
        Cursor cursor = adapter.fetchAllNotes(null, null);
        while(cursor.moveToNext()){
            adapter.deleteNote(cursor.getLong(0));
        }
    }

    // ------------------- utils -------------------

    private NotesDbAdapter adapter;

    @Override
    public void setUp() {
        adapter = new NotesDbAdapter(getContext());

        // open database
        adapter.open();
    }

}