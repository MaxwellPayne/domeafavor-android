package edu.indiana.maxandblack.domeafavor;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.model.GraphUser;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link edu.indiana.maxandblack.domeafavor.RegisterFragment.RegisterFragmentListener} interface
 * to handle interaction events.
 */
public class RegisterFragment extends Fragment {

    private RegisterFragmentListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
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
    }

}
