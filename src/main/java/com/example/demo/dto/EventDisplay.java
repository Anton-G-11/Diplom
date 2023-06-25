/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;
import com.example.demo.entities.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author anton
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDisplay {
    private Integer id;
    private String name;
    private String description;
    private Integer day;
    private Integer number;
    private String type;
    private User user;
    
    @JsonIgnore
    private List<Group> groups;
    
    public EventDisplay (Event event)
    {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.type = "event";
        this.user = event.getUser();
        this.groups = event.getGroups();
        this.number = event.getNumber();
        this.day = event.getDay().getDay();
    }
    
    public EventDisplay (Lesson lesson)
    {
        this.id = lesson.getId();
        this.name = lesson.getCourse().getName();
        this.description = lesson.getCourse().getDescription();
        this.type = lesson.getType();
        this.user = lesson.getTeacher();
        this.groups = lesson.getGroups();
        this.number = lesson.getNumber();
        this.day = lesson.getDay();
    }
}
