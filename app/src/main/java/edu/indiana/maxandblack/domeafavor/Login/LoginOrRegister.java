package edu.indiana.maxandblack.domeafavor.Login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import android.view.View.OnClickListener;
import android.util.Log;


import edu.indiana.maxandblack.domeafavor.activities.register.*;
import edu.indiana.maxandblack.domeafavor.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLoginOrRegisterInteractionListener} interface
 * to handle interaction events.
 */
public class LoginOrRegister extends Fragment implements OnClickListener {

    private OnLoginOrRegisterInteractionListener mListener;
    private static final String TAG = "LoginOrRegisterFragment";
    private UiLifecycleHelper uiHelper;


    public LoginOrRegister() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_login_or_register, container, false);

        LoginButton facebookLoginButton = (LoginButton) view.findViewById(R.id.facebookLoginButton);
        facebookLoginButton.setFragment(this);
        facebookLoginButton.setReadPermissions(getResources().getStringArray(R.array.fb_read_permissions));
        Button otherLoginButton = (Button) view.findViewById(R.id.otherLoginButton);

        facebookLoginButton.setOnClickListener(this);
        otherLoginButton.setOnClickListener(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            //onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLoginOrRegisterInteractionListener) activity;
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
        Intent intent = new Intent(LoginOrRegister.this.getActivity(), Register.class);
        startActivity(intent);

    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            mListener.onFacebookLoginSuccess(this, session);
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            if (exception == null) {
                mListener.onFacebookLogout(this, session);
            } else {
                mListener.onFacebookLoginFailure(this, session, exception);
            }
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
    public interface OnLoginOrRegisterInteractionListener {
        // TODO: Update argument type and name
        public void onFacebookLoginSuccess(LoginOrRegister loginFragment, Session session);
        public void onFacebookLoginFailure(LoginOrRegister loginFragment, Session session, Exception exception);
        public void onFacebookLogout(LoginOrRegister loginFragment, Session session);
    }

}
