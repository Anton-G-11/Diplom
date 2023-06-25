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
import static com.example.demo.services.ScheduleService.paircomments;
import static com.example.demo.services.ScheduleService.weekdays;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.persistence.criteria.JoinType;
import lombok.Data;
/**
 *
 * @author anton
 */
@Service
public class CreatorService {
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private LessonRepository lessonRepository;
    
    @Autowired
    private SlotRepository slotRepository;
    
    
    
    public List<Lesson> FullReset(List<Lesson> lessons)
    {
        for (Lesson l:lessons)
        {
            l.setSlot(null);
        }
        return lessons;
    }
    
    public List <Lesson> AssignLessons(List<Lesson>lessons)
    {
        List <Lesson> unassigned = lessons.stream().filter(l -> l.getDay()==0||l.getNumber()==0).collect(Collectors.toList());
        Collections.shuffle(unassigned);
        ListIterator <Lesson> i = unassigned.listIterator();
        while (i.hasNext())
        {
            Lesson l = i.next();
            Slot f = null;
            Integer a=0;
            List <Slot> slots = slotRepository.findAll();
            Collections.shuffle(slots);
            for (Slot s:slots)
            {
                if (CheckLesson(l, s.getDay(), s.getNumber(), lessons) == 0 && CheckTeacher(s, l))
                {
                Integer a1 = s.getPriority();
                a1+=CheckLesson(l, l.getDay(), l.getNumber()+1, lessons);
                a1+=CheckLesson(l, l.getDay(), l.getNumber()-1, lessons);
                if (a1>a)
                {
                    f=s;
                    a=a1;
                }
                }
            }
            l.setSlot(f);
            
        }
        return lessons;
    }
    
    private boolean CheckTeacher(Slot s, Lesson l)
    {
        for (User t: s.getUsers())
        {
            if (l.getTeacher().getId().equals(t.getId())) return false;
        }
        return true;
    }
    
    private Integer CheckLesson(Lesson ls, Integer d, Integer n, List<Lesson> lessons)
    {
        List <Lesson> test = lessons.stream().filter((l) -> l.getDay().equals(d) && l.getNumber().equals(n)).collect(Collectors.toList());
        Integer res = 0;
        for (Lesson l:test)
        {
            if (l.getTeacher()==ls.getTeacher())
            {
                
                res++;
            }
            for (Group g1: l.getGroups())
            {
                for (Group g2: ls.getGroups())
                {
                    
                    if (g1.getName().equals(g2.getName()))
                    {
                        res++;
                        break;
                    }
                }
            }
        }
        return res;
    }
    
    public void SaveAll(List<Lesson> lessons) {
        lessonRepository.saveAll(lessons);
    }
    
    public List<Lesson> ProcessSingle(List<SlotData> slotslist, List<Lesson> lessons) {
        for (SlotData item : slotslist) {
            Optional<Slot> slotOpt = slotRepository.findSlotInfo(item.getDay(), item.getNumber());
            
            Slot slot = null;
            if (slotOpt.isPresent()) {
                slot = slotOpt.get();
            }
            for (Lesson l:lessons)
            {
                if (item.getId().equals(l.getId()))
                {
                    l.setSlot(slot);
                }
            }
        }
        return lessons;
    }
    
    public List<DayDisplay> GetWeekLessons(Integer teacher_id, Integer group_id, List<Lesson>lessons) {
        //List<Lesson> lessons = GetAllLessons();
        if (teacher_id > 0) {
            lessons = lessons.stream().filter((l) -> l.getTeacher().getId().equals(teacher_id)).collect(Collectors.toList());
        }
        if (group_id > 0) {
            Group group = groupRepository.getReferenceById(group_id);
            lessons = lessons.stream().filter((l) -> ContainsGroup(l.getGroups(), group)).collect(Collectors.toList());
            
        }
        List<DayDisplay> week = new LinkedList<>();

        for (int i = 0; i < 7; i++) {
            int day = i;
            List<PairDisplay> daypairs = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                int number = j;
                List<Lesson> pairlessons = lessons.stream().filter((p) -> p.getDay() == day && p.getNumber() == number).collect(Collectors.toList());
                if (!pairlessons.isEmpty()) {
                    PairDisplay pair = new PairDisplay(number, paircomments.get(number), prepareLessinsDisplay(pairlessons));
                    daypairs.add(pair);
                }
            }
            DayDisplay dayDisplay = new DayDisplay(weekdays.get(day), day, daypairs);
            week.add(dayDisplay);
        }

        return week;
    }
    
    private boolean ContainsGroup(List<Group> groups, Group group)
    {
        
        for (Group g:groups)
        {
            if (g.getName().equals(group.getName())) return true;
            
        }
        return false;
    }

    private List<EventDisplay> prepareLessinsDisplay(List<Lesson> list) {
        List<EventDisplay> items = new LinkedList<>();
        for (Lesson lesson : list) {
            items.add(new EventDisplay(lesson));
        }
        return items;
    }

    public HashMap<String, List<EventDisplay>> PrepareLessonsToHash(List<DayDisplay> lessons) {
        HashMap<String, List<EventDisplay>> list = new HashMap<>();
        for (DayDisplay day : lessons) {
            for (PairDisplay pair : day.getDaylessons()) {
                list.put(day.getDay() + "_" + pair.getNumber(), pair.getLessons());
            }
        }
        return list;
    }
    
    public List<Lesson> GetAllLessons()
    {
        return lessonRepository.findAll();
    }
    
}
