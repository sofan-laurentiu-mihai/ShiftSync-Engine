package com.shiftsync.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

// I am telling Hibernate to create "shifts" table in database
@Entity
@Table(name = "shifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shift {

    // Unique identifier of shift
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Moment of clock-in
    @NotNull(message = "Clock-in hour is mandatory")
    @Column(nullable = false)
    private LocalDateTime startTime;

    // Moment of clock-out
    @NotNull(message = "Clock-out hour is mandatory")
    @Column(nullable = false)
    private LocalDateTime endTime;

    // Minimum role for a member to take another shift
    @NotNull(message = "Necessary role is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentRole requiredRole;

    // Current shift status
    @NotNull(message = "Shift status in mandatory")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftStatus status;

    // Relation to the employee: more shifts to the same employee (Many-to-One)
    // LAZY = gets employee data only at request, necessary for not slowing down the app
    // Could be null if the shift is urgently deallocated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;

    // Optimistic Locking: avoids conflict when two members try to take the same shift
    @Version
    private Long version;
}