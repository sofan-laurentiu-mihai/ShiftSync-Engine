package com.shiftsync.repository;

import com.shiftsync.model.DepartmentRole;
import com.shiftsync.model.Shift;
import com.shiftsync.model.ShiftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    // With JPQL, I will work with Java classes, not with the SQL tables
    // I will search all the shifts of an employees on a certain time frame, chronologically ordered
    @Query("SELECT s FROM Shift s WHERE s.assignedEmployee.id = :employeeId " +
            // Fixed a bug where I was only searching in a specific window
            "AND s.startTime >= :to AND s.endTime <= :from ORDER BY s.startTime ASC")
    List<Shift> findShiftsForEmployeeInInterval(
            @Param("employeeId") UUID employeeId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // Fastly finds the urgent shifts remained unoccupied for a specific role
    List<Shift> findByStatusAndRequiredRole(ShiftStatus status, DepartmentRole requiredRole);
}