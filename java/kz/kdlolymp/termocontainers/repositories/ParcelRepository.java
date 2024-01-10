package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ParcelRepository extends JpaRepository<Parcel,Long> {

    Parcel findParcelById(Long parcelId);

    Parcel findParcelByParcelNumber(String parcelNumber);

    List<Parcel> findAllByParentNumber (String parentNumber);

}
