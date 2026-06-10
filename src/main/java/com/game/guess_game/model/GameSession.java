package com.game.guess_game.model;

import com.game.guess_game.dto.GameResult;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class GameSession {

    @Id
    private String gameId;

    private int maxPlayers;
    private int timeLimit;
    private String status = "WAITING";

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> players = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Double> guesses = new HashMap<>();

    @Embedded
    private GameResult result;

    public GameSession() {
    }

    public GameSession(String gameId, int maxPlayers, int timeLimit) {
        this.gameId = gameId;
        this.maxPlayers = maxPlayers;
        this.timeLimit = timeLimit;
        this.status = "WAITING";
    }

    public void calculateResults() {
        if (guesses.isEmpty()) return;

        double sum = guesses.values().stream().mapToDouble(Double::doubleValue).sum();
        double average = sum / guesses.size();
        double target = average * (2.0 / 3.0);

        double minDifference = Double.MAX_VALUE;
        List<String> winners = new ArrayList<>();

        for (Map.Entry<String, Double> entry : guesses.entrySet()) {
            double diff = Math.abs(entry.getValue() - target);
            if (diff < minDifference) {
                minDifference = diff;
                winners.clear();
                winners.add(entry.getKey());
            } else if (Math.abs(diff - minDifference) < 0.0001) {
                winners.add(entry.getKey());
            }
        }

        this.result = new GameResult(winners, average, target);
        this.status = "COMPLETED";
    }

    // --- Getters & Setters ---
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getPlayers() { return players; }
    public void setPlayers(List<String> players) { this.players = players; }

    public Map<String, Double> getGuesses() { return guesses; }
    public void setGuesses(Map<String, Double> guesses) { this.guesses = guesses; }

    public GameResult getResult() {
        if (!"COMPLETED".equals(this.status)) {
            return null;
        }
        return result;
    }
    public String getGameProgress() {
        if ("COMPLETED".equals(this.status)) {
            return "Game Finished! Results are calculated.";
        }
        return "Game is in progress. Waiting for all players to submit guesses.";
    }
    public void setResult(GameResult result) { this.result = result; }
}