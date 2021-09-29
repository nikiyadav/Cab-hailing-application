package pods.service.cab.model;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CabService {
    @Autowired
    private CabRepository cabRepository;

    public Cab save(Cab cab) {
        return cabRepository.save(cab);
    }

    public Cab findById(Long cabId) {
        return cabRepository.findByCabId(cabId).orElse(null);
    }

    public Cab findByRideId(Long rideId) {
        return cabRepository.findByRideId(rideId).orElse(null);
    }

    // This will return the three available cabs closest to sourceLoc
    public List<Cab> findAvailable(Long sourceLoc) {
        return cabRepository.findAvailable(sourceLoc, PageRequest.of(0, 3)).orElse(null);
    }

}
