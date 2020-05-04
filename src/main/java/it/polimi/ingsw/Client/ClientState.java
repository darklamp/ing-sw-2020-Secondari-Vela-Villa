package it.polimi.ingsw.Client;

/**
 * Keeps track of a Player's state in the game,
 * aka what he can do in a certain moment of the game
 */
public enum ClientState {
    MOVE,BUILD,MOVEORBUILD,BUILDORPASS,WAIT,WIN,LOSE,INIT
}
