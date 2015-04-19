package edu.indiana.maxandblack.domeafavor.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.models.users.MainUser;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.RegisterFragmentListener} interface
 * to handle interaction events.
 */
public class RegisterFragment extends Fragment {
    //views variable declarations
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText birthdayEditText;
    private EditText emailEditText;
    private CheckBox locationCheckBox;
    private Button submitButton;

    private RegisterFragmentListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        //get references to all the views in the fragment_register layout
        firstNameEditText = (EditText) v.findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) v.findViewById(R.id.lastNameEditText);
        birthdayEditText = (EditText) v.findViewById(R.id.birthdayEditText);
        emailEditText = (EditText) v.findViewById(R.id.emailEditText);
        locationCheckBox = (CheckBox) v.findViewById(R.id.locationCheckBox);
        submitButton = (Button) v.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(submitButtonListener);

        return v;
    }

    public View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

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
            else {
                MainUser.getInstance().setFirstName(firstNameEditText.getText().toString());
                MainUser.getInstance().setLastName(lastNameEditText.getText().toString());
            }
        }
    };

    public String validateInput() {
        String errorMessage = "";

        //Check to see if all edittexts are non empty
        //and if they are not validate the input they have
        if (firstNameEditText.getText().toString().isEmpty())
            errorMessage += "First Name must be filled out.\n";
        else if (!firstNameEditText.getText().toString().matches("[A-Za-z]+"))
            errorMessage += "First Name must be composed of only letters.\n";
        if (lastNameEditText.getText().toString().isEmpty())
            errorMessage += "Last Name must be filled out.\n";
        else if (!lastNameEditText.getText().toString().matches("[a-zA-Z]+"))
            errorMessage += "Last Name must be composed of only letters.\n";
        if (birthdayEditText.getText().toString().isEmpty())
            errorMessage += "Your birthday must be filled out.\n";
        else if (!isValidDate(birthdayEditText.getText().toString(), "MM/dd/yyyy"))
            errorMessage += "Birthday must be a proper date of the format \"mm/dd/yyyy\n";
        if (emailEditText.getText().toString().isEmpty())
            errorMessage += "Your email must be filled out.\n";
        else if (!isValidEmailAddress(emailEditText.getText().toString()))
            errorMessage += "You must enter a valid email address.";

        return errorMessage;
    }

    public boolean isValidDate(String dateToValidate, String dateFormat) {
        if (dateToValidate == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        try {
            //if the date is invalid a ParseException is thrown
            Date date = sdf.parse(dateToValidate);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public boolean isValidEmailAddress(String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            return false;
        }
        return true;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RegisterFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RegisterFragmentListener");
        }

        submitForm();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void submitForm() {
        mListener.onRegisterFormCompletion();
    }

    private void dismiss() {
        mListener.onFormDismiss();
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
    public interface RegisterFragmentListener {
        /* user finished filling form, ready to create user */
        public void onRegisterFormCompletion();
        public void onFormDismiss();
    }

}
