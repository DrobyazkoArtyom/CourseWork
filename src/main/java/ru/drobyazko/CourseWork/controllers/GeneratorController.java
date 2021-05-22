package ru.drobyazko.CourseWork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.drobyazko.CourseWork.common.TimeTable;
import ru.drobyazko.CourseWork.services.TimeTableGeneratorService;

@RestController
public class GeneratorController {

    private final TimeTableGeneratorService timeTableGeneratorService;

    @Autowired
    public GeneratorController(TimeTableGeneratorService timeTableGeneratorService) {
        this.timeTableGeneratorService = timeTableGeneratorService;
    }

    @GetMapping("/generate")
    public ResponseEntity<TimeTable> getTimetable() {
        return ResponseEntity.ok(timeTableGeneratorService.generateTimeTable());
    }

}
