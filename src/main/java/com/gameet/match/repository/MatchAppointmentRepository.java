package com.gameet.match.repository;

import com.gameet.match.entity.MatchAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchAppointmentRepository extends JpaRepository<MatchAppointment, Long> {

    List<MatchAppointment> findAllByAppointmentAt(LocalDateTime localDateTime);
}
