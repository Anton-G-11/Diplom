/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author anton
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "slots", schema = "test")
public class Slot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "day")
    private Integer day;

    @Column(name = "number")
    private Integer number;

    @Column(name = "priority")
    private Integer priority;

    @JsonIgnore
    @OneToMany(mappedBy = "slot")
    private List<Lesson> lessons;

    @ManyToMany(mappedBy = "slots")
    private List<User> users;

    @Override
    public String toString() {
        return day + " " + number;
    }

}
