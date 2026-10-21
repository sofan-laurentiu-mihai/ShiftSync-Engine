package com.shiftsync.service;

import com.shiftsync.model.Employee;
import com.shiftsync.model.Shift;
import com.shiftsync.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

// @Service marks the class as a business service controlled automatically by Spring
// @RequiredArgsConstructor (Lombok) generates the constructor from which we inject ShiftRepository
@Service
@RequiredArgsConstructor
public class ShiftComplianceValidator {

    private final ShiftRepository shiftRepository;

    // Romanian Work Code rules in the sanitary field
    private static final int MINIMUM_REST_HOURS = 11;
    private static final int MAXIMUM_WEEKLY_HOURS = 48;

    /**
     * Checks if an employee can legally work a shift without breaking the Work Code rules.
     * Returns true if the allocation is 100% legal, and false is if breaks any rules.
     */
    public boolean isEligibleForShift(Employee employee, Shift proposedShift) {
        // Rule 1: We check the maximum limit for the week (48h)
        long proposedDurationHours = Duration.between(proposedShift.getStartTime(), proposedShift.getEndTime()).toHours();
        if (employee.getAccumulatedWeeklyHours() + proposedDurationHours > MAXIMUM_WEEKLY_HOURS) {
            return false; // Maximum working hours exceeded
        }

        // Only look for adjacent shifts (24h before and 24h after the desired shift)
        LocalDateTime windowStart = proposedShift.getStartTime().minusHours(24);
        LocalDateTime windowEnd = proposedShift.getEndTime().plusHours(24);

        List<Shift> surroundingShifts = shiftRepository.findShiftsForEmployeeInInterval(
                employee.getId(),
                windowStart,
                windowEnd
        );

        for (Shift existingShift : surroundingShifts) {
            // Rule 2: Check for overlap in the schedule
            if (isOverlapping(existingShift, proposedShift)) {
                return false;
            }

            // Rule 3: Check if the minimum rest hours (11h)
            /*
            * BUGFIX: We used isBefore/isAfter which completely jumped over the shift which started directly
            * after the existing shift ended.
            * Then, 0 rest hours passed directly undetected. Now, !isBefore/!isAfter covers the limit (equal cases)
             */
            if (!existingShift.getEndTime().isBefore(proposedShift.getStartTime())) {
                // Existing shift is before (or starts exactly at) the one proposed
                long restHoursBefore = Duration.between(existingShift.getEndTime(), proposedShift.getStartTime()).toHours();
                if (restHoursBefore < MINIMUM_REST_HOURS) {
                    return false; // Rest before the proposed shift is under 11 hours
                }
            } else if (!existingShift.getStartTime().isAfter(proposedShift.getEndTime())) {
                // Existing shift is after (or ends exactly at) the one proposed
                long restHoursAfter = Duration.between(proposedShift.getEndTime(), existingShift.getStartTime()).toHours();
                if (restHoursAfter < MINIMUM_REST_HOURS) {
                    return false; // Rest until the next shift is under 11 hours
                }
            }
        }

        // All the check successfully passed
        return true;
    }

    // With this method, we check if two shifts are physically overlapping.
    private boolean isOverlapping(Shift s1, Shift s2) {
        return s1.getStartTime().isBefore(s2.getEndTime()) && s2.getStartTime().isBefore(s1.getEndTime());
    }
}