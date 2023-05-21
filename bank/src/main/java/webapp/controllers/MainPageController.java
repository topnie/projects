package webapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller

public class MainPageController {

    @RequestMapping("/")
    public String mainPage() {
        return "main";
    }

    @RequestMapping("/client")
    public String clientHome() {
        return "client";
    }
    @RequestMapping("/employee")
    public String employeeHome() {
        return "employee";
    }
}
