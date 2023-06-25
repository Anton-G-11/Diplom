/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.services;

import com.example.demo.entities.Group;
import com.example.demo.repositories.GroupRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Sergiy
 */
@Service
public class GroupService {
 
    
    @Autowired
    GroupRepository groupRepository;
    
    public List<Group> GetGroups()
    {
        return groupRepository.findAll();
    }
    
    public Group GetGroupById (Integer id)
    {
        return groupRepository.getReferenceById(id);
    }
}
