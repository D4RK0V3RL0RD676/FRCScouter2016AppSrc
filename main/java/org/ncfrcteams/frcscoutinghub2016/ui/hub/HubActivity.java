package org.ncfrcteams.frcscoutinghub2016.ui.hub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.ncfrcteams.frcscoutinghub2016.R;
import org.ncfrcteams.frcscoutinghub2016.communication.http.PostClass;
import org.ncfrcteams.frcscoutinghub2016.communication.sms_server.SmsReceiver;
import org.ncfrcteams.frcscoutinghub2016.matchdata.schedule.Match;
import org.ncfrcteams.frcscoutinghub2016.ui.CustomPageAdapter;
import org.ncfrcteams.frcscoutinghub2016.ui.CustomViewPager;

import java.util.ArrayList;

public class HubActivity extends AppCompatActivity implements SmsReceiver.SmsListener,
         HubCreateFragment.HubCreateFragListener, HubListFragment.HubListFragListener,
        HubContentsFragment.HubContentsFragListener{

    private CustomViewPager hubViewPager;
    private CustomPageAdapter myPageAdapter;
    private String user = "test";
    private String pass = "test";
    private boolean inDetailFrag = false;
    private boolean backpress;
    private SmsReceiver smsReceiver;

    private HubCreateFragment createFragment;
    private HubContentsFragment contentsFragment;
    private HubListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        hubViewPager = (CustomViewPager) findViewById(R.id.hubViewPager);
        hubViewPager.setPagingEnabled(false);
        setSupportActionBar(toolbar);

        ArrayList<Fragment> fragments = new ArrayList<>();
        createFragment = HubCreateFragment.newInstance();
        contentsFragment = HubContentsFragment.newInstance();
        listFragment = HubListFragment.newInstance();

        fragments.add(createFragment);
        fragments.add(listFragment);
        fragments.add(contentsFragment);

        ArrayList<String> fragTitles = new ArrayList<>();
        fragTitles.add("Create");
        fragTitles.add("Schedule");
        fragTitles.add("Details");

        myPageAdapter = new CustomPageAdapter(getSupportFragmentManager(), fragments, fragTitles);
        hubViewPager.setAdapter(myPageAdapter);
        hubViewPager.setCurrentItem(1);

        inDetailFrag = false;

        smsReceiver = new SmsReceiver(this);
        smsReceiver.register();
        smsReceiver.setSmsListener(this);
        backpress = false;
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
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hubnew:
                switchAwayFromDetailFrag(0);
                return true;
            case R.id.hubview:
                switchAwayFromDetailFrag(1);
                return true;
            case R.id.hubpush:
                if (user.equals("test")){
                    //TODO prompt for user / pass
                    user = "4828";
                    pass = "RoboEagles4828";
                }
                sendPostRequest(user, pass, ((HubListFragment) myPageAdapter.fragments.get(1)).getDatabase());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(inDetailFrag){
            switchAwayFromDetailFrag(1);
        } else {
            if(backpress) {
                super.onBackPressed();
            } else{
                backpress = true;
                Toast.makeText(this, "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendPostRequest(String user, String pass, String data) {
        String urlstring = "http://pavanec2.us.to/frc/database/update/view/index.php";
        new PostClass(this, urlstring, user, pass, data, true).execute();
    }

    @Override
    public void smsReceived(String number, String message) { //launched from smsReceiver
        String result = listFragment.mySchedule.addSMS(message);
        Toast.makeText(this, number + " updated " + result, Toast.LENGTH_LONG).show();
    }

    @Override
    public void autopush() { //launched from hubListFragment
        Toast.makeText(this, "autopush", Toast.LENGTH_SHORT).show(); //TODO auto push to server
    }

    @Override
    public void switchToDetails(int matchId){ //launched from hubListFragment
        inDetailFrag = true;
        contentsFragment.reset(listFragment.getMatchFromId(matchId));
        hubViewPager.setCurrentItem(2);
    }

    @Override                                //launched from hubCreateFragment
    public void addNewMatch(int[] teams, int matchnum, boolean isQual, String phonenum) {
        listFragment.addNewMatch(teams, matchnum, isQual, phonenum);
    }

    @Override
    public ArrayList<String> getMatchTitles() { //launched from hubCreateFragment
        return listFragment.mySchedule.getMatchTitles();
    }

    @Override
    public void saveContents(int i) { //launched from hubContentsFragment
        switchAwayFromDetailFrag(i);
    }

    public void switchAwayFromDetailFrag(int i){ //called by saveContents(), back button, and viewpager
        inDetailFrag = false;
        contentsFragment.save();
        hubViewPager.setCurrentItem(i);
    }

}