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
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.persistence.criteria.JoinType;

/**
 *
 * @author anton
 */
@Service
public class ScheduleService {

    public static HashMap<Integer, String> weekdays = new HashMap<Integer, String>() {
        {
            put(1, "Пн");
            put(2, "Вв");
            put(3, "Ср");
            put(4, "Чт");
            put(5, "Пт");
            put(6, "Сб");
        }
    };

    public static HashMap<Integer, String> paircomments = new HashMap<Integer, String>() {
        {
            put(1, "8:30");
            put(2, "9:25");
            put(3, "10:20");
            put(4, "11:15");
            put(5, "12:10");
            put(6, "13:05");
        }
    };

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private EventRepository eventRepository;

    public List<DayDisplay> GetWeekLessons(Integer teacher_id, Integer group_id) {

        Specification where = Specification.where(null);
        if (teacher_id > 0) {
            where = where.and((item, cq, cb) -> {
                return cb.equal(item.get("teacherId"), teacher_id);
            });
        }
        if (group_id > 0) {
            where = where.and((item, cq, cb) -> {
                return cb.equal(item.join("groups", JoinType.INNER).get("id"), group_id);
            });
        }
        List<Lesson> lessons = lessonRepository.findAll(where);
        if (group_id==0&&teacher_id==0)
            lessons = new ArrayList<>();

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

    public HashMap<String, List<Lesson>> PrepareLessonsToExcel(List<Lesson> lessons) {
        HashMap<String, List<Lesson>> list = new HashMap<>();
        for (Lesson lesson : lessons) {
            if (lesson.getSlot() != null) {

                String key = lesson.getSlot().getDay() + "_" + lesson.getSlot().getNumber();
                if (list.containsKey(key)) {
                    list.get(key).add(lesson);
                } else {
                    List<Lesson> splotlist = new ArrayList<>();
                    splotlist.add(lesson);
                    list.put(key, splotlist);
                }
            }
        }
        return list;
    }

    public HashMap<String, List<EventDisplay>> PrepareEventsToHash(LocalDate monday, LocalDate saturday, User user) {
        HashMap<String, List<EventDisplay>> list = new HashMap<>();

        try {
            Date startWeek = new SimpleDateFormat("yyyy-MM-dd").parse(monday.toString());
            Date endWeek = new SimpleDateFormat("yyyy-MM-dd").parse(saturday.toString());
            List<Event> allevents = eventRepository.findEventsByWeek(startWeek, endWeek);
            List<Event> events = allevents.stream().filter(e -> e.getUser() == user || e.getGroups().contains(user.getGroup())).collect(Collectors.toList());

            for (Event event : events) {
                EventDisplay item = new EventDisplay(event);
                String key = item.getDay() + "_" + item.getNumber();
                if (list.containsKey(key)) {
                    list.get(key).add(item);
                } else {
                    List<EventDisplay> items = new ArrayList<>();
                    items.add(item);
                    list.put(key, items);
                }
            }
            return list;
        } catch (ParseException exc) {

        }
        return list;
    }

    public HashMap<String, List<EventDisplay>> MergeList(HashMap<String, List<EventDisplay>> lessons, HashMap<String, List<EventDisplay>> events) {
        for (String key : events.keySet()) {
            if (lessons.containsKey(key)) {
                lessons.get(key).addAll(events.get(key));
            } else {
                lessons.put(key, events.get(key));
            }
        }
        return lessons;
    }

    public List<Lesson> GetAllLessons() {
        return lessonRepository.findAll();
    }

}
