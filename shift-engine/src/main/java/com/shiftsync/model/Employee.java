package com.shiftsync.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

// I am telling to Hibernate to create the table "employees" in database
@Entity
@Table(name = "employees")
// Here, I used Lombok to generate automatically getters, setters and constructors
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    // Primary key automatically generated in the format of UUID
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Full name of the employee noted in Java and SQL
    @NotBlank(message = "Name is mandatory")
    @Column(nullable = false)
    private String fullName;

    // Unique email address for each employee
    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is mandatory")
    @Column(nullable = false, unique = true)
    private String email;

    // Role extracted from the Enum
    // EnumType.STRING saves the text (e.g.: 'DOCTOR'), not the index (0, 1)
    @NotNull(message = "Role is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentRole role;

    // Department or medical body
    @NotBlank(message = "Department is mandatory")
    @Column(nullable = false)
    private String department;

    // Hours worked in current week (48h check)
    @Builder.Default
    @Column(nullable = false)
    private int accumulatedWeeklyHours = 0;
}