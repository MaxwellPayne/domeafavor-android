package edu.indiana.maxandblack.domeafavor.activities.findoddjob;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OddjobListFragment.OddjobListFragmentListener}
 * interface.
 */
public class OddjobListFragment extends ListFragment {

    private OddjobListFragmentListener mListener;
    private ArrayList<Oddjob> oddjobs = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OddjobListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oddjobs = new ArrayList<Oddjob>();
        setListAdapter(new OddjobsAdapter(getActivity(), oddjobs));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OddjobListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onOddjobSelect(oddjobs.get(position));
        }
    }

    public void setOddjobs(ArrayList<Oddjob> oddjobsList) {
        /* change the data set and notify the adapter */
        oddjobs.clear();
        oddjobs.addAll(oddjobsList);
        OddjobsAdapter adapter = (OddjobsAdapter) getListAdapter();
        adapter.notifyDataSetChanged();
    }

    /**
     * Custom Adapter used to return oddjob_list_item views
     * at various indices in this ListFragment
     */
    private class OddjobsAdapter extends ArrayAdapter<Oddjob> {

        /** what reused convertView will look like when passed to getView **/
        private class CachedView {
            TextView idTextView;
            TextView titleTextView;
        }

        public OddjobsAdapter(Context context, ArrayList<Oddjob> oddjobList) {
            super(context, R.layout.oddjob_list_item, oddjobList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Oddjob job = getItem(position);
            CachedView cachedView;
            if (convertView != null) {
                /* reuse convertView as a CachedView */
                cachedView = (CachedView) convertView.getTag();
            } else {
                /* create a completely new view */
                cachedView = new CachedView();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.oddjob_list_item, parent, false);
                cachedView.idTextView = (TextView) convertView.findViewById(R.id.oddjobListitemIdTextView);
                cachedView.titleTextView = (TextView) convertView.findViewById(R.id.oddjobListitemTitleTextView);
                convertView.setTag(cachedView);
            }
            View returnView = convertView;
            /* editing the CachedView here will change properties on the returnView */
            String ID = job.get_id();
            cachedView.idTextView.setText(job.get_id());
            cachedView.titleTextView.setText(job.getTitle());

            return returnView;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OddjobListFragmentListener {
        public void onOddjobSelect(Oddjob job);
    }

}
