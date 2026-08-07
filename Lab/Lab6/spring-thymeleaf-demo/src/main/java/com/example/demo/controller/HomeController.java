package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "นายกานดิทัต นามสุดตา");
        model.addAttribute("studentId", "673380392-1");
        return "home"; // ไม่ใช่ path ไฟล์ แค่ "ชื่อ view" เชิงตรรกะเท่านั้น
    }
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("aboutme", "สวัสดีครับ ผมชื่อ นายกานดิทัต นามสุดตา");
        return "about";
}
}
