package com.gameet.match.repository;

import com.gameet.match.entity.MatchAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchAppointmentRepository extends JpaRepository<MatchAppointment, Long> {
}
