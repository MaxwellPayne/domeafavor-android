package edu.indiana.maxandblack.domeafavor;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link edu.indiana.maxandblack.domeafavor.OddjobFragment.OddjobFragmentListener} interface
 * to handle interaction events.
 */
public class OddjobFragment extends Fragment {

    private OddjobFragmentListener mListener;
    private Oddjob job;

    /* UI widgets */
    private TextView oddjobTitleTextView;

    public OddjobFragment() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(Bundle args) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_oddjob, container, false);

        /* attach oddjob data to widgets */
        oddjobTitleTextView = (TextView) v.findViewById(R.id.oddjobTitleTextView);
        oddjobTitleTextView.setText(job.getTitle());

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OddjobFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OddjobFragmentListener");
        }
        if (job == null) {
            throw new AssertionError("Must give OddjobFragment a job before attaching");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setJob(Oddjob j) {
        job = j;
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
    public interface OddjobFragmentListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
