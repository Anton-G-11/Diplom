/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.services;

import com.example.demo.dto.*;
import com.example.demo.entities.*;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author anton
 */
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private LessonRepository lessonRepository;

    public Integer CtreateEvent(String name, String description, Date date, List<Group> groups) {
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setDay(date);
        event.setGroups(groups);
        return eventRepository.saveAndFlush(event).getId();
    }

    public List<Event> GetAllEvents() {
        return eventRepository.findAll();
    }

    public Event GetEvent(Integer id) {
        return eventRepository.findById(id).orElse(null);
    }

    public void DeleteEvent(Integer id) {
        eventRepository.deleteById(id);
    }

    public Event UpdateEvent(EventForm form, Integer userId) {
        Event event;
        if (form.getId() == null || form.getId()==0) {
            event = new Event();
            event.setUser(userRepository.getReferenceById(userId));
        } else {
            event = eventRepository.findById(form.getId()).orElse(new Event());
        }

        event.setName(form.getName());
        event.setDescription(form.getDescription());
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(form.getDate());
            event.setDay(date1);
        } catch (ParseException ignore) {
        }
        event.setNumber(form.getNumber());
        event.setGroups(null);

        return eventRepository.saveAndFlush(event);
    }
    
    public Course GetCourseByLesson(Integer id)
    {
        return lessonRepository.getById(id).getCourse();
    }
    
    public Course GetCourse(Integer id)
    {
        return courseRepository.getById(id);
    }
    
    public Course UpdateCourse(CourseForm form) {
        Course course;
        course = courseRepository.findById(form.getId()).orElse(new Course());

        course.setName(form.getName());
        course.setDescription(form.getDescription());

        return courseRepository.saveAndFlush(course);
    }
}
