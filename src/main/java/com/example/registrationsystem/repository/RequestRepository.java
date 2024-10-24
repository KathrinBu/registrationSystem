package com.example.registrationsystem.repository;

import com.example.registrationsystem.model.Request;
import com.example.registrationsystem.model.RequestStatus;
import com.example.registrationsystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Page<Request> findByUser(User user, Pageable pageable);
    Page<Request> findByStatus(RequestStatus status, Pageable pageable);
    Page<Request> findByUserIgnoreCaseAndStatus(String name, RequestStatus status, Pageable pageable);
    Optional<Request> findByIdAndUser(Long id, User user);
}
