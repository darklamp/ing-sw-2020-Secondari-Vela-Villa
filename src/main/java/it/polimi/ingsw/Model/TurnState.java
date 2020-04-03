package it.polimi.ingsw.Model;

/**
 * Holds the state of a player's turn
 * MOVE --> the player is allowed to move
 * BUILD -->                         build
 * BOTH -->                          move/build
 * PASS --> the player has to pass
 */
public enum TurnState {
    MOVE,BUILD,MOVEORBUILD,BUILDORPASS,PASS
}
