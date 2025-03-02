package com.tus.individual.formatter;

import org.apache.poi.ss.usermodel.Row;

import com.tus.individual.exception.CellFormatException;
import com.tus.individual.exception.RowFormatException;
import com.tus.individual.model.Action;
import com.tus.individual.model.Match;
import com.tus.individual.model.Team;
import com.tus.individual.model.Player;

public class RowFormatter {
    private static final String NULL_ROW_ERROR_MESSAGE = "Row is null";
    private static final String COULD_NOT_FORMAT_ROW_ERROR_MESSAGE = "Could not format row: ";
    private static final int ACTION_ROW_EXPECTED_CELLS = 19;
    private static final int MATCH_ROW_EXPECTED_CELLS = 7;
    private static final int TEAM_ROW_EXPECTED_CELLS = 2;
    private static final int PLAYER_ROW_EXPECTED_CELLS = 2;
    private CellFormatter cellFormatter;
    
    public RowFormatter() {
        cellFormatter = new CellFormatter();
    }

    public Action formatToAction(Row row) throws RowFormatException {
        validateRow(row, ACTION_ROW_EXPECTED_CELLS);
        
        try {
            Action action = new Action();
            action.setPlayerId(cellFormatter.formatToInteger(row.getCell(0)));
            action.setMatchId(cellFormatter.formatToInteger(row.getCell(1)));
            action.setTeamId(cellFormatter.formatToInteger(row.getCell(2)));
            action.setFirstGoal(cellFormatter.formatToInteger(row.getCell(3)));
            action.setWinningGoal(cellFormatter.formatToInteger(row.getCell(4)));
            action.setShotsOnTarget(cellFormatter.formatToInteger(row.getCell(5)));
            action.setSavesMade(cellFormatter.formatToInteger(row.getCell(6)));
            action.setTimePlayed(cellFormatter.formatToInteger(row.getCell(7)));
            action.setPositionId(cellFormatter.formatToInteger(row.getCell(8)));
            action.setStarts(cellFormatter.formatToInteger(row.getCell(9)));
            action.setSubstituteOn(cellFormatter.formatToInteger(row.getCell(10)));
            action.setSubstituteOff(cellFormatter.formatToInteger(row.getCell(11)));
            action.setGoals(cellFormatter.formatToInteger(row.getCell(12)));
            action.setTeam1(cellFormatter.formatToString(row.getCell(13)));
            action.setTeam2(cellFormatter.formatToString(row.getCell(14)));
            action.setShotEfficiency(cellFormatter.formatToLong(row.getCell(15)));
            action.setPassesEfficiency(cellFormatter.formatToLong(row.getCell(16)));
            action.setTackleEfficiency(cellFormatter.formatToLong(row.getCell(17)));
            action.setDribbleEfficiency(cellFormatter.formatToLong(row.getCell(18)));
            
            return action;
        } catch (CellFormatException e) {
            throw new RowFormatException(COULD_NOT_FORMAT_ROW_ERROR_MESSAGE + e.getMessage());
        }
    }

    public Match formatToMatch(Row row) throws RowFormatException {
        validateRow(row, MATCH_ROW_EXPECTED_CELLS);
        
        try {
            Match match = new Match();
            match.setMatchId(cellFormatter.formatToInteger(row.getCell(0)));
            match.setTeamHomeID(cellFormatter.formatToInteger(row.getCell(1)));
            match.setTeamAwayID(cellFormatter.formatToInteger(row.getCell(2)));
            match.setTeamHomeFormation(cellFormatter.formatToInteger(row.getCell(3)));
            match.setTeamAwayFormation(cellFormatter.formatToInteger(row.getCell(4)));
            match.setResultOfTeamHome(cellFormatter.formatToInteger(row.getCell(5)));
            match.setDate(cellFormatter.formatToDate(row.getCell(6)));
            
            return match;
        } catch (CellFormatException e) {
            throw new RowFormatException(COULD_NOT_FORMAT_ROW_ERROR_MESSAGE + e.getMessage());
        }
    }

    public Team formatToTeam(Row row) throws RowFormatException {
        validateRow(row, TEAM_ROW_EXPECTED_CELLS);
        
        try {
            Team team = new Team();
            team.setTeamId(cellFormatter.formatToInteger(row.getCell(0)));
            team.setTeamName(cellFormatter.formatToString(row.getCell(1)));
            
            return team;
        } catch (CellFormatException e) {
            throw new RowFormatException(COULD_NOT_FORMAT_ROW_ERROR_MESSAGE + e.getMessage());
        }
    }

    public Player formatToPlayer(Row row) throws RowFormatException {
        validateRow(row, PLAYER_ROW_EXPECTED_CELLS);
        
        try {
            Player player = new Player();
            player.setPlayerId(cellFormatter.formatToInteger(row.getCell(0)));
            player.setPlayerName(cellFormatter.formatToString(row.getCell(1)));
            
            return player;
        } catch (CellFormatException e) {
            throw new RowFormatException(COULD_NOT_FORMAT_ROW_ERROR_MESSAGE + e.getMessage());
        }
    }

    private void validateRow(Row row, int expectedCells) throws RowFormatException {
        if (row == null) {
            throw new RowFormatException(NULL_ROW_ERROR_MESSAGE);
        }
        if (row.getLastCellNum() != expectedCells) {
            throw new RowFormatException("Could not format row: Expected " + expectedCells + " cells, but found " + row.getLastCellNum());
        }
    }
    
}
