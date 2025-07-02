package com.college.PlacementApl.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {


    @RequestMapping("/home")
    public String home(){
        return "This is User Page";
    }



}
