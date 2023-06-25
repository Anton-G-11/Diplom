/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.services;

import com.example.demo.dto.EventDisplay;
import com.example.demo.dto.LessonTmpl;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Sergiy
 */
@Service
public class ExcelService {
    
    @Autowired
    SlotRepository slotRepository;
    
    @Autowired
    LessonRepository lessonRepository;

    public XSSFWorkbook GetSсheduler(HashMap<String, List<Lesson>> displaylist) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sсheduler");

        CreateHeader(workbook, sheet);

        CellStyle headerstyle = createRowStyle(workbook);
        CellStyle style = createRowStyle(workbook);

        for (int r : ScheduleService.paircomments.keySet()) {

            Row row = sheet.createRow(r);
            Cell cell0 = row.createCell(0, CellType.STRING);
            cell0.setCellStyle(headerstyle);
            cell0.setCellValue(ScheduleService.paircomments.get(r));
            row.setHeightInPoints((float) 70);

            for (Integer coll : ScheduleService.weekdays.keySet()) {

                String key = coll + "_" + r;
                String value = "";
                if (displaylist.containsKey(key)) {
                    for (Lesson lesson : displaylist.get(key)) {
                        value += String.format("%d;%s;%s;%s;%s\n", lesson.getId(), lesson.getType(), lesson.getCourse().getName(), lesson.getTeacher(), lesson.getGroups());
                    }
                }
                Cell cell = row.createCell(coll, CellType.STRING);
                cell.setCellStyle(style);
                cell.setCellValue(value.trim());

            }
        }

        return workbook;
    }

    private void CreateHeader(XSSFWorkbook workbook, XSSFSheet sheet) {

        CellStyle style = createHeadingStyle(workbook);

        Row row = sheet.createRow(0);
        Cell cell0 = row.createCell(0, CellType.STRING);
        cell0.setCellValue("#");
        sheet.setColumnWidth(0, 1500);

        for (Integer coll : ScheduleService.weekdays.keySet()) {
            Cell cell = row.createCell(coll, CellType.STRING);
            cell.setCellStyle(style);
            cell.setCellValue(ScheduleService.weekdays.get(coll));
            sheet.setColumnWidth(coll, 14 * 1000);
        }
    }

    public List<Lesson> ProcessFile(File excel) throws EncryptedDocumentException, IOException {
        Workbook workbook = WorkbookFactory.create(excel);
        Sheet sheet = workbook.getSheetAt(0);

        List<LessonTmpl> list = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();

        for (int r = 1; r <= 6; r++) {
            Row row = sheet.getRow(r);
            for (int c = 1; c <= 6; c++) {
                Cell cell = row.getCell(c);
                String key = c + "_" + r;
                if (cell != null) {
                    String cellValue = dataFormatter.formatCellValue(cell).trim();
                    String[] lessons = cellValue.split("\n");
                    for (String lesson : lessons) {
                        String[] blocks = lesson.split(";");
                        if(blocks.length == 5){
                            Integer id = Integer.valueOf(blocks[0]);
                            String type = blocks[1];
                            String title = blocks[2];
                            String teacher = blocks[3];
                            String groups_block = blocks[4];
                            String[] groups = groups_block.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                            
                            //System.out.println(key + "=>" + title + " / " + teacher + " / " + groups.length);
                            
                            LessonTmpl tmpl = new LessonTmpl(id, type, c, ScheduleService.weekdays.get(c), r, ScheduleService.paircomments.get(r), title, teacher, groups_block);
                            list.add(tmpl);
                        }
                    }
                }
            }
        }
        List <Lesson> res = new ArrayList<>();
        for (LessonTmpl t:list)
        {
            Lesson l = lessonRepository.getReferenceById(t.getId());
            l.setSlot(slotRepository.findSlotInfo(t.getDay(), t.getNumber()).orElse(null));
            res.add(l);
        }
        return res;
    }

    public static CellStyle createHeadingStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setFontName("Courier New");
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        //font.setUnderline(Font.U_SINGLE);
        //font.setColor(HSSFColor.HSSFColorPredefined.DARK_GREEN.getIndex());
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    public static CellStyle createRowStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();

        /*
        Font font = workbook.createFont();
        font.setFontName("Courier New");
        font.setFontHeightInPoints((short)14);
        style.setFont(font);
         */
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setWrapText(true);

        return style;
    }

    public File saveFile(MultipartFile storingFile) throws IOException {

        String filename = (storingFile.getOriginalFilename() != null) ? storingFile.getOriginalFilename() : storingFile.getName();
        File dir = new File("/tmp/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File loadFile = new File("/tmp/", filename);

        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(loadFile));
        stream.write(storingFile.getBytes());
        stream.flush();
        stream.close();
        return loadFile;
    }
}
