package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.BetweenPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BetweenPointRepository extends JpaRepository<BetweenPoint, Long> {


    BetweenPoint findBetweenPointById(Long id);

}
