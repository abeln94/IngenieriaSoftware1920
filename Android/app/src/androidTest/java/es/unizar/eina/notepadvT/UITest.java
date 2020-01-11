package es.unizar.eina.notepadvT;


import android.app.Activity;
import android.database.Cursor;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.action.AdapterViewProtocol;
import android.support.test.espresso.core.deps.guava.base.Optional;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.LinkedList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.AdapterViewProtocols.standardProtocol;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UITest {

    @Rule
    public ActivityTestRule<NotepadvT> mActivityTestRule = new ActivityTestRule<>(NotepadvT.class);

    @Test
    public void createNoteTest() {
        createNote("note_to_create", "body", "category");

        onLastItemOfListView(withId(R.id.list))
                .onChildView(withId(R.id.text1))
                .check(matches(withText("note_to_create")));
    }

    @Test
    public void editNoteTest() {
        createNote("note_to_edit", ".", "");

        onLastItemOfListView(withId(R.id.list))
                .perform(longClick());

        onView(withText("Edit Note"))
                .perform(click());

        onView(withId(R.id.title))
                .perform(replaceText("note_to_edit_edited"), closeSoftKeyboard());

        onView(withId(R.id.confirm))
                .perform(click());

        onLastItemOfListView(withId(R.id.list))
                .onChildView(withId(R.id.text1))
                .check(matches(withText("note_to_edit_edited")));
    }

    @Test
    public void deleteNoteTest() {
        createNote("note_to_delete", ".", "");

        onLastItemOfListView(withId(R.id.list))
                .perform(longClick());

        onView(withText("Delete Note"))
                .perform(click());

        onLastItemOfListView(withId(R.id.list))
                .onChildView(withId(R.id.text1))
                .check(matches(not(withText("note_to_delete"))));
    }


    @Test
    public void testLotsOfNotesUI() {

        final NotepadvT activity = (NotepadvT) getActivityInstance();
        final NotesDbAdapter notesDbAdapter = Reflection.getPrivate(activity, "mDbHelper");

        // delete all notes
        Cursor cursor = notesDbAdapter.fetchAllNotes(null, null);
        while(cursor.moveToNext()){
            notesDbAdapter.deleteNote(cursor.getLong(0));
        }

        // create 1000 notes
        for (int i = 0; i < 1000; ++i) {
            notesDbAdapter.createNote("volume_" + i, "n" + i, null);
        }

        // create and edit one note
        editNoteTest();

        // edit note 75
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(75)
                .perform(longClick());

        onView(withText("Edit Note"))
                .perform(click());

        onView(withId(R.id.title))
                .perform(replaceText("note_75_edited"), closeSoftKeyboard());

        onView(withId(R.id.confirm))
                .perform(click());

        // edit note 76
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(76)
                .perform(longClick());

        onView(withText("Edit Note"))
                .perform(click());

        onView(withId(R.id.title))
                .perform(replaceText("note_76_edited"), closeSoftKeyboard());

        onView(withId(R.id.confirm))
                .perform(click());

        // delete all notes
        cursor = notesDbAdapter.fetchAllNotes(null, null);
        while(cursor.moveToNext()){
            notesDbAdapter.deleteNote(cursor.getLong(0));
        }
    }


    // ------------------- Common -------------------

    private void createNote(String title, String body, String category) {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText("Add Item"))
                .perform(click());

        onView(withId(R.id.title))
                .perform(replaceText(title), closeSoftKeyboard());
        onView(withId(R.id.category))
                .perform(replaceText(category), closeSoftKeyboard());
        onView(withId(R.id.body))
                .perform(replaceText(body), closeSoftKeyboard());

        onView(withId(R.id.confirm))
                .perform(click());
    }

    // ------------------- Utils -------------------


    private DataInteraction onLastItemOfListView(Matcher<View> listViewMatcher) {
        return onData(anything()).inAdapterView(listViewMatcher).usingAdapterViewProtocol(new ReverseProtocol()).atPosition(0);
    }

    private class ReverseProtocol implements AdapterViewProtocol {
        private final AdapterViewProtocol delegate = standardProtocol();

        @Override
        public Iterable getDataInAdapterView(AdapterView<? extends Adapter> av) {
            LinkedList result = new LinkedList<>();
            for (AdaptedData data : delegate.getDataInAdapterView(av)) {
                result.addFirst(data);
            }
            return result;
        }

        @Override
        public Optional getDataRenderedByView(AdapterView<? extends Adapter> av, View v) {
            return delegate.getDataRenderedByView(av, v);
        }

        @Override
        public void makeDataRenderedWithinAdapterView(AdapterView<? extends Adapter> adapterView, AdaptedData data) {
            delegate.makeDataRenderedWithinAdapterView(adapterView, data);
        }

        @Override
        public boolean isDataRenderedWithinAdapterView(AdapterView<? extends Adapter> adapterView, AdaptedData adaptedData) {
            return delegate.isDataRenderedWithinAdapterView(adapterView, adaptedData);
        }
    }

    public Activity getActivityInstance() {
        final Activity[] currentActivity = {null};
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivities =
                        ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                for (Activity activity : resumedActivities) {
                    Log.d("Your current activity: ", activity.getClass().getName());
                    currentActivity[0] = activity;
                    break;
                }
            }
        });

        return currentActivity[0];
    }
}
