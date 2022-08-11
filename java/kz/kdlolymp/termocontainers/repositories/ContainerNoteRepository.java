package kz.kdlolymp.termocontainers.repositories;

import kz.kdlolymp.termocontainers.entity.ContainerNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ContainerNoteRepository extends PagingAndSortingRepository<ContainerNote, Long> {

    ContainerNote findContainerNoteById(Long id);

}
