package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.ConsultationAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConsultationAppointmentRepository extends JpaRepository<ConsultationAppointment, Long> {
    List<ConsultationAppointment> findByStudentIdOrderByAppointmentDateDesc(Long studentId);
    List<ConsultationAppointment> findByStatus(String status);
}
