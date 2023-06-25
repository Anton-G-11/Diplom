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
@RequestMapping("/course")
public class CourseController {

    @Autowired
    EventService eventService;

    @Autowired
    private HttpSession httpSession;

    
    @GetMapping(value = "/edit/{id:\\d+}")
    public String edit(Model model, @PathVariable(name = "id") Integer id) {
        Course course=null;
        if (id != 0) {
            course = eventService.GetCourse(id);
        } 
        model.addAttribute("course", course);

        if (CheckCourses((Integer)httpSession.getAttribute("loggedIn"), course)) {
            return "editcourse";
        } else {
            return "viewcourse";
        }
    }
    
    private boolean CheckCourses(Integer t, Course c)
    {
        for (Lesson l: c.getLessons())
        {
            if (l.getTeacher().getId().equals(t)) return true;
        } 
        return false;
    }

    @PostMapping(value = "/update")
    public String edit(Model model, @ModelAttribute("CourseForm") CourseForm courseForm) {
        Course course = eventService.UpdateCourse(courseForm);
        model.addAttribute("course", course);

        return "redirect:/course/edit/" + course.getId();
    }

}
