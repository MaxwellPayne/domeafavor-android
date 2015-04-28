package edu.indiana.maxandblack.domeafavor.activities.findoddjob;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OddjobFragment.OddjobFragmentListener} interface
 * to handle interaction events.
 */
public class OddjobFragment extends Fragment implements View.OnClickListener {

    private OddjobFragmentListener mListener;
    private Oddjob job;

    /* UI widgets */
    private TextView oddjobTitleTextView;
    private TextView oddjobExpiresDateTextView;
    private TextView oddjobPriceTextView;
    private Button   oddjobViewLocationButton;
    private Button   oddjobViewChatroomButton;

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

        oddjobExpiresDateTextView = (TextView) v.findViewById(R.id.oddjobExpiresDateTextView);
        String formattedExpiry = new SimpleDateFormat("hh:mm a MMM dd", Locale.US)
                                        .format(job.getExpiry());
        oddjobExpiresDateTextView.setText(formattedExpiry);

        oddjobPriceTextView = (TextView) v.findViewById(R.id.oddjobPriceTextView);
        /* @hack - .getCurrencyInstance() DOES NOT guarantee consistency with server's dollar currency */
        NumberFormat dollarFormatter = NumberFormat.getCurrencyInstance();
        oddjobPriceTextView.setText(dollarFormatter.format(job.getPrice()));

        oddjobViewLocationButton = (Button) v.findViewById(R.id.oddjobViewLocationButton);
        oddjobViewLocationButton.setOnClickListener(this);

        oddjobViewChatroomButton = (Button) v.findViewById(R.id.oddjobViewChatroomButton);
        oddjobViewChatroomButton.setOnClickListener(this);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
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

    @Override
    public void onClick(View v) {
        if (v == oddjobViewLocationButton) {
            /* user wants to view this oddjob's location */
            mListener.onLocationClick(job.getKeyLocation());
        } else if (v == oddjobViewChatroomButton) {
            /* user wants to view this oddjob's chat */
            mListener.onCommentClick(job);
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
    public interface OddjobFragmentListener {
        public void onLocationClick(Location loc);

        public void onCommentClick(Oddjob job);
    }
}
