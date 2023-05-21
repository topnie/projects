package webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.expression.Numbers;
import webapp.services.ClientService;

@Controller
@SessionAttributes("id")
public class ClientLoginController {
        @Autowired
        private ClientService userService;

        @GetMapping("/client")
        public String loginForm() {
            return "client";
        }

        @PostMapping("/client")
        public String login(@RequestParam("id") String id,
                            @RequestParam("password") String password,
                            Model model) {
            boolean success;
            try {
                Long.parseLong(id);
                success = userService.login(Long.parseLong(id), password);

            } catch(NumberFormatException e){
                success = false;
            }
            if (success) {
                model.addAttribute("id", Long.parseLong(id));
                return "redirect:/client/home";
            } else {
                model.addAttribute("error", "Invalid email or password");
                return "client";
            }

        }
}
