package com.game.guess_game.controller;

import com.game.guess_game.model.GameSession;
import com.game.guess_game.dto.*;
import com.game.guess_game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    private int gameCounter = 0;

    @PostMapping
    public ResponseEntity<GameSession> createGame(@RequestBody CreateGameRequest request) {
        gameCounter++;
        String gameId = String.valueOf(gameCounter);

        GameSession newGame = new GameSession(gameId, request.maxPlayers, request.timeLimit);
        gameRepository.save(newGame);

        return ResponseEntity.ok(newGame);
    }

    @GetMapping
    public ResponseEntity<List<GameSummary>> listGames() {
        List<GameSession> allGames = gameRepository.findAll();

        List<GameSummary> summaries = allGames.stream()
                .map(game -> new GameSummary(game.getGameId(), game.getPlayers().size(), game.getMaxPlayers(), game.getStatus()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(summaries);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<String> joinGame(@PathVariable String id, @RequestParam String playerName) {
        GameSession game = gameRepository.findById(id).orElse(null);
        if (game == null) return ResponseEntity.notFound().build();

        if (game.getPlayers().size() >= game.getMaxPlayers()) {
            return ResponseEntity.badRequest().body("Error: This game room is already full! Maximum capacity is " + game.getMaxPlayers());
        }

        if (game.getPlayers().contains(playerName)) {
            return ResponseEntity.badRequest().body("Error: A player named '" + playerName + "' has already joined this room!");
        }
        game.getPlayers().add(playerName);
        gameRepository.save(game);

        return ResponseEntity.ok(playerName + " successfully joined the game!");
    }

    @PostMapping("/{id}/guess")
    public ResponseEntity<String> submitGuess(@PathVariable String id, @RequestBody GuessRequest request) {
        GameSession game = gameRepository.findById(id).orElse(null);
        if (game == null) return ResponseEntity.notFound().build();

        if ("COMPLETED".equals(game.getStatus())) {
            return ResponseEntity.badRequest().body("Error: This game is already completed!");
        }

        if (!game.getPlayers().contains(request.playerId)) {
            return ResponseEntity.badRequest().body("Error: You cannot guess because you haven't joined this game room!");
        }

        if (request.guess < 0 || request.guess > 100) return ResponseEntity.badRequest().body("Guess must be between 0 and 100");

        game.getGuesses().put(request.playerId, request.guess);

        if (game.getPlayers().size() == game.getMaxPlayers() && game.getGuesses().size() == game.getMaxPlayers()) {
            game.calculateResults();
        }

        gameRepository.save(game);
        return ResponseEntity.ok("Guess submitted successfully!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameSession> getGameDetails(@PathVariable String id) {

        GameSession game = gameRepository.findById(id).orElse(null);
        return game != null ? ResponseEntity.ok(game) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<GameResult> getResult(@PathVariable String id) {

        GameSession game = gameRepository.findById(id).orElse(null);
        if (game == null) return ResponseEntity.notFound().build();

        if (!"COMPLETED".equals(game.getStatus())) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(game.getResult());
    }

    @PutMapping("/{id}/guess")
    public ResponseEntity<String> updateGuess(@PathVariable String id, @RequestBody GuessRequest request) {
        GameSession game = gameRepository.findById(id).orElse(null);
        if (game == null) return ResponseEntity.notFound().build();

        if ("COMPLETED".equals(game.getStatus())) {
            return ResponseEntity.badRequest().body("Cannot update guess. The game is already over!");
        }

        if (!game.getPlayers().contains(request.playerId)) {
            return ResponseEntity.badRequest().body("Error: You cannot update a guess for a room you haven't joined!");
        }

        if (request.guess < 0 || request.guess > 100) {
            return ResponseEntity.badRequest().body("Guess must be between 0 and 100");
        }

        game.getGuesses().put(request.playerId, request.guess);

        if (game.getPlayers().size() == game.getMaxPlayers() && game.getGuesses().size() == game.getMaxPlayers()) {
            game.calculateResults();
        } else {

            game.setResult(null);
        }

        gameRepository.save(game);
        return ResponseEntity.ok(request.playerId + " successfully updated their guess to " + request.guess);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGame(@PathVariable String id) {

        if (!gameRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }


        gameRepository.deleteById(id);

        return ResponseEntity.ok("Game room " + id + " was successfully deleted.");
    }

    @DeleteMapping("/{id}/players/{playerName}")
    public ResponseEntity<String> removePlayer(@PathVariable String id, @PathVariable String playerName) {

        GameSession game = gameRepository.findById(id).orElse(null);
        if (game == null) return ResponseEntity.notFound().build();

        if ("COMPLETED".equals(game.getStatus())) {
            return ResponseEntity.badRequest().body("Cannot remove player. The game is already completed!");
        }

        if (!game.getPlayers().contains(playerName)) {
            return ResponseEntity.badRequest().body("Player '" + playerName + "' is not in game room " + id);
        }

        game.getPlayers().remove(playerName);
        game.getGuesses().remove(playerName);

        gameRepository.save(game);

        return ResponseEntity.ok("Player '" + playerName + "' was successfully removed from room " + id);
    }
}