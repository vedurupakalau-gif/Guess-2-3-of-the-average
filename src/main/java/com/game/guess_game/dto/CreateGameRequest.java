package com.game.guess_game.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class CreateGameRequest {
    @Min(value = 2, message = "A game must have at least 2 players")
    @Max(value = 20, message = "A game cannot exceed 20 players")
    public int maxPlayers;

    @Min(value = 10, message = "Time limit must be at least 10 seconds")
    public int timeLimit;
}