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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq_gen")
    @SequenceGenerator(name = "role_seq_gen", sequenceName = "roles_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; //  "ADMIN", "MODERATOR, "USER"
}

