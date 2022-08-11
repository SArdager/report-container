package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.Probe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface ProbeRepository extends JpaRepository<Probe, Integer> {

    Probe findProbeById(int probeId);
    Probe findProbeByProbeName(String probeName);
}
