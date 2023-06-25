/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.services;


import com.example.demo.dto.TeacherForm;
import com.example.demo.entities.User;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.example.demo.repositories.*;
/**
 *
 * @author anton
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public Integer CtreateTeacher (String firstName, String lastName, String email)
    {
        User teach = new User ();
        teach.setFirstName(firstName);
        teach.setLastName(lastName);
        teach.setEmail(email);
        teach.setType(1);
        return userRepository.saveAndFlush(teach).getId();
    }
    
    public Integer CtreateStudent (String firstName, String lastName, String email)
    {
        User teach = new User ();
        teach.setFirstName(firstName);
        teach.setLastName(lastName);
        teach.setEmail(email);
        teach.setType(2);
        return userRepository.saveAndFlush(teach).getId();
    }
    
    public List<User> GetAllTeachers ()
    {
        List <User> teachers = new ArrayList<>();
        for (User user:userRepository.findAll())
        {
            if (user.getType()==1)
                teachers.add(user);
        }
        return teachers;
    }
    
    public List<User> GetAllStudents ()
    {
        List <User> students = new ArrayList<>();
        for (User user:userRepository.findAll())
        {
            if (user.getType()==2)
                students.add(user);
        }
        return students;
    }
    
    public User GetUser (Integer id)
    {
        //return userRepository.findById(id).orElse(null);
        return userRepository.getReferenceById(id);
    }

    public User GetUserByEmail (String email)
    {
        List<User> users = userRepository.findAll();
        for (User u:users)
        {
            System.out.println(u);
            if (u.getEmail().equals(email)) return u;
        }
        return null;
    }
    
    public void DeleteUser (Integer id)
    {
        userRepository.deleteById(id);
    }

    public User UpdateUser (TeacherForm form)
    {
        User user;
        if(form.getId() == 0){
            user = new User();
        }else{
            user = userRepository.findById(form.getId()).orElse(new User());
        }
        
        user.setEmail(form.getEmail());
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        
        return userRepository.saveAndFlush(user);
    }
    
    public void SaveUser(User user)
    {
        userRepository.saveAndFlush(user);
    }
}
