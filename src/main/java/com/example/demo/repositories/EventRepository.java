/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.repositories;

import com.example.demo.entities.*;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/**
 *
 * @author anton
 */
public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event>{
    
    @Query("FROM Event v WHERE v.day >= :monday AND v.day <= :saturday")
    List<Event> findEventsByWeek(@Param("monday") Date monday, @Param("saturday") Date saturday);
}
