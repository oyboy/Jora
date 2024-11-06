package com.main.Jora.calendar;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home/calendar")
public class CalendarController {
    @GetMapping
    public String getCalendar() {
        return "calendar";
    }
}