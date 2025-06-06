package com.cts.employee_management.entity;

import com.cts.employee_management.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    String employeeName;

    @Email @Column(unique = true)
    String email;

    @Enumerated(EnumType.STRING)
    Role role;

    @ManyToOne
    @JoinColumn
    private Shift shift;

    @ManyToOne
    @JoinColumn(name="managerId")
    private Employee manager;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    List<Employee> teamMembers;
}
