//Register class

package edu.indiana.maxandblack.domeafavor.activities.register;

import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Button;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.Context;

import com.sun.mail.smtp.SMTPAddressSucceededException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.models.users.MainUser;

public class Register extends ActionBarActivity {
    //views variable declarations
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText birthdayEditText;
    private EditText emailEditText;
    private CheckBox locationCheckBox;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show the fragment_register layout
        setContentView(R.layout.fragment_register);

        //get references to all the views in the fragment_register layout
        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        birthdayEditText = (EditText) findViewById(R.id.birthdayEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        locationCheckBox = (CheckBox) findViewById(R.id.locationCheckBox);
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(submitButtonListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    //Submit Button Listener
    public OnClickListener submitButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Register.this);

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
}

