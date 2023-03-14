package com.team29.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.team29.backend.model.Calendar;

public interface CalendarRepository extends JpaRepository<Calendar,Long> {
    
}
