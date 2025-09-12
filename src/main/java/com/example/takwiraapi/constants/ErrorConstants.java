package com.example.takwiraapi.constants;

public class ErrorConstants {
    //Functional Errors
    public static final String PLAYER_NOT_FOUND = "Player.not.found";
    public static final String PLAYER_NAME_ALREADY_EXISTS = "Player.name.already.exists";
    public static final String MATCH_NOT_FOUND = "Match.not.found";
    public static final String SCORER_NOT_FOUND_IN_MATCH = "Scorer.is.not.part.of.the.match.in.the.specified.team";
    public static final String ASSIST_PLAYER_NOT_FOUND_IN_MATCH = "Assist.player.is.not.part.of.the.match.in.the.specified.team";
    public static final String PLAYER_IN_BOTH_TEAMS = "Player.in.both.teams";
    public static final String SCORER_AND_ASSIST_PLAYER_ARE_THE_SAME = "Scorer.and.assist.player.are.the.same";

    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred";
}
