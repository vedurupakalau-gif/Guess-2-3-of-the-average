package com.game.guess_game.dto;

public class GameSummary {
    public String gameId;
    public int playersJoined;
    public int maxPlayers;
    public String status;

    public GameSummary(String gameId, int playersJoined, int maxPlayers, String status) {
        this.gameId = gameId;
        this.playersJoined = playersJoined;
        this.maxPlayers = maxPlayers;
        this.status = status;
    }
}