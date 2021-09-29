package pods.service.rideservice.model;

import javax.persistence.LockModeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RideService {
    @Autowired
    private RideRepository rideRepository;

    public Ride save(Ride ride) {
        return rideRepository.save(ride);
    }

    public void delete(Ride ride) {
        rideRepository.delete(ride);
    }

    public Ride findById(Long rideId) {
        return rideRepository.findByRideId(rideId).orElse(null);
    }
}
