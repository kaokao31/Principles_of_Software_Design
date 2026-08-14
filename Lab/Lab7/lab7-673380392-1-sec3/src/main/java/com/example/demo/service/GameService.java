package com.example.demo.service;

import com.example.demo.model.Game;
import com.example.demo.repository.GameRepository;
import com.example.demo.strategy.DiscountContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final DiscountContext discountContext;

    public GameService(GameRepository gameRepository, DiscountContext discountContext) {
        this.gameRepository = gameRepository;
        this.discountContext = discountContext;
    }

    public List<Game> getAllGames() {
        List<Game> games = gameRepository.findAll();
        games.forEach(this::applyDiscountInfo);
        return games;
    }

    public Game getGameById(Long id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game != null) {
            applyDiscountInfo(game);
        }
        return game;
    }

    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }

    private void applyDiscountInfo(Game game) {
        double finalPrice = discountContext.calculatePrice(game.getDiscountType(), game.getPrice());
        game.setFinalPrice(finalPrice);
        game.setDiscountName(discountContext.getDiscountName(game.getDiscountType()));
    }
}