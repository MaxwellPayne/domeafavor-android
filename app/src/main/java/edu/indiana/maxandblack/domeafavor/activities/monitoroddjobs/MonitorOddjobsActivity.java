package edu.indiana.maxandblack.domeafavor.activities.monitoroddjobs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.views.PinnedSectionListView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.andrest.AndrestClient;
import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;
import edu.indiana.maxandblack.domeafavor.models.users.MainUser;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public final class MonitorOddjobsActivity extends ActionBarActivity implements View.OnClickListener, PtrHandler {

    private final String TAG = "MonitorOddjobsActivity";

    private static final Semaphore canRequerySemaphore = new Semaphore(1);
    private static final AndrestClient domeafavorClient = new AndrestClient();

    PinnedSectionListView listView;
    PtrFrameLayout pullRefreshView;


    protected class OddjobSection {
        private final String sectionName;
        private final ArrayList<Oddjob> jobList;
        private final Oddjob.CompletionState completionState;

        private OddjobSection() {
            /* disable default constructor */
            System.exit(1);
            sectionName = null;
            jobList = null;
            completionState = null;
        }

        OddjobSection(String name, Oddjob.CompletionState completion, ArrayList<Oddjob> list) {
            sectionName = name;
            jobList = list;
            completionState = completion;
        }
    }

    protected final static int NUMBER_OF_SECTIONS = 6;
    protected OddjobSection[] oddjobSections = new OddjobSection[NUMBER_OF_SECTIONS];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_oddjobs);

        oddjobSections = new OddjobSection[] {
            /* jobs not assigned to a lackey */
                new OddjobSection(getString(R.string.monitor_oddjob_section_unassigned),
                        Oddjob.CompletionState.UNASSIGNED,
                        new ArrayList<Oddjob>()),

            /* jobs in progress */
                new OddjobSection(getString(R.string.monitor_oddjob_section_inprogress),
                        Oddjob.CompletionState.IN_PROGRESS,
                        new ArrayList<Oddjob>()),

            /* jobs where lackey marked complete */
                new OddjobSection(getString(R.string.monitor_oddjob_section_pendingpayment),
                        Oddjob.CompletionState.PENDING_PAYMENT,
                        new ArrayList<Oddjob>()),

            /* jobs that were completed and paid */
                new OddjobSection(getString(R.string.monitor_oddjob_section_completed),
                        Oddjob.CompletionState.COMPLETED,
                        new ArrayList<Oddjob>()),

            /* jobs where lackey marked complete */
                new OddjobSection(getString(R.string.monitor_oddjob_section_paymentdenied),
                        Oddjob.CompletionState.PAYMENT_DENIED,
                        new ArrayList<Oddjob>()),

            /* jobs where neither party considered it complete */
                new OddjobSection(getString(R.string.monitor_oddjob_section_expired),
                        Oddjob.CompletionState.EXPIRED,
                        new ArrayList<Oddjob>())
        };

        listView = (PinnedSectionListView) findViewById(R.id.monitorOddjobsListView);
        listView.setAdapter(new MonitorOddjobsListAdapter());

        pullRefreshView = (PtrFrameLayout) findViewById(R.id.monitorOddjobsPullRefresh);
        pullRefreshView.setPtrHandler(this);

        new RequeryAllTask().start();
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

    // TODO: implement and test handling of following PtrHandler methods
    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        RequeryAllTask requeryTask = new RequeryAllTask();
        requeryTask.start();

    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view2) {
        if (canRequerySemaphore.tryAcquire()) {
            canRequerySemaphore.release();
            return true;
        } else {
            return false;
        }
    }

    private boolean isHeader(int position) {
        Integer[] headerPositions = new Integer[NUMBER_OF_SECTIONS];
        int headerPosIdx = 0;
        int headerPos = 0;

        OddjobSection[] nonemptySections = getNonemptySections();
        for (OddjobSection section : nonemptySections) {
            headerPositions[headerPosIdx] = headerPos;
            headerPosIdx++;
            headerPos += section.jobList.size() + 1;
        }
        return Arrays.asList(headerPositions).contains(position);
    }

    private OddjobSection[] getNonemptySections() {
        int nonemptySectionCount = 0;
        for (OddjobSection section : oddjobSections) {
            if (section.jobList.size() > 0) {
                nonemptySectionCount += 1;
            }
        }
        OddjobSection[] nonemptySections = new OddjobSection[nonemptySectionCount];
        int insertionCounter = 0;
        for (OddjobSection section : oddjobSections) {
            if (section.jobList.size() > 0) {
                nonemptySections[insertionCounter] = section;
                insertionCounter++;
            }
        }
        return nonemptySections;
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

                if (convertView == null || convertView.getTag() instanceof Oddjob) {
                    /* have to create new view */
                    v = getLayoutInflater().inflate(R.layout.monitor_oddjobs_list_header, parent, false);
                }
                TextView monitorOddjobsHeaderName = (TextView) v.findViewById(R.id.monitorOddjobsHeaderName);
                monitorOddjobsHeaderName.setText(headerName);
            } else {
                /* list item */

                if (convertView == null || ! (convertView.getTag() instanceof Oddjob)) {
                    /* have to create new view */
                    v = getLayoutInflater().inflate(R.layout.oddjob_list_item, parent, false);
                }

                Oddjob listItem = (Oddjob) getItem(position);
                String viewText = listItem.get_id();
                TextView oddjobListItemIdTextView = (TextView) v.findViewById(R.id.oddjobListitemIdTextView);
                oddjobListItemIdTextView.setText(listItem.getTitle());


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
            OddjobSection[] nonemptySections = getNonemptySections();

            ArrayList<Oddjob>[] jobLists = new ArrayList[nonemptySections.length];
            String[] nonemptySectionNames = new String[nonemptySections.length];
            /* populate jobLists and nonemptySectionNames from nonemptySections */
            for (int i = 0; i < nonemptySections.length; i++) {
                jobLists[i] = nonemptySections[i].jobList;
                nonemptySectionNames[i] = nonemptySections[i].sectionName;
            }

            if (isHeader(position)) {
                int sectionNameIdx = 0;
                for (ArrayList<Oddjob> jobList : jobLists) {
                    /* subtract out count of jobs for each type of job */
                    if ( (position-1) < jobList.size()) {
                        return nonemptySectionNames[sectionNameIdx];
                    } else {
                        position -= (jobList.size() + 1);
                        sectionNameIdx++;
                    }
                }
                return nonemptySectionNames[sectionNameIdx];
            } else {
                for (ArrayList<Oddjob> jobList : jobLists) {
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
            OddjobSection[] nonemptySections = getNonemptySections();

            int count = 0;
            for (OddjobSection section : nonemptySections) {
                /* increment by one for the section header */
                count++;
                /* increment count by number of jobs in this section */
                count += section.jobList.size();
            }
            return count;
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null) {
            if (tag instanceof Oddjob) {
                /* transition to edit this oddjob */
                Oddjob selectedJob = (Oddjob) tag;
                Intent editOddjobIntent = new Intent(this, EditOddjobActivity.class);
                /* pass the selected job to the next activity */
                editOddjobIntent.putExtra(Oddjob.MAIN_SERIALIZED_ODDJOB_KEY, selectedJob);
                startActivity(editOddjobIntent);
            }
        }
    }

    private class RequeryAllTask extends Thread {
        @Override
        public void run() {
            if (canRequerySemaphore.tryAcquire()) {
                try {
                    String endpoint = getString(R.string.dmfv_delegated_byme,
                            getString(R.string.dmfv_host),
                            MainUser.getInstance().get_id().toString());
                    JSONArray jobsJson = domeafavorClient.getArray(endpoint);
                    final ArrayList<Oddjob> newJobs = new ArrayList<>(jobsJson.length());
                    /* coerce json to jobs, store in an array for later use on GUI thread */
                    for (int i = 0; i < jobsJson.length(); i++) {
                        Oddjob job = new Oddjob(jobsJson.getJSONObject(i));
                        newJobs.add(job);
                    }
                    /* only update data source arrays on GUI thread */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (OddjobSection section : oddjobSections) {
                                /* clear out all sections */
                                section.jobList.clear();
                            }
                            HashMap<Oddjob.CompletionState, ArrayList<Oddjob>> sectionMapping =
                                    new HashMap<>(NUMBER_OF_SECTIONS);
                            /* map each section using .completionState as the key */
                            for (OddjobSection section : oddjobSections) {
                                sectionMapping.put(section.completionState, section.jobList);
                            }
                            for (Oddjob job : newJobs) {
                                /* put each job in the array for section that matches its state */
                               ArrayList<Oddjob> correspondingArray = sectionMapping.get(job.getCompletionState());
                                correspondingArray.add(job);
                            }
                            /* update the GUI with new data */
                            MonitorOddjobsListAdapter listAdapter = (MonitorOddjobsListAdapter) listView.getAdapter();
                            listAdapter.notifyDataSetChanged();
                            if (listAdapter.getCount() == 0) {
                                /* are no oddjobs to monitor */
                                Toast.makeText(MonitorOddjobsActivity.this,
                                        getString(R.string.monitor_oddjob_no_jobs_exist),
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
                } catch (Exception e) {
                    // TODO: handle failed requery
                }

                /* always release the semaphore, regardless of what happened */
                canRequerySemaphore.release();
            }
        }
    }

    /**
     * Deprecated code; good example of one asynchronous task
     * managing/waiting on multithreaded sub-tasks
     */
    /*private Runnable requeryAll = new Runnable() {
        private CountDownLatch requeryCountdown = new CountDownLatch(oddjobSections.length);
        private final AndrestClient domeafavorClient = new AndrestClient();

        @Override
        public void run() {
            /* only execute if no other requery is in progress *
            if (canRequerySemaphore.tryAcquire()) {

                /* dispatch all sections on their own RequeryOne *
                for (OddjobSection section : oddjobSections) {
                    RequeryOne r = new RequeryOne(section);
                    new Thread(r).start();
                }
                try {
                    /* wait for all RequeryOnes to finish *
                    boolean didSucceed =
                            requeryCountdown.await(getResources().getInteger(R.integer.requery_timeout), TimeUnit.SECONDS);
                    if (didSucceed) {
                        /* update the GUI upon success *
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MonitorOddjobsListAdapter listAdapter = (MonitorOddjobsListAdapter) listView.getAdapter();
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                } catch (InterruptedException e) {
                    // TODO: handle interrupted exception because I don't know what that does
                } finally {
                    /* release the semaphore no matter what happened *
                    canRequerySemaphore.release();
                }
            }
        }

        /**
         * Requeries a single OddJobSection
         *
        class RequeryOne implements Runnable {
            private final OddjobSection section;

            public RequeryOne(OddjobSection sec) {
                section = sec;
            }

            @Override
            public void run() {
                Log.d(TAG, "Running a RequeryOne");
                try {
                    Log.d(TAG, String.format("Contacting host %s", section.requeryEndpoint));
                    /* get the new oddjobs, store them *
                    JSONArray newJobsJson =
                            domeafavorClient.getArray(section.requeryEndpoint);
                    ArrayList<Oddjob> newJobs = new ArrayList<>(newJobsJson.length());
                    for (int i = 0; i < newJobsJson.length(); i++) {
                        Oddjob job = new Oddjob(newJobsJson.getJSONObject(i));
                        newJobs.add(job);
                    }
                    /* notify the countdown of success on this thread *
                    requeryCountdown.countDown();
                    /* wait for all other RequeryOnes to finish *
                    boolean didSucceed =
                            requeryCountdown.await(getResources().getInteger(R.integer.requery_timeout), TimeUnit.SECONDS);
                    if (didSucceed) {
                        /* only refresh the data source if ALL oddjobs succeeded *
                        section.jobList.clear();
                        section.jobList.addAll(newJobs);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    };*/
}
