package com.example.demo.controller;

import com.example.demo.model.Game;
import com.example.demo.service.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public String listGames(Model model) {
        model.addAttribute("games", gameService.getAllGames());
        return "games/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("game", new Game());
        return "games/add";
    }

    @PostMapping("/save")
    public String saveGame(@ModelAttribute Game game, RedirectAttributes redirectAttributes) {
        gameService.saveGame(game);
        redirectAttributes.addFlashAttribute("message", "เพิ่มเกมสำเร็จ!");
        return "redirect:/games";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("game", gameService.getGameById(id));
        return "games/edit";
    }

    @PostMapping("/update/{id}")
    public String updateGame(@PathVariable Long id, @ModelAttribute Game game, RedirectAttributes redirectAttributes) {
        game.setId(id);
        gameService.saveGame(game);
        redirectAttributes.addFlashAttribute("message", "อัปเดตข้อมูลเกมสำเร็จ!");
        return "redirect:/games";
    }

    @GetMapping("/delete/{id}")
    public String showDeleteConfirm(@PathVariable Long id, Model model) {
        model.addAttribute("game", gameService.getGameById(id));
        return "games/delete";
    }

    @PostMapping("/delete/{id}")
    public String deleteGame(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        gameService.deleteGame(id);
        redirectAttributes.addFlashAttribute("message", "ลบเกมสำเร็จ!");
        return "redirect:/games";
    }
}