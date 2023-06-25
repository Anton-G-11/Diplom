/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.entities.*;
import com.example.demo.services.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * @author sgrig
 */
@Controller
@RequestMapping("")
public class MainController {

    @Autowired
    UserService userService;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    GroupService groupService;

    @Autowired
    EventService eventService;

    @Autowired
    CreatorService creatorService;
    
    @Autowired
    ExcelService excelCreator;


    @Autowired
    private HttpSession httpSession;

    @GetMapping(value = {"/index", ""})
    public String index() {
        
        return "redirect:/lessons/";
    }

    @GetMapping(value = "teaches")
    public String teaches(Model model) {

        List<User> list = userService.GetAllTeachers();
        model.addAttribute("list", list);

        return "teaches";
    }

    @GetMapping("/login")
    public String login() {

        return "login";
    }

    @PostMapping("/login")
    public String logined(@RequestParam(name = "login") String login, @RequestParam(name = "password") String password) {

        User u = userService.GetUserByEmail(login);
        if (login.equals("admin@sm.sm")) {
            httpSession.setAttribute("loggedIn", 0);
            return "redirect:/lessons";
        } else
        if (u != null) {
            httpSession.setAttribute("loggedIn", u.getId());
            return "redirect:/lessons";
        } else 
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout() {
        httpSession.setAttribute("loggedIn", null);
        return "redirect:/lessons";
    }

    @GetMapping(value = "/lessons")
    public String lessons(Model model,
            @RequestParam(name = "monday", required = false) Optional<String> var_monday,
            @RequestParam(name = "teacher_id", required = false) Optional<Integer> var_teacher_id,
            @RequestParam(name = "group_id", required = false) Optional<Integer> var_group_id) {
        Integer teacher_id = var_teacher_id.orElseGet(() -> 0);
        Integer group_id = var_group_id.orElseGet(() -> 0);

        LocalDate currentDate = (var_monday.isPresent()) ? LocalDate.parse(var_monday.get()) : LocalDate.now();
        LocalDate currentMonday = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        model.addAttribute("currentMonday", currentMonday);
        LocalDate currentSaturday = currentMonday.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        model.addAttribute("currentSaturday", currentSaturday);

        LocalDate prevMonday = currentMonday.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        model.addAttribute("prevMonday", prevMonday);

        LocalDate nextMonday = currentMonday.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        model.addAttribute("nextMonday", nextMonday);

        List<DayDisplay> lessons = scheduleService.GetWeekLessons(teacher_id, group_id);

        HashMap<String, List<EventDisplay>> displaylist = scheduleService.PrepareLessonsToHash(lessons);
        HashMap<String, List<EventDisplay>> displayresult;
        if (httpSession.getAttribute("loggedIn") != null && (Integer)httpSession.getAttribute("loggedIn") > 0) {
            HashMap<String, List<EventDisplay>> events = scheduleService.PrepareEventsToHash(currentMonday, currentSaturday, userService.GetUser((Integer) httpSession.getAttribute("loggedIn")));

            displayresult = scheduleService.MergeList(displaylist, events);
        } else {
            displayresult = displaylist;
        }
        model.addAttribute("displaylist", displayresult);

        model.addAttribute("weekdays", ScheduleService.weekdays);
        model.addAttribute("paircomments", ScheduleService.paircomments);

        List<User> teachers = userService.GetAllTeachers();
        model.addAttribute("teachers", teachers);

        List<Group> groups = groupService.GetGroups();
        model.addAttribute("groups", groups);

        model.addAttribute("teacher_id", teacher_id);
        model.addAttribute("group_id", group_id);

        if (httpSession.getAttribute("loggedIn") == null) {
            return "lessons";
        } else if ((Integer) httpSession.getAttribute("loggedIn") == 0) {
            return "lessons_admin";
        } else if ((Integer) httpSession.getAttribute("loggedIn") > 0) {
            return "lessons_user";
        } else {
            return "lessons";
        }
    }
    
    @GetMapping(value = "/lecture/edit/{id:\\d+}")
    public String lessonEdit(Model model, @PathVariable(name = "id") Integer id)
    {
        Integer courseId=eventService.GetCourseByLesson(id).getId();
        return "redirect:/course/edit/"+courseId;
    }
    @GetMapping(value = "/lab/edit/{id:\\d+}")
    public String labEdit(Model model, @PathVariable(name = "id") Integer id)
    {
        Integer courseId=eventService.GetCourseByLesson(id).getId();
        return "redirect:/course/edit/"+courseId;
    }
    
    @GetMapping("/download")
    public ResponseEntity<Object> download() {

        if (httpSession.getAttribute("lessons") == null) {
            httpSession.setAttribute("lessons", creatorService.GetAllLessons());
        }
        List<Lesson> lessons = (List<Lesson>) httpSession.getAttribute("lessons");
        HashMap<String, List<Lesson>> displaylist = scheduleService.PrepareLessonsToExcel(lessons);
        XSSFWorkbook workbook = excelCreator.GetSсheduler(displaylist);

        try {
            File file = new File("/tmp/scheduler.xlsx");
            workbook.write(new FileOutputStream(file));
            workbook.close();

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file.getAbsoluteFile()));

            return ResponseEntity.ok()
                    .contentType(MediaType.ALL.valueOf(Files.probeContentType(file.toPath())))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.OK);
        }
    }

    @GetMapping("/import")
    public String importExcel() {
    
        return "import";
    }

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file) throws IOException{
        
        File excel = excelCreator.saveFile(file);
        List<Lesson> list = excelCreator.ProcessFile(excel);
        httpSession.setAttribute("lessons", list);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
