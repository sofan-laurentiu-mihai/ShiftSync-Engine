package com.shiftsync;

import com.shiftsync.model.DepartmentRole;
import com.shiftsync.model.Employee;
import com.shiftsync.model.Shift;
import com.shiftsync.model.ShiftStatus;
import com.shiftsync.repository.EmployeeRepository;
import com.shiftsync.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// @Component tells Spring to automatically control the class
// CommandLineRunner executes run() command one time, right after the server has loaded
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;

    @Override
    public void run(String... args) {
        // If we already have saved data, we do nothing
        if (employeeRepository.count() > 0) {
            log.info("Database had data already. Omitting population");
            return;
        }

        log.info("Database loaded with demonstrative data for the hospital...");

        // 1. We create a medic for the ATI section
        Employee doctor = employeeRepository.save(Employee.builder()
                .fullName("Dr. Alexandru Radu")
                .email("alexandru.radu@spital.ro")
                .role(DepartmentRole.SENIOR_DOCTOR)
                .department("ATI")
                .accumulatedWeeklyHours(20)
                .build());

        // 2. Create a head nurse
        Employee nurse = employeeRepository.save(Employee.builder()
                .fullName("Elena Popescu")
                .email("elena.popescu@spital.ro")
                .role(DepartmentRole.SENIOR_NURSE)
                .department("ATI")
                .accumulatedWeeklyHours(32)
                .build());

        // 3. Create a junior nurse
        Employee junior = employeeRepository.save(Employee.builder()
                .fullName("Mihai Ionescu")
                .email("mihai.ionescu@spital.ro")
                .role(DepartmentRole.JUNIOR_NURSE)
                .department("UPU")
                .accumulatedWeeklyHours(16)
                .build());

        // 4. Create a shift for Dr.Radu
        shiftRepository.save(Shift.builder()
                .startTime(LocalDateTime.now().plusDays(1).withHour(8).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(1).withHour(20).withMinute(0))
                .requiredRole(DepartmentRole.SENIOR_DOCTOR)
                .status(ShiftStatus.SCHEDULED)
                .assignedEmployee(doctor)
                .build());

        // 5. Create an unoccupied shift for a head nurse
        shiftRepository.save(Shift.builder()
                .startTime(LocalDateTime.now().plusDays(2).withHour(8).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(2).withHour(20).withMinute(0))
                .requiredRole(DepartmentRole.SENIOR_NURSE)
                .status(ShiftStatus.VACANT_EMERGENCY)
                .assignedEmployee(null) // It's empty. The AI will intervene here
                .build());

        log.info("Test data successfully created in PostgreSQL!");
    }
}