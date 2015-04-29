package edu.indiana.maxandblack.domeafavor.activities.createoddjob;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.models.datatypes.MongoDate;
import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;
import edu.indiana.maxandblack.domeafavor.models.users.MainUser;
import edu.indiana.maxandblack.domeafavor.Validation;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateOddjobFragment.CreateOddjobFragmentListener} interface
 * to handle interaction events.
 */
public class CreateOddjobFragment extends Fragment implements View.OnClickListener {

    private CreateOddjobFragmentListener mListener;
    private Oddjob newJob;

    private EditText jobTitleEditText;
    private EditText jobPriceEditText;
    private EditText jobDateEditText;
    private EditText jobDescriptionEditText;
    private Button createOddjobSubmitFormButton;
    private Button createOddjobCancelButton;
    private Button authorizeLackeysButton;
    private Button createOddjobAnotherLocationButton;
    private CheckBox createOddjobMyLocationCheckbox;


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

        jobTitleEditText = (EditText) v.findViewById(R.id.jobTitleEditText);
        jobPriceEditText = (EditText) v.findViewById(R.id.jobPriceEditText);
        jobDateEditText = (EditText) v.findViewById(R.id.jobDateEditText);
        jobDescriptionEditText = (EditText) v.findViewById(R.id.jobDescriptionEditText);
        createOddjobSubmitFormButton = (Button) v.findViewById(R.id.createOddjobSubmitFormButton);
        createOddjobCancelButton = (Button) v.findViewById(R.id.createOddjobCancelButton);
        authorizeLackeysButton = (Button) v.findViewById(R.id.authorizeLackeysButton);
        createOddjobAnotherLocationButton = (Button) v.findViewById(R.id.createOddjobAnotherLocationButton);
        createOddjobMyLocationCheckbox = (CheckBox) v.findViewById(R.id.createOddjobMyLocationCheckBox);

        createOddjobSubmitFormButton.setOnClickListener(this);
        createOddjobCancelButton.setOnClickListener(this);
        authorizeLackeysButton.setOnClickListener(this);
        createOddjobAnotherLocationButton.setOnClickListener(this);
        createOddjobMyLocationCheckbox.setOnClickListener(this);

        /* coerce locationCheckbox to initialize with MainUser's location */
        setJobLocation(null);

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
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setTitle("Invalid Registration Information.");
            //validateInput returns an empty string or an error message string
            alertDialogBuilder.setMessage(validateInput());
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }

            });

            AlertDialog alertDialog = alertDialogBuilder.create();

            //if there is invalid input show an alert dialog, otherwise construct the main user
            if (!validateInput().isEmpty())
                alertDialog.show();
            else
                submitOddjobForm();
        } else if (v == createOddjobCancelButton) {
            cancelOddjobCreation();
        } else if (v == authorizeLackeysButton) {
            mListener.launchFriendPicker();
        } else if (v == createOddjobAnotherLocationButton) {
            /* prompt user to select a different location */
            try {
                mListener.launchPlacePicker(this);
            } catch (GooglePlayServicesNotAvailableException
                     | GooglePlayServicesRepairableException e) {
                /* failed to get Google Play Services */
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else if (v == createOddjobMyLocationCheckbox) {
            /* fall back to the MainUser's location */
            setJobLocation(null);
        }
    }

    public void setJobLocation(Location loc) {
        boolean didPickACustomLoc = loc != null;
        /* use loc if exists, else fall back on MainUser's location */
        newJob.setKeyLocation( didPickACustomLoc ? loc : MainUser.getInstance().getLoc() );
        /* can click if not using MainUser loc */
        boolean checkboxIsClickable = didPickACustomLoc;
        /* checkbox will be checked when using MainUser's location */
        boolean isCheked = ! didPickACustomLoc;

        createOddjobMyLocationCheckbox.setClickable(checkboxIsClickable);
        createOddjobMyLocationCheckbox.setChecked(isCheked);
    }

    public String validateInput() {
        String errorMessage = "";
        //Job Titles cannot be blank
        if (jobTitleEditText.getText().toString().isEmpty()) {
            errorMessage += "Job Title must be filled out.\n";
        }
        //Job Prices cannot be blank or less than zero (The soft keyboard
        //should prevent negative numbers however)
        if (jobPriceEditText.getText().toString().isEmpty()) {
            errorMessage += "Job Price must be filled out.\n";
        }
        else if (Integer.parseInt(jobPriceEditText.getText().toString()) < 0) {
            errorMessage += "Job Price must be a positive integer.\n";
        }
        //Job Date must not be blank, a valid date, and be the current date or later
        if (jobDateEditText.getText().toString().isEmpty()) {
            errorMessage += "Job Date must be filled out.\n";
        }
        else if (!Validation.isValidDate(jobDateEditText.getText().toString(), "MM/dd/yyyy")) {
            errorMessage += "Job Date must be a proper date of the format \"mm/dd/yyyy\"\n";
        }

        else if (!Validation.dateIsTodayOrAfter(jobDateEditText.getText().toString(), "MM/dd/yyyy")) {
            errorMessage += "Job Date must be today or later.\n";
        }

        if (jobDescriptionEditText.getText().toString().isEmpty()) {
            errorMessage += "Job Description must be filled out.\n";
        }

        return errorMessage;
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
        //newJob.setKeyLocation(MainUser.getInstance().getLoc());

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
        public void launchPlacePicker(CreateOddjobFragment sender) throws
                GooglePlayServicesNotAvailableException,
                GooglePlayServicesRepairableException;
    }

}
