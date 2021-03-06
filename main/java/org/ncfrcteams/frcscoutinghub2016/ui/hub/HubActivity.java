package org.ncfrcteams.frcscoutinghub2016.ui.hub;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.ncfrcteams.frcscoutinghub2016.R;
import org.ncfrcteams.frcscoutinghub2016.communication.sms_server.SmsReceiver;
import org.ncfrcteams.frcscoutinghub2016.ui.hub.fragments.HubContentsFragment;
import org.ncfrcteams.frcscoutinghub2016.ui.hub.fragments.HubListFragment;
import org.ncfrcteams.frcscoutinghub2016.ui.hub.fragments.HubManageFragment;
import org.ncfrcteams.frcscoutinghub2016.ui.hub.support.CustomViewPager;
import org.ncfrcteams.frcscoutinghub2016.ui.hub.support.HubPageAdapter;

import java.util.ArrayList;

public class HubActivity extends AppCompatActivity implements SmsReceiver.SmsListener,
        HubManageFragment.HubCreateFragListener, HubListFragment.HubListFragListener,
        HubContentsFragment.HubContentsFragListener, HubCreateMatchDialog.HubCreateDialogListener {

    private CustomViewPager hubViewPager;
    private HubPageAdapter hubPageAdapter;
    private boolean inDetailFrag;
    private boolean backpress;
    private SmsReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        hubViewPager = (CustomViewPager) findViewById(R.id.hubViewPager);
        hubViewPager.setPagingEnabled(false);
        setSupportActionBar(toolbar);

        inDetailFrag = false;

        hubPageAdapter = new HubPageAdapter(this, getSupportFragmentManager());
        hubViewPager.setAdapter(hubPageAdapter);
        hubViewPager.setCurrentItem(1);

        smsReceiver = new SmsReceiver(this);
        smsReceiver.register();
        smsReceiver.setSmsListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        backpress = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            smsReceiver.unregister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (inDetailFrag) {
            switchAwayFromDetailFrag(1);
        } else {
            if (backpress) {
                super.onBackPressed();
            } else {
                backpress = true;
                Toast.makeText(this, "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //******************************** HubActivity OnClick Listener ********************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hubpost:
                switchAwayFromDetailFrag(0);
                return true;
            case R.id.hubview:
                switchAwayFromDetailFrag(1);
                return true;
            case R.id.hubnew:
                switchAwayFromDetailFrag(1);
                ArrayList<String> matchTitles = hubPageAdapter.listView.mySchedule.getMatchTitles();
                new HubCreateMatchDialog(this, this, matchTitles).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //************************************ SmsReceiver Listener ************************************
    @Override
    public void smsReceived(String number, String message) {
        hubPageAdapter.addSMStoSchedule(number, message);
    }

    //********************************* HubManageFragment Listener *********************************
    @Override
    public void POSTRequest(String requestType, String extra) {
        hubPageAdapter.POSTRequest(requestType, extra);
    }

    @Override
    public void signin() {
        hubPageAdapter.signin();
    }

    //******************************** HubListViewFragment Listener ********************************
    @Override
    public void autopush() {
        hubPageAdapter.autopush();
    }

    //******************************** HubContentsFragment Listener ********************************
    @Override
    public void saveContents(int i) {
        switchAwayFromDetailFrag(i);
    }

    //******************************* HubContentsFragment.save() Call ******************************
    public void switchAwayFromDetailFrag(int i){
        inDetailFrag = false;
        //hubPageAdapter.content.save();
        hubViewPager.setCurrentItem(i);
    }

    //********************************** HubCreateMatchDialog Listener **********************************
    @Override
    public void onNewMatchCreate(int[] teams, int matchnum, boolean isQual, String phonenum) {
        hubPageAdapter.listView.addNewMatch(teams, matchnum, isQual, phonenum);
    }
}