package pods.service.rideservice.model;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface CabRepository extends JpaRepository<Cab, Long> {

    // Using pessimistic lock to lock the table. 
    // It guaranteed that table is locked immediately when a transaction begins.
    // and transaction releases the lock either by committing or rolling back the transaction
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cab1 FROM Cab cab1 where cab1.cabId IN (select cab2.cabId from Cab  cab2 where cab2.minorState='available' ORDER BY ABS(cab2.initialPos - :sourceLoc))")
    Optional<List<Cab>> findAvailable(Long sourceLoc, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Cab> findAll();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Cab> findByRideId(Long rideId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Cab> findByMinorState(String minorState);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Cab> findByMajorState(String majorState);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Cab> findById(Long cabId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Cab save(Cab cab);
}


