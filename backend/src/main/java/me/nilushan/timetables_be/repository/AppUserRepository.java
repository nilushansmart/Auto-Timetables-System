package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByEmailAndActiveTrue(String email);
    boolean existsByEmail(String email);
    Page<AppUser> findBySchoolId(Long schoolId, Pageable pageable);
}
