package it.polimi.ingsw.Network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Server configuration helper class.
 * NB: the values here are the defaults. They can get overridden by the yaml conf,
 * and the yaml conf can in turn be overridden by command line arguments.
 */
public class ServerConf {
    public String ip = "0.0.0.0";
    public int port = 1337;
    public boolean disk = false;
    public String verb = "info";
    public short moveTimer = 2;
    public boolean console = false;
    public String MOTD = "Have fun!";
    public String timeunit = "m";
    public int maxPlayers = 3;
    public List<String> gods = new ArrayList<>(Arrays.asList("APOLLO", "ARTEMIS", "ATHENA", "ATLAS", "DEMETER", "HEPHAESTUS", "MINOTAUR", "PAN", "PROMETHEUS"));
}
