package it.polimi.ingsw.Model;

/**
 * Holds the state of a player's turn
 * MOVE --> the player is allowed to move
 * BUILD -->                         build
 * BOTH -->                          move/build
 * NOOP --> the player cannot do anything
 */
public enum TurnState {
    MOVE,BUILD,MOVEORBUILD,BUILDORPASS, NOOP
}
