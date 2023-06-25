/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Sergiy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonTmpl {

    private Integer id;
    private String type;
    private Integer day;
    private String daytitle;
    private Integer number;
    private String pair;
    private String lesson;
    private String teacher;
    private String groups;
}
