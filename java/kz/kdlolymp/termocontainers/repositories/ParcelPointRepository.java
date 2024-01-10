package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.ParcelPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface ParcelPointRepository extends JpaRepository<ParcelPoint,Long> {

    List<ParcelPoint> findAllByParcelNumber(String parcelNumber);


}
