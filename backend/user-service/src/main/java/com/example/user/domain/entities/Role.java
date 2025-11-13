package com.example.user.domain.entities;

import com.example.user.domain.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Role extends com.example.user.domain.entities.BaseClass<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; //  "ADMIN", "USER"
}

