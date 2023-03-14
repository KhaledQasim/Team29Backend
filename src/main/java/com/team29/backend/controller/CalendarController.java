package com.team29.backend.controller;

import com.team29.backend.exception.NoEventE;
import com.team29.backend.model.Calendar;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;

import com.team29.backend.repository.CalendarRepository;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, allowCredentials = "true")
public class CalendarController {
    @Autowired
    CalendarRepository calendarRepository;
    
    @GetMapping("/get")
    List<Calendar> getAllCalendars() {
        return calendarRepository.findAll();
    }

    @PostMapping("/post")
    Calendar newCalendar(@RequestBody Calendar newCalendar) {
        return calendarRepository.save(newCalendar);
    }


 
    @DeleteMapping("/delete/{id}")
    String deleteEvent(@PathVariable Long id) {
        if (!calendarRepository.existsById(id)) {
            throw new NoEventE(id);
        }
        calendarRepository.deleteById(id);
        return "Event with id: " + id + " has been deleted!";
    }


}
