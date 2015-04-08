package edu.indiana.maxandblack.domeafavor.activities.createoddjob;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.activities.selectfriends.SelectFriendsActivity;
import edu.indiana.maxandblack.domeafavor.models.datatypes.MongoDate;
import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;
import edu.indiana.maxandblack.domeafavor.models.users.MainUser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateOddjobFragment.CreateOddjobFragmentListener} interface
 * to handle interaction events.
 */
public class CreateOddjobFragment extends Fragment implements View.OnClickListener {

    private CreateOddjobFragmentListener mListener;
    private Oddjob newJob;

    private Button createOddjobSubmitFormButton;
    private Button createOddjobCancelButton;
    private Button authorizeLackeysButton;

    private static Random generator = new Random();

    public CreateOddjobFragment() {
        // Required empty public constructor
        newJob = new Oddjob();
    }

    private static String randString(int maxLen) {
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(maxLen);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_oddjob, container, false);

        createOddjobSubmitFormButton = (Button) v.findViewById(R.id.createOddjobSubmitFormButton);
        createOddjobCancelButton = (Button) v.findViewById(R.id.createOddjobCancelButton);
        authorizeLackeysButton = (Button) v.findViewById(R.id.authorizeLackeysButton);

        createOddjobSubmitFormButton.setOnClickListener(this);
        createOddjobCancelButton.setOnClickListener(this);
        authorizeLackeysButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CreateOddjobFragmentListener) activity;
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
    public void onClick(View v) {
        /* intercept button clicks */
        if (v == createOddjobSubmitFormButton) {
            submitOddjobForm();
        } else if (v == createOddjobCancelButton) {
            cancelOddjobCreation();
        } else if (v == authorizeLackeysButton) {
            mListener.launchFriendPicker();
        }
    }

    private void randomlyGenerateOddjobData() {
        /* @hack - Randomly generating oddjob data here for debug purposes, UI not yet designed */
        newJob.setSolicitorId(MainUser.getInstance().get_id());
        newJob.setTitle(randString(250));
        newJob.setDescription(randString(1000));
        Calendar expiryCalendar = Calendar.getInstance();
        expiryCalendar.setTime(new Date());
        final int HOURS_IN_YEAR = 8766;
        expiryCalendar.add(Calendar.HOUR_OF_DAY, generator.nextInt() % (HOURS_IN_YEAR * 2));
        newJob.setExpiry(new MongoDate(expiryCalendar.getTime()));

        final float maxCash = 200.0f, minCash = 0.0f;
        newJob.setPrice(Math.random() * (maxCash - minCash) + minCash);
        newJob.setKeyLocation(MainUser.getInstance().getLoc());

    }

    private void submitOddjobForm() {
        randomlyGenerateOddjobData();
        // TODO: Replace random oddjob generation here with data from form widgets
        mListener.onOddjobFormCompletion(newJob);
    }

    private void cancelOddjobCreation() {
        mListener.onCreateOddjobCancel();
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
    public interface CreateOddjobFragmentListener {
        public void onOddjobFormCompletion(Oddjob oddjob);
        public void onCreateOddjobCancel();
        public void launchFriendPicker();
    }

}
