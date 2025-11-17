package trinity.play2learn.backend.admin.year.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import trinity.play2learn.backend.admin.year.models.Year;

public interface IYearRepositoryPaginated extends JpaRepository<Year, Long>, JpaSpecificationExecutor<Year> {
    
}
