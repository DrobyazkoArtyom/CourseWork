package ru.drobyazko.CourseWork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.drobyazko.CourseWork.common.TimeTable;
import ru.drobyazko.CourseWork.services.TimeTableSolverService;

@RestController
public class SolverController {

    private final TimeTableSolverService timeTableSolverService;
    private static int counter = 0;

    @Autowired
    public SolverController(TimeTableSolverService timeTableSolverService) {
        this.timeTableSolverService = timeTableSolverService;
    }

    @GetMapping("/solveTimeTable")
    public ResponseEntity<TimeTable> solveTimeTable() {
        RestTemplate restTemplate = new RestTemplate();

        String resourceUrl = "http://localhost:8080/newTimeTable";
        ResponseEntity<TimeTable> response = restTemplate.getForEntity(resourceUrl, TimeTable.class);

        TimeTable timeTable = response.getBody();
        timeTableSolverService.solveTimeTable(timeTable);

        resourceUrl = "http://localhost:8080/timeTable/timeTable" + counter;
        ++counter;
        restTemplate.postForEntity(resourceUrl, timeTable, TimeTable.class);

        return ResponseEntity.ok(timeTable);
    }

}
