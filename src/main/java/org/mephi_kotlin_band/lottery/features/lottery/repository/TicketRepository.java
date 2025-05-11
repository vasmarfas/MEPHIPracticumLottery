package org.mephi_kotlin_band.lottery.features.lottery.repository;

import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByUser(User user);
    List<Ticket> findByUserAndDraw(User user, Draw draw);
    List<Ticket> findByDraw(Draw draw);
    List<Ticket> findByStatus(Ticket.Status status);
    List<Ticket> findByDrawAndStatus(Draw draw, Ticket.Status status);
    Optional<Ticket> findTopByUserAndDrawOrderByCreatedAtDesc(User user, Draw draw);
} 