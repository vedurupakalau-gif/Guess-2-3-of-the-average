package com.game.guess_game.repository;

import com.game.guess_game.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<GameSession, String> {

}