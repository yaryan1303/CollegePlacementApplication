package com.college.PlacementApl.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @RequestMapping("/home")
    public String home(){
        return "This is Admin Page";
    }

}
