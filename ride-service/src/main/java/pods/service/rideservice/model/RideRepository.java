package pods.service.rideservice.model;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RideRepository extends JpaRepository <Ride, Long> {

    // Using pessimistic lock to lock the table. 
    // It guaranteed that table is locked immediately when a transaction begins.
    // and transaction releases the lock either by committing or rolling back the transaction.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Ride> findByRideId(Long rideId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void delete(Ride ride);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Ride save(Ride ride);
}
