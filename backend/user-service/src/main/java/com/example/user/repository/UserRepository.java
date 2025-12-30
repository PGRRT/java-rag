package com.example.user.repository;

import com.example.user.domain.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = { "role" })
    Optional<User> findUserWithRoleByEmail(String email);

    @EntityGraph(attributePaths = { "role" })
    Optional<User> findUserWithRoleById(UUID id);

}
