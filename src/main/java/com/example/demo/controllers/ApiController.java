/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controllers;

import com.example.demo.dto.DayDisplay;
import com.example.demo.dto.EventDisplay;
import com.example.demo.dto.SlotData;
import com.example.demo.dto.TeacherForm;
import com.example.demo.entities.Group;
import com.example.demo.entities.Lesson;
import com.example.demo.entities.User;
import com.example.demo.services.GroupService;
import com.example.demo.services.ScheduleService;
import com.example.demo.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sergiy
 */
@CrossOrigin
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    UserService teacherService;

    @Autowired
    ScheduleService scheduleService; 
    
    @Autowired
    GroupService groupService;
    
    @GetMapping(value = "/teachers")
    public ResponseEntity<List<User>> teachers() {

        List<User> list = teacherService.GetAllTeachers();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/teacher/{id:\\d+}")
    public ResponseEntity<User> teacher(@PathVariable(name = "id") Integer id) {

        User teacher = teacherService.GetUser(id);
        return new ResponseEntity<>(teacher, HttpStatus.OK);
    }

    @GetMapping(value = "/delteacher/{id:\\d+}")
    public ResponseEntity<String> delteacher(@PathVariable(name = "id") Integer id) {

        teacherService.DeleteUser(id);
        return new ResponseEntity<>("Ok", HttpStatus.OK);
    }

    @GetMapping(value = "/groups")
    public ResponseEntity<List<Group>> groups() {

        List<Group> list = groupService.GetGroups();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    @PostMapping(value = "updateteacher")
    public ResponseEntity<User> updateteacher(@ModelAttribute("TeacherForm")TeacherForm teacherForm) {
        User teacher = teacherService.UpdateUser(teacherForm);
        return new ResponseEntity<>(teacher, HttpStatus.OK);
    }
    
    @GetMapping(value = "/lessons", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> lessons(@RequestParam(name = "teacher_id", required = false) Optional<Integer> var_teacher_id, 
            @RequestParam(name = "group_id", required = false) Optional<Integer> var_group_id) {

        Integer teacher_id = var_teacher_id.orElseGet(() -> 0);
        Integer group_id = var_group_id.orElseGet(() -> 0);
        
        List<DayDisplay> lessons = scheduleService.GetWeekLessons(teacher_id, group_id);
        
        return new ResponseEntity<>(lessons, HttpStatus.OK);
    }
    
}
