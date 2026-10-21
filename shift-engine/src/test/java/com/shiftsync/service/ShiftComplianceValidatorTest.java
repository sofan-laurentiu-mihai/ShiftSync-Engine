package com.shiftsync.service;

import com.shiftsync.model.DepartmentRole;
import com.shiftsync.model.Employee;
import com.shiftsync.model.Shift;
import com.shiftsync.model.ShiftStatus;
import com.shiftsync.repository.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

// Activate Mockito for rapid unit tests without booting the entire Spring system
@ExtendWith(MockitoExtension.class)
class ShiftComplianceValidatorTest {

    // Simulating the database
    @Mock
    private ShiftRepository shiftRepository;

    // Inject the simulation into the mock
    @InjectMocks
    private ShiftComplianceValidator validator;

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        // We prepare an employee for the test
        testEmployee = Employee.builder()
                .id(UUID.randomUUID())
                .fullName("Sofan Laurentiu")
                .email("laurentiu.sofan@spital.ro")
                .role(DepartmentRole.SENIOR_NURSE)
                .department("ATI")
                .accumulatedWeeklyHours(24) // Has 24h worked from the 48h
                .build();
    }

    @Test
    @DisplayName("Allocation denied because rest hours are less than 11h (10h)")
    void shouldRejectShiftWhenRestIsLessThan11Hours() {
        // Existing shift: Today, 08:00 - 20:00
        LocalDateTime existingStart = LocalDateTime.of(2026, 10, 5, 8, 0);
        LocalDateTime existingEnd = LocalDateTime.of(2026, 10, 5, 20, 0);

        Shift existingShift = Shift.builder()
                .id(UUID.randomUUID())
                .startTime(existingStart)
                .endTime(existingEnd)
                .requiredRole(DepartmentRole.SENIOR_NURSE)
                .status(ShiftStatus.SCHEDULED)
                .assignedEmployee(testEmployee)
                .build();

        // New shift proposed: Tomorrow, 06:00 - 14:00 (from today at 20:00 to tomorrow at 06:00, the time frame is 10h)
        LocalDateTime proposedStart = LocalDateTime.of(2026, 10, 6, 6, 0);
        LocalDateTime proposedEnd = LocalDateTime.of(2026, 10, 6, 14, 0);

        Shift proposedShift = Shift.builder()
                .id(UUID.randomUUID())
                .startTime(proposedStart)
                .endTime(proposedEnd)
                .requiredRole(DepartmentRole.SENIOR_NURSE)
                .status(ShiftStatus.VACANT_EMERGENCY)
                .build();

        // We teach the simulation to return existing shift when the validator is searching for it
        when(shiftRepository.findShiftsForEmployeeInInterval(eq(testEmployee.getId()), any(), any()))
                .thenReturn(List.of(existingShift));

        // We run the method we test
        boolean isEligible = validator.isEligibleForShift(testEmployee, proposedShift);

        // Check if the result if false (legally blocked shift)
        assertFalse(isEligible, "System blocks the shift because the minimum rest hours have not been reached (11h)!");
    }

    @Test
    @DisplayName("System allows the allocation if the shift is 12h (>=11h")
    void shouldApproveShiftWhenRestIsSufficient() {
        // Existing shift: Today, 08:00 - 20:00
        LocalDateTime existingStart = LocalDateTime.of(2026, 10, 5, 8, 0);
        LocalDateTime existingEnd = LocalDateTime.of(2026, 10, 5, 20, 0);

        Shift existingShift = Shift.builder()
                .id(UUID.randomUUID())
                .startTime(existingStart)
                .endTime(existingEnd)
                .requiredRole(DepartmentRole.SENIOR_NURSE)
                .status(ShiftStatus.SCHEDULED)
                .assignedEmployee(testEmployee)
                .build();

        // New shift proposed: Tomorrow, 08:00 - 16:00 (from today at 20:00 to tomorrow at 08:00, the time frame is 12h)
        LocalDateTime proposedStart = LocalDateTime.of(2026, 10, 6, 8, 0);
        LocalDateTime proposedEnd = LocalDateTime.of(2026, 10, 6, 16, 0);

        Shift proposedShift = Shift.builder()
                .id(UUID.randomUUID())
                .startTime(proposedStart)
                .endTime(proposedEnd)
                .requiredRole(DepartmentRole.SENIOR_NURSE)
                .status(ShiftStatus.VACANT_EMERGENCY)
                .build();

        when(shiftRepository.findShiftsForEmployeeInInterval(eq(testEmployee.getId()), any(), any()))
                .thenReturn(List.of(existingShift));

        boolean isEligible = validator.isEligibleForShift(testEmployee, proposedShift);

        // Checks if the result is true
        assertTrue(isEligible, "Systems allows the allocation because is exceeds the minimum legal rest hours (11h)!");
    }
}
