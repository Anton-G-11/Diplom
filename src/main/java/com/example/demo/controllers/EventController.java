/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.services.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author sgrig
 */
@Controller
@RequestMapping("/event")
public class EventController {

    @Autowired
    UserService userService;

    @Autowired
    EventService eventService;

    @Autowired
    private HttpSession httpSession;

    
    @GetMapping(value = "/edit/{id:\\d+}")
    public String edit(Model model, @PathVariable(name = "id") Integer id) {
        Event event;
        if (id != 0) {
            event = eventService.GetEvent(id);
        } else {
            event = new Event();
            User u;
            Integer loggedIn = (Integer) httpSession.getAttribute("loggedIn");
            System.out.println (loggedIn);
            if (loggedIn != null) {
                u = userService.GetUser(loggedIn);
                System.out.println(u);
            } else {
                u = userService.GetAllStudents().get(0);
            }
            event.setUser(u);
        }
        model.addAttribute("event", event);

        if (event.getUser().getId() == httpSession.getAttribute("loggedIn")) {
            return "editevents";
        } else {
            return "viewevent";
        }
    }

    @PostMapping(value = "/update")
    public String edit(Model model, @ModelAttribute("EventForm") EventForm eventForm) {
        //System.out.println(eventForm);
        Event event = eventService.UpdateEvent(eventForm, (Integer)httpSession.getAttribute("loggedIn"));
        model.addAttribute("event", event);

        return "redirect:/event/edit/" + event.getId();
    }

}
