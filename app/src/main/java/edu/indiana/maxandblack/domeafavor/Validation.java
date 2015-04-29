package edu.indiana.maxandblack.domeafavor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Created by Zachary on 4/29/2015.
 */
public class Validation {

    public static boolean isValidDate(String dateToValidate, String dateFormat) {
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

    //takes a date "inputDate" and verifies that the date is after or equal to "afterDate"
    public static boolean dateIsTodayOrAfter(String inputStringDate, String dateFormat) {
        if (inputStringDate == null)
            return false;

        //make a simple date format
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        try {
            //get today as a simple date format
            Calendar cal = Calendar.getInstance();
            Date today = sdf.parse(sdf.format(cal.getTime()));

            //get input date as a simple date format
            Date inputDate = sdf.parse(inputStringDate);

            if (inputDate.after(today) || inputDate.equals(today)) {
                return true;
            }

            else {
                return false;
            }

        }
        catch (ParseException e) {
            return false;
        }
    }

    public static boolean isValidEmailAddress(String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            return false;
        }
        return true;
    }
}
