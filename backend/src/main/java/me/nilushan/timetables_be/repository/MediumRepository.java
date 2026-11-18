package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.Medium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediumRepository extends JpaRepository<Medium, Short> {
    Optional<Medium> findByCode(String code);
}
