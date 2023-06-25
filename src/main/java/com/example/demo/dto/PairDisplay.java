/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;
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
public class PairDisplay {
    private Integer number;
    private String comment;
    private List<EventDisplay> lessons = new ArrayList<>();
}
