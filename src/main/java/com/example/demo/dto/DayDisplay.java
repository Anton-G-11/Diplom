/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import java.util.HashMap;
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
public class DayDisplay {
    private String weekday;
    private Integer day;
    private List<PairDisplay> daylessons;
}
