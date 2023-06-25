/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.entities.*;
import com.example.demo.services.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author sgrig
 */
@Controller
@RequestMapping("/createschedule")
public class CreatorController {
    
    @Autowired
    UserService userService;
    
    @Autowired
    GroupService groupService;
    
    @Autowired 
    CreatorService creatorService;
    
    @Autowired 
    private HttpSession httpSession;
    
    
    @GetMapping(value = "")
    public String schedule(Model model,
            //@RequestParam(name = "monday", required = false) Optional<String> var_monday,
            @RequestParam(name = "teacher_id", required = false) Optional<Integer> var_teacher_id,
            @RequestParam(name = "group_id", required = false) Optional<Integer> var_group_id) {
        Integer teacher_id = var_teacher_id.orElseGet(() -> 0);
        Integer group_id = var_group_id.orElseGet(() -> 0);
        if (httpSession.getAttribute("lessons") == null) {
            httpSession.setAttribute("lessons", creatorService.GetAllLessons());
        }

        List<DayDisplay> lessons = creatorService.GetWeekLessons(teacher_id, group_id, (List<Lesson>) httpSession.getAttribute("lessons"));

        HashMap<String, List<EventDisplay>> displaylist = creatorService.PrepareLessonsToHash(lessons);
        model.addAttribute("displaylist", displaylist);

        model.addAttribute("weekdays", ScheduleService.weekdays);
        model.addAttribute("paircomments", ScheduleService.paircomments);

        List<User> teachers = userService.GetAllTeachers();
        model.addAttribute("teachers", teachers);

        if (teacher_id > 0) {
            List<Slot> slotslist = userService.GetUser(teacher_id).getSlots();
            List<String> slots = new ArrayList<>();
            for (Slot slot : slotslist) {
                String key = slot.getDay() + "_" + slot.getNumber();
                slots.add(key);
            }
            model.addAttribute("slots", slots);
        } else {
            model.addAttribute("slots", new ArrayList<String>());
        }

        List<Group> groups = groupService.GetGroups();
        model.addAttribute("groups", groups);

        model.addAttribute("teacher_id", teacher_id);
        model.addAttribute("group_id", group_id);

        return "schedule";
    }

    @GetMapping(value = "/saveall")
    public ResponseEntity<Object> saveslots() {
        creatorService.SaveAll((List<Lesson>)httpSession.getAttribute("lessons"));
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
    
    @PostMapping(value = "/savesingle")
    public ResponseEntity<Object> savesingle(@RequestBody List<SlotData> slots) {
        httpSession.setAttribute("lessons",creatorService.ProcessSingle(slots, (List<Lesson>)httpSession.getAttribute("lessons")));
        return new ResponseEntity<>(slots, HttpStatus.OK);
    }
    
    @GetMapping(value = "/reset")
    public String resetSlots() {
        httpSession.setAttribute("lessons", creatorService.FullReset((List<Lesson>)httpSession.getAttribute("lessons")));
        return "redirect:/createschedule/";
    }
    
    @GetMapping(value = "/current")
    public String GetCurrentSlots() {
        httpSession.setAttribute("lessons",creatorService.GetAllLessons());
        return "redirect:/createschedule/";
    }
    
    @GetMapping(value = "/assign")
    public String AssignLessons() {
        httpSession.setAttribute("lessons", creatorService.AssignLessons((List<Lesson>)httpSession.getAttribute("lessons")));
        return "redirect:/createschedule/";
    }
    
}