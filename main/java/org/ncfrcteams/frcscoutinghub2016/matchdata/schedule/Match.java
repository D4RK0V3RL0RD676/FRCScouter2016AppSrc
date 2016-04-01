package org.ncfrcteams.frcscoutinghub2016.matchdata.schedule;

import org.ncfrcteams.frcscoutinghub2016.matchdata.Obstacle;

/**
 * Created by pavan on 3/30/16.
 */
public class Match {

    private int matchNum;
    private int matchStatus;
    private boolean isQual;
    private MatchDescriptor matchDescriptor;
    private String[] data = new String[6];
    private String[] comments = new String[6];

    public Match(int matchNum, int matchStatus, boolean isQual, MatchDescriptor matchDescriptor){
        this.matchNum = matchNum;
        this.matchStatus = matchStatus;
        this.isQual = isQual;
        this.matchDescriptor = matchDescriptor;
    }

    public static Match getBlank(int matchNum, boolean isQual) {
        return new Match(matchNum, 0, isQual, null);
    }

    public static Match getFromDescriptor(MatchDescriptor matchDescriptor) {
        return new Match(matchDescriptor.getMatchNum(), 0, matchDescriptor.isQual(), matchDescriptor);
    }

    public boolean isPlaceHolder() {
        return matchDescriptor == null;
    }

    public String getTitle(){
        return (isQual ? "Qual " : "Elim ") + matchNum;
    }

    public int getColor(){
        switch(matchStatus){
            case 0:
                return 0xffff0000; //red = incomplete
            case 1:
                return 0xff00ff00; //green = ready for scouting
            case 2:
                return 0xffffff00; //yellow = scouting in progress
            default:
                return 0xffffffff; //black = done (all data back)
        }
    }

    public void addComment(int teamNum, String comment) {
        int[] teamNums = matchDescriptor.getTeams();

        for(int i=0; i<teamNums.length; i++) {
            if(teamNums[i] == teamNum) {
                this.comments[i] = comment;
            }
        }

        return;
    }

    public void addData(int teamNum, String data) {
        int[] teamNums = matchDescriptor.getTeams();

        for(int i=0; i<teamNums.length; i++) {
            if(teamNums[i] == teamNum) {
                this.data[i] = data;
            }
        }

        return;
    }
    
    public int[] getTeams() {
        return matchDescriptor.getTeams();
    }

    public Obstacle[] getObstacles() {
        return matchDescriptor.getObstacles();
    }

    public int getMatchNum() {
        return matchNum;
    }

    public boolean isQual() {
        return isQual;
    }
}
