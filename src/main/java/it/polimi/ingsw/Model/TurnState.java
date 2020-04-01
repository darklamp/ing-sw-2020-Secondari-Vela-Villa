package it.polimi.ingsw.Model;

/**
 * Holds the state of a player's turn
 * MOVE --> the player is in the act of moving
 * BUILD -->                            building
 * PASS --> the player has passed his turn
 */
public enum TurnState {
    MOVE,BUILD,PASS
}
