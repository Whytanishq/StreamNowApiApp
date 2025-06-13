package com.streamnow.api.repository;

import com.streamnow.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Set<User> findAllByIdIn(Set<Long> ids);

}