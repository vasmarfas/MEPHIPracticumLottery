package org.mephi_kotlin_band.lottery.features.lottery.repository;

import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DrawRepository extends JpaRepository<Draw, UUID> {
    List<Draw> findByStatus(Draw.Status status);
    List<Draw> findByStartTimeBefore(LocalDateTime time);
    List<Draw> findByStartTimeBeforeAndStatus(LocalDateTime time, Draw.Status status);
}
