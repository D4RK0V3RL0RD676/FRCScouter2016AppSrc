package org.ncfrcteams.frcscoutinghub2016.ui.hub;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.ncfrcteams.frcscoutinghub2016.R;
import org.ncfrcteams.frcscoutinghub2016.matchdata.Obstacle;
import org.ncfrcteams.frcscoutinghub2016.matchdata.schedule.Match;

public class HubContentsFragment extends Fragment implements View.OnClickListener {
    private int matchId = 0;
    private Match match;
    private HubContentsFragListener mListener;

    private TextView title;
    private EditText[] teamNumbers;
    private ImageView[] selectors;
    private ImageView[] positions;

    private Obstacle[] selection;
    private Obstacle selected = null;

    public static HubContentsFragment newInstance() {
        return new HubContentsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HubContentsFragListener) {
            mListener = (HubContentsFragListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HubContentsFragListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.h_frag_contents, container, false);

        title = (TextView) view.findViewById(R.id.contentsTitle);

        int id;
        String name;

        teamNumbers = new EditText[6];

        for(int i=0; i<teamNumbers.length; i++) {
            name = "edit" + ((i<3)?"Red":"Blue") + (i+1);
            id = getResources().getIdentifier(name, "id", getActivity().getPackageName());

            teamNumbers[i] = (EditText) view.findViewById(id);
        }

        selectors = new ImageView[8];

        for(int i=0; i<selectors.length; i++) {
            name = "barrierView" + (i+1);
            id = getResources().getIdentifier(name, "id", getActivity().getPackageName());

            selectors[i] = (ImageView) view.findViewById(id);
            selectors[i].setOnClickListener(this);
        }

        positions = new ImageView[8];

        for(int i=0; i<positions.length; i++) {
            name = "barrier" + (i+1);
            id = getResources().getIdentifier(name, "id", getActivity().getPackageName());

            positions[i] = (ImageView) view.findViewById(id);
            positions[i].setOnClickListener(this);
        }

        if (matchId != 0) {
            reset(match);
        }

        return view;
    }

    public void setMatchId(int newMatchId) {
        matchId = newMatchId;
        match = mListener.getMatchFromId(matchId);
        reset(match);
    }

    public void reset(Match match) {
        title.setText(match.getTitle());

        int[] nums = match.getTeams();

        for(int i=0;i<teamNumbers.length;i++) {
            teamNumbers[i].setText(nums[i]);
        }


        Obstacle[] obstacles = match.getObstacles();

        String name;
        int id;
        for(int i=0; i<positions.length; i++) {
            name = "barrier" + obstacles[i].getValue();
            id = getResources().getIdentifier(name, "drawable", getActivity().getPackageName());

            positions[i].setImageResource(id);
        }

        selected = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        selection = match.getObstacles();

        for(int i=0; i<selectors.length; i++) {
            if(id == selectors[i].getId()) {
                selected = Obstacle.getObstacle(i+1);
                return;
            }
        }

        if(selected == null)
            return;

        for(int i=0; i<positions.length; i++) {
            if(positions[i].getId() == id) {
                selection[i] = selected;
                positions[i].setImageResource(getResources().getIdentifier(
                        "barrier" + selected.getValue(), "drawable", getActivity().getPackageName()));
            }
        }
    }

    public void save() {
        int[] teams = match.getTeams();

        for(int i=0; i<teamNumbers.length; i++) {
            teams[i] = Integer.parseInt(teamNumbers.toString());
        }
    }

    public void submit(View view) {
        save();
        HubActivity activity = (HubActivity) getActivity();
        activity.switchAwayFromDetailFrag(1);
    }

    public interface HubContentsFragListener {
        Match getMatchFromId(int matchId);
    }

}