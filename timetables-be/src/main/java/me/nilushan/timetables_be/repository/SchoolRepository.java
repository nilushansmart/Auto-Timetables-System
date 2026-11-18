package me.nilushan.timetables_be.repository;

import me.nilushan.timetables_be.model.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByCensusNo(String censusNo);
}
