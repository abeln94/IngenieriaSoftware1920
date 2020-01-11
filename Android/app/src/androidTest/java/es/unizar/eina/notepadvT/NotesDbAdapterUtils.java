package es.unizar.eina.notepadvT;

import android.database.Cursor;
import android.test.AndroidTestCase;

public class NotesDbAdapterUtils extends AndroidTestCase {

    public void testCreateLotsOfNotes(){
        final NotesDbAdapter adapter = new NotesDbAdapter(getContext());
        adapter.open();

        for (int i = 0; i < 1000; ++i) {
            adapter.createNote("volume_" + i, "n" + i, null);
        }
    }

    public void testDeleteAllNotes(){
        final NotesDbAdapter adapter = new NotesDbAdapter(getContext());
        adapter.open();

        Cursor cursor = adapter.fetchAllNotes(null, null);
        while(cursor.moveToNext()){
            adapter.deleteNote(cursor.getLong(0));
        }
    }

}