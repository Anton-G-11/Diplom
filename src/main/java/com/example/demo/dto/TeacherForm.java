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
@NoArgsConstructor
@AllArgsConstructor
public class TeacherForm {
    
    private Integer id = 0;
    private String firstName;
    private String lastName;
    private String email;
}
