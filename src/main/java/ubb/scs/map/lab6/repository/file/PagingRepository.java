package ubb.scs.map.lab6.repository.file;


import ubb.scs.map.lab6.domain.Entity;
import ubb.scs.map.lab6.repository.Repository;

public interface PagingRepository<ID,E extends Entity<ID>> extends Repository<ID,E> {
    Page<E> findAll(Pageable pageable);
}
