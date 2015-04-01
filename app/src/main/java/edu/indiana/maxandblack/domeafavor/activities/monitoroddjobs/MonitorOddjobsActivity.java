package edu.indiana.maxandblack.domeafavor.activities.monitoroddjobs;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.views.PinnedSectionListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;

public class MonitorOddjobsActivity extends ActionBarActivity implements View.OnClickListener {

    private String[] sectionNames;
    private ArrayList<Dummy> inProgressJobs = new ArrayList<>();
    private ArrayList<Dummy> solicitorFinishedJobs = new ArrayList<>();
    private ArrayList<Dummy> lackeyFinishedJobs = new ArrayList<>();
    private ArrayList<Dummy> finishedJobs = new ArrayList<>();

    private class Dummy {
        int name;
        Dummy(int x) {
            name = x;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_oddjobs);

        sectionNames = getResources().getStringArray(R.array.monitor_oddjobs_sections);

        PinnedSectionListView listView = (PinnedSectionListView) findViewById(R.id.monitorOddjobsListView);
        listView.setAdapter(new MonitorOddjobsListAdapter());


        for (int i=0; i< 3; i++) {
            inProgressJobs.add(new Dummy(i));
        }
        for (int i=3; i<3; i++) {
            solicitorFinishedJobs.add(new Dummy(i));
        }
        for (int i=3; i < 4; i++) {
            lackeyFinishedJobs.add(new Dummy(i));
        }

        for (int i=4; i<27; i++) {
            finishedJobs.add(new Dummy(i));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_monitor_oddjobs, menu);
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

    private boolean isHeader(int position) {
        Integer[] headerPositions = {null, null, null, null};
        int headerPosIdx = 0;
        int headerPos = 0;

        ArrayList<Dummy>[] nonempties = getNonemptyLists();
        for (ArrayList<Dummy> nonempty : nonempties) {
            if (nonempty.size() > 0) {
                headerPositions[headerPosIdx] = headerPos;
                headerPosIdx++;
                headerPos += nonempty.size() + 1;
            }
        }
        return Arrays.asList(headerPositions).contains(position);
    }

    private ArrayList<Dummy>[] getNonemptyLists() {
        ArrayList<Dummy>[] jobLists = new ArrayList[] {
                inProgressJobs,
                solicitorFinishedJobs,
                lackeyFinishedJobs,
                finishedJobs
        };
        int nonemptyListCount = 0;
        for (ArrayList<Dummy> jobList : jobLists) {
            if (jobList.size() > 0) {
                nonemptyListCount += 1;
            }
        }
        ArrayList<Dummy>[] retVal = new ArrayList[nonemptyListCount];
        int insertionCounter = 0;
        for (ArrayList<Dummy> jobList : jobLists) {
            if (jobList.size() > 0) {
                retVal[insertionCounter] = jobList;
                insertionCounter += 1;
            }
        }
        return retVal;
    }

    ArrayList<String> getNonemptySectionNames() {
        ArrayList<String> retVal = new ArrayList<>(sectionNames.length);
        ArrayList<Dummy>[] allJobLists = new ArrayList[] {
                inProgressJobs,
                solicitorFinishedJobs,
                lackeyFinishedJobs,
                finishedJobs
        };
        int iterCounter = 0;
        for (ArrayList<Dummy> jobList : allJobLists) {
            if (jobList.size() > 0) {
                retVal.add(sectionNames[iterCounter]);
            }
            iterCounter++;
        }
        return retVal;
    }

    private class MonitorOddjobsListAdapter extends BaseAdapter
            implements PinnedSectionListView.PinnedSectionListAdapter {

        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_LISTITEM = 1;

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == VIEW_TYPE_HEADER;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            final int viewType = getItemViewType(position);
            if (viewType == VIEW_TYPE_HEADER) {
                String headerName = (String) getItem(position);

                if (convertView == null || convertView.getTag() instanceof Dummy) {
                    /* have to create new view */
                    v = getLayoutInflater().inflate(R.layout.monitor_oddjobs_list_header, parent, false);
                }
                TextView monitorOddjobsHeaderName = (TextView) v.findViewById(R.id.monitorOddjobsHeaderName);
                monitorOddjobsHeaderName.setText(headerName);
            } else {
                /* list item */

                if (convertView == null || ! (convertView.getTag() instanceof Dummy)) {
                    /* have to create new view */
                    v = getLayoutInflater().inflate(R.layout.oddjob_list_item, parent, false);
                }

                Dummy listItem = (Dummy) getItem(position);
                String viewText = Integer.toString(listItem.name);
                TextView oddjobListItemIdTextView = (TextView) v.findViewById(R.id.oddjobListitemIdTextView);
                oddjobListItemIdTextView.setText(viewText);

                v.setTag(listItem);
            }
            v.setOnClickListener(MonitorOddjobsActivity.this);
            return v;
        }

        @Override
        public long getItemId(int position) {
            /* not implemented */
            return -1;
        }

        @Override
        public Object getItem(int position) {
            /* list of all job lists */
            ArrayList<Dummy>[] jobLists = getNonemptyLists();
            ArrayList<String> nonemptySectionNames = getNonemptySectionNames();

            if (isHeader(position)) {
                int sectionNameIdx = 0;
                for (ArrayList<Dummy> jobList : jobLists) {
                    /* subtract out count of jobs for each type of job */
                    if ( (position-1) < jobList.size()) {
                        return nonemptySectionNames.get(sectionNameIdx);
                    } else {
                        position -= (jobList.size() + 1);
                        sectionNameIdx++;
                    }
                }
                return nonemptySectionNames.get(sectionNameIdx);
            } else {
                for (ArrayList<Dummy> jobList : jobLists) {
                    if ( (position-1) < jobList.size()) {
                        return jobList.get(position-1);
                    } else {
                        position -= (jobList.size() + 1);
                    }
                }
            }
            /* ERROR - should never happen */
            System.exit(1);
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            if (isHeader(position)) {
                return VIEW_TYPE_HEADER;
            } else {
                return VIEW_TYPE_LISTITEM;
            }
        }

        @Override
        public int getCount() {
            /* count of all jobs + the four headers */
            int count = 0;
            for (ArrayList<Dummy> nonemptyJobList : getNonemptyLists()) {
                /* add all jobs to the count */
                count += nonemptyJobList.size();
            }
            /* add all sections to the count */
            count += getNonemptySectionNames().size();
            return count;
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null) {
            if (tag instanceof Dummy) {
                Toast.makeText(this,
                        Integer.toString(((Dummy) tag).name),
                        Toast.LENGTH_LONG).show();


            }
        }
    }
}
