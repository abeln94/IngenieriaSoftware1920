package es.unizar.eina.notepadvT;

import android.test.AndroidTestCase;

public class NotesDbAdapterTest extends AndroidTestCase {
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    // ------------------- Tests -------------------

    public void testCreateNoteValid1() {
        testCreateNote("Hola", "Hola", 1);
    }
    public void testCreateNoteInvalid1() {
        testCreateNote(null, "Hola", -1);
    }
    public void testCreateNoteInvalid2() {
        testCreateNote("Hola", null, -1);
    }
    public void testCreateNoteInvalid3() {
        testCreateNote("", "Hola", -1);
    }

    public void testDeleteNoteValid1(){
        testDeleteNote(getValidNote(), true);
    }
    public void testDeleteNoteInvalid1(){
        testDeleteNote(-1, false);
    }

    public void testUpdateNotevalid1(){
        testUpdateNote("Hola", "Hola", getValidNote(), true);
    }
    public void testUpdateNoteInvalid1(){
        testUpdateNote(null, "Hola", getValidNote(), false);
    }
    public void testUpdateNoteInvalid2(){
        testUpdateNote("Hola", null, getValidNote(), false);
    }
    public void testUpdateNoteInvalid3(){
        testUpdateNote("", "Hola", getValidNote(), false);
    }
    public void testUpdateNoteInvalid4(){
        testUpdateNote("Hola", "Hola", -1, false);
    }

    // ------------------- TestManagers -------------------


    private void testCreateNote(String title, String body, int result){
        final long actual = getAdapter().createNote(title, body, null);
        if(result >= 0){
            assertTrue( "The note was not created when it should", actual>=0);
        }else{
            assertEquals("The note was created when it shouldn't", -1, actual);
        }
    }


    private void testDeleteNote(long rowId, boolean result) {
        final boolean actual = getAdapter().deleteNote(rowId);
        if(result){
            assertTrue("The note was deleted but it shouldn't", actual);
        }else{
            assertFalse("The note wasn't deleted, but it should", actual);
        }
    }

    private void testUpdateNote(String title, String body, long rowid, boolean result) {
        final boolean actual = getAdapter().updateNote(rowid, title, body, null);
        if(result){
            assertTrue("The note was updated but it shouldn't", actual);
        }else{
            assertFalse("The note wasn't updated but it should", actual);
        }
    }

    // ------------------- Utils -------------------

    private NotesDbAdapter getAdapter(){
        final NotesDbAdapter adapter = new NotesDbAdapter(getContext());
        adapter.open();
        return adapter;
    }

    private long getValidNote(){
        return getAdapter().createNote("a", "a", null);
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
}