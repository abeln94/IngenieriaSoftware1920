package es.unizar.eina.notepadvT;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.lang.reflect.Field;

public class NotesDbAdapterTest extends AndroidTestCase {

    // ------------------- Tests -------------------

    // CreateNote
    public void testCreateNoteValid1() {
        testCreateNote("Hola", "Hola", getValidCategoryId(), 1);
    }

    public void testCreateNoteValid2() {
        testCreateNote("Hola", "Hola", null, 1);
    }

    public void testCreateNoteInvalid1() {
        testCreateNote(null, "Hola", getValidCategoryId(), -1);
    }

    public void testCreateNoteInvalid2() {
        testCreateNote("Hola", null, getValidCategoryId(), -1);
    }

    public void testCreateNoteInvalid3() {
        testCreateNote("", "Hola", getValidCategoryId(), -1);
    }

    public void testCreateNoteInvalid4() {
        testCreateNote("Hola", "Hola", -1, -1);
    }

    // deleteNote
    public void testDeleteNoteValid1() {
        testDeleteNote(getValidNoteId(), true);
    }

    public void testDeleteNoteInvalid1() {
        testDeleteNote(-1, false);
    }

    // updateNote
    public void testUpdateNoteValid1() {
        testUpdateNote(getValidNoteId(), "Hola", "Hola", getValidCategoryId(), true);
    }

    public void testUpdateNoteValid2() {
        testUpdateNote(getValidNoteId(), "Hola", "Hola", null, true);
    }

    public void testUpdateNoteInvalid1() {
        testUpdateNote(getValidNoteId(), null, "Hola", getValidCategoryId(), false);
    }

    public void testUpdateNoteInvalid2() {
        testUpdateNote(getValidNoteId(), "Hola", null, getValidCategoryId(), false);
    }

    public void testUpdateNoteInvalid3() {
        testUpdateNote(getValidNoteId(), "", "Hola", getValidCategoryId(), false);
    }

    public void testUpdateNoteInvalid4() {
        testUpdateNote(-1, "Hola", "Hola", getValidCategoryId(), false);
    }

    public void testUpdateNoteInvalid5() {
        testUpdateNote(getValidNoteId(), "Hola", "Hola", -1, false);
    }

    // fetchAllNotes
    public void testFetchAllNotesValid1() {
        testFetchAllNotes(NotesDbAdapter.KEY_TITLE, "categoría", Cursor);
    }

    public void testFetchAllNotesValid2() {
        testFetchAllNotes(NotesDbAdapter.KEY_CATEGORY, "", Cursor);
    }

    public void testFetchAllNotesValid3() {
        testFetchAllNotes(null, null, Cursor);
    }

    public void testFetchAllNotesInvalid1() {
        testFetchAllNotes("ERROR", null, null);
    }

    // fetchNote
    public void testFetchNoteValid1() {
        testFetchNote(getValidNoteId(), Cursor);
    }

    public void testFetchNoteInvalid1() {
        testFetchNote(-1, null);
    }

    // createCategory
    public void testCreateCategoryValid1() {
        testCreateCategory("Hola", 1);
    }

    public void testCreateCategoryInvalid1() {
        testCreateCategory(null, -1);
    }

    public void testCreateCategoryInvalid2() {
        testCreateCategory("", -1);
    }

    // deleteCategory
    public void testDeleteCategoryValid1() {
        testDeleteCategory(getValidCategoryId(), true);
    }

    public void testDeleteCategoryInvalid1() {
        testDeleteCategory(-1, false);
    }

    // fetchCategoryL
    public void testFetchCategoryLValid1() {
        testFetchCategoryL(getValidCategoryId(), Cursor);
    }

    public void testFetchCategoryLInvalid1() {
        testFetchCategoryL(-1, null);
    }

    // fetchCategoryS
    public void testFetchCategorySValid1() {
        testFetchCategoryS("Hola", 1);
    }

    public void testFetchCategorySInvalid1() {
        testFetchCategoryS(null, null);
    }

    public void testFetchCategorySInvalid2() {
        testFetchCategoryS("", null);
    }

    // updateCategory
    public void testUpdateCategoryValid1() {
        testUpdateCategory(getValidCategoryId(), "Hola", true);
    }

    public void testUpdateCategoryInvalid1() {
        testUpdateCategory(getValidCategoryId(), null, false);
    }

    public void testUpdateCategoryInvalid2() {
        testUpdateCategory(-1, "Hola", false);
    }

    public void testUpdateCategoryInvalid3() {
        testUpdateCategory(getValidCategoryId(), "", false);
    }

    // ------------------- TestManagers -------------------

    private void testCreateNote(String title, String body, Integer category, long result) {
        final long actual = adapter.createNote(title, body, category);
        if (result >= 0) {
            assertTrue("The note was not created when it should", actual >= 0);
        } else {
            assertEquals("The note was created when it shouldn't", -1, actual);
        }
    }


    private void testDeleteNote(long rowId, boolean result) {
        final boolean actual = adapter.deleteNote(rowId);
        if (result) {
            assertTrue("The note was deleted but it shouldn't", actual);
        } else {
            assertFalse("The note wasn't deleted, but it should", actual);
        }
    }

    private void testUpdateNote(long rowid, String title, String body, Integer category, boolean result) {
        final boolean actual = adapter.updateNote(rowid, title, body, category);
        if (result) {
            assertTrue("The note was updated but it shouldn't", actual);
        } else {
            assertFalse("The note wasn't updated but it should", actual);
        }
    }

    private void testFetchAllNotes(String orderBy, String category, ACursor result) {
        final Cursor actual = adapter.fetchAllNotes(orderBy, category);
        if (result != null) {
            assertNotNull("A cursor with notes wasn't returned but it should", actual);
        } else {
            assertNull("A cursor with notes was returned but it shouldn't", actual);
        }
    }

    private void testFetchNote(long rowId, ACursor result) {
        final Cursor actual = adapter.fetchNote(rowId);
        if (result != null) {
            assertNotNull("A cursor with a note wasn't returned but it should", actual);
        } else {
            assertNull("A cursor with a note was returned but it shouldn't", actual);
        }
    }

    private void testCreateCategory(String name, long result) {
        final long actual = adapter.createCategory(name);
        if (result >= 0) {
            assertTrue("The category was not created when it should", actual >= 0);
        } else {
            assertEquals("The note was created when it shouldn't", -1, actual);
        }
    }

    private void testDeleteCategory(long rowId, boolean result) {
        final boolean actual = adapter.deleteCategory(rowId);
        if (result) {
            assertTrue("The category was deleted but it shouldn't", actual);
        } else {
            assertFalse("The category wasn't deleted, but it should", actual);
        }
    }

    private void testFetchCategoryL(long rowId, ACursor result) {
        final Cursor actual = adapter.fetchCategory(rowId);
        if (result != null) {
            assertNotNull("A cursor with a category wasn't returned but it should", actual);
        } else {
            assertNull("A cursor with a category was returned but it shouldn't", actual);
        }
    }

    private void testFetchCategoryS(String name, Integer result) {
        final Integer actual = adapter.fetchCategory(name);
        if (result != null) {
            assertNotNull("A category id wasn't returned but it should", actual);
        } else {
            assertNull("A category id was returned but it shouldn't", actual);
        }
    }

    private void testUpdateCategory(long rowId, String name, boolean result) {
        final boolean actual = adapter.updateCategory(rowId, name);
        if (result) {
            assertTrue("The category was updated but it shouldn't", actual);
        } else {
            assertFalse("The category wasn't updated but it should", actual);
        }
    }

    // ------------------- Utils -------------------

    /**
     * The adapter to use
     */
    private NotesDbAdapter adapter;

    /**
     * Just a placeholder for a null/not_null cursor test (to match specifications instead of boolean)
     */
    private ACursor Cursor = new ACursor();

    private class ACursor {
    }

    private long getValidNoteId() {
        return adapter.createNote("testNote", "testNote", null);
    }

    private Integer getValidCategoryId() {
        return adapter.fetchCategory("testCategory");
    }

//    private void assertCrash(boolean crash, Runnable runnable){
//        try {
//            runnable.run();
//        }catch (Throwable e){
//            assertTrue("Expected not a crash but crashed",crash);
//            return;
//        }
//        assertFalse("Expected crash but found not a crash", crash);
//    }

    // ------------------- setUp/TearDown -------------------

    // to allow accesing the private field mDb and start/end transaction (see below)
    private Field privateField_mDb;

    {
        try {
            // set the field to accesible
            privateField_mDb = NotesDbAdapter.class.getDeclaredField("mDb");
            privateField_mDb.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUp() throws Exception {
        adapter = new NotesDbAdapter(getContext());

        // open database and start transaction
        adapter.open();
        ((SQLiteDatabase) privateField_mDb.get(adapter)).beginTransaction();
    }

    @Override
    public void tearDown() throws Exception {
        // end transaction without setting as successful (so changes are discarded), then close
        ((SQLiteDatabase) privateField_mDb.get(adapter)).endTransaction();
        adapter.close();
    }

}