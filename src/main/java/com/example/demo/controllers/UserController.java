/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controllers;

import com.example.demo.dto.TeacherForm;
import com.example.demo.entities.User;
import com.example.demo.services.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Sergiy
 */
@Controller
@RequestMapping("/teacher")
public class UserController {
    
    @Autowired
    UserService teacherService;
    
    @GetMapping(value = "/list")
    public String list(Model model) {

        List<User> list = teacherService.GetAllTeachers();
        model.addAttribute("list", list);
        return "teachers";
    }    
    
    @GetMapping(value = "/edit/{id:\\d+}")
    public String edit(Model model, @PathVariable(name = "id") Integer id) {

        User teacher = teacherService.GetUser(id);
        model.addAttribute("teacher", teacher);
        
        return "editteachers";
    }    

    @PostMapping(value = "/update")
    public String edit(Model model, @ModelAttribute("TeacherForm")TeacherForm teacherForm) {

        User teacher = teacherService.UpdateUser(teacherForm);
        model.addAttribute("teacher", teacher);
        
        return "redirect:/teacher/edit/" + teacher.getId();
    }    

}
