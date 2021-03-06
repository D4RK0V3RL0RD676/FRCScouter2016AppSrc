package org.ncfrcteams.frcscoutinghub2016.ui.hub.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ncfrcteams.frcscoutinghub2016.R;
import org.ncfrcteams.frcscoutinghub2016.matchdata.bluealliance.EventSelectorDialog;

public class HubManageFragment extends Fragment implements View.OnClickListener, EventSelectorDialog.EventScheduleListener {

    private HubCreateFragListener mListener;
    public HubManageFragment() {
    }

    public static HubManageFragment newInstance() {
        return new HubManageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.h_frag_manage, container, false);

        view.findViewById(R.id.uploadDatabase).setOnClickListener(this);
        view.findViewById(R.id.downloadDatabase).setOnClickListener(this);
        view.findViewById(R.id.setupSchedule).setOnClickListener(this);
        view.findViewById(R.id.signin).setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HubCreateFragListener) {
            mListener = (HubCreateFragListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HubCreateFragListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.uploadDatabase:
                mListener.POSTRequest("database", "extra");
                break;
            case R.id.downloadDatabase:
                mListener.POSTRequest("download", "extra");
                break;
            case R.id.setupSchedule:
                EventSelectorDialog dialog = new EventSelectorDialog(getContext(),this);
                dialog.show();
                break;
            case R.id.signin:
                mListener.signin();
                break;
            default:
                break;
        }
    }

    @Override
    public void scheduleListener(String schedule) {
        mListener.POSTRequest("upload", schedule);
    }

    public interface HubCreateFragListener {
        void POSTRequest(String requestType, String extra);
        void signin();
    }
}
