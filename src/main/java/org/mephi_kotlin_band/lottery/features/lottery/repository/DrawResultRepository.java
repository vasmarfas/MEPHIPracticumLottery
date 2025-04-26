package org.mephi_kotlin_band.lottery.features.lottery.repository;

import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.mephi_kotlin_band.lottery.features.lottery.model.DrawResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DrawResultRepository extends JpaRepository<DrawResult, UUID> {
    Optional<DrawResult> findByDraw(Draw draw);
    Optional<DrawResult> findByDrawId(UUID drawId);
}
