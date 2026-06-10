package com.game.guess_game.dto;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class GameResult {

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> winners = new ArrayList<>();

    private Double average;
    private Double target;

    public GameResult() {
    }

    public GameResult(List<String> winners, double average, double target) {
        this.winners = winners;
        this.average = average;
        this.target = target;
    }

    // --- Getters and Setters ---
    public List<String> getWinners() { return winners; }
    public void setWinners(List<String> winners) { this.winners = winners; }

    public Double getAverage() { return average; }
    public void setAverage(double average) { this.average = average; }

    public Double getTarget() { return target; }
    public void setTarget(double target) { this.target = target; }
}