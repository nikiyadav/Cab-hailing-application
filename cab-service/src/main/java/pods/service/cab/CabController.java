package pods.service.cab;

import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import pods.service.cab.model.Cab;
import pods.service.cab.model.CabService;


@Transactional(isolation = Isolation.REPEATABLE_READ)
@EnableAutoConfiguration
@RestController
public class CabController {

    @Autowired
    private CabService cabService;

    @GetMapping("/")
    public String index() {
        return "Cab service";
    }

    // Tried using synchronized keyword but that is a big performance bottleneck
    // as synchronization will only one thread to access the method and any other
    // request would be delayed irrespective of the cabId
    @GetMapping("requestRide")
    // This method is called from rideService.requestRide method
    // Cab accepts every alternate request
    // It returns true if cab accepts the ride request else return false
    public Boolean requestRide( Long cabId,
                                Long sourceLoc,
                                Long rideId,
                                Long destinationLoc) throws InterruptedException {
        // Checking for negative sourceLoc and destinationLoc
        if (sourceLoc.compareTo(0L) < 0 || destinationLoc.compareTo(0L) < 0)
            return false;

        Cab cab = cabService.findById(cabId);
        System.out.println("Requestride: cab= " + cab.getCabId() +" Major State= " + cab.getMajorState() + 
        " Minor State= " + cab.getMinorState() + " numRides "+ cab.getNumberRides() + " Ride id " + rideId);

        // If the cab is in signed-In and available state, it has not given last ride
        // then changing its state to "committed"
        if (cab != null &&
            cab.getMajorState().equals("signed-in") &&
            cab.getMinorState().equals("available")) {

            // cab accepts every alternate request
            if (!cab.getLastRide()) {
                // cab state changed to committed state
                cab.setMinorState("committed")
                    .setRideId(rideId)
                    .setSourceLoc(sourceLoc)
                    .setDestinationLoc(destinationLoc);
                System.out.println("Cab "  + cab.getCabId() + " is being put in committed state.");

                cabService.save(cab);
                System.out.println("Cab state saved");
                return true;
            }
            // If the code reached here that means cab was available but refused the ride, so setting lastRide = False
            cab.setLastRide(false);
            cabService.save(cab);
       }
        return false;
    }

    @GetMapping("rideStarted")
    // This method is called from rideService.requestRide method
    // It returns true if cab starts the ride and goes into "giving-ride" state, else it returns false
    public Boolean rideStarted( Long cabId,
                                Long rideId) {
        Cab cab = cabService.findById(cabId);
        
        // If cab is in committed state for the given rideId then its state is changed to 
        // giving-ride
        if (cab != null && cab.getRideId().compareTo(rideId) == 0 &&
            cab.getMinorState().equals("committed")) {
            // cab state changed from committed to giving-ride
            cab.setMinorState("giving-ride")
                .setLastRide(true)
                .setNumberRides(cab.getNumberRides()+1);
            cabService.save(cab);
            return true;
        }
        return false;
    }

    @GetMapping("rideCancelled")
    // This method is called from rideService.requestRide if wallet transaction fails.
    // It returns true if ride is cancelled successfully
    public Boolean rideCancelled( Long cabId,
                                  Long rideId) {
        Cab cab = cabService.findById(cabId);
        
        /* Here we need to check for majorState as minorState may be null
           in case cab is signed-out */
        // If cab is in committed state for the given rideId then its state is changed to available
        if (cab != null && cab.getMajorState().equals("signed-in") &&
            cab.getMinorState().equals("committed") &&
            cab.getRideId().compareTo(rideId) == 0) {

            // minor state changed to available
            cab.setMinorState("available")
                .setDestinationLoc(null)
                .setSourceLoc(null)
                .setLastRide(true)
                .setRideId(null);
            cabService.save(cab);
            return true;
        }
        return false;
    }

    @GetMapping("rideEnded")
    // This method is called to end the ride
    // It returns true if ride was ended successfully
    public Boolean rideEnded(Long cabId,
                             Long rideId) {
        Cab cab = cabService.findById(cabId);
        if (cab != null &&
            cab.getMajorState().equals("signed-in") &&
            cab.getMinorState().equals("giving-ride") &&
            cab.getRideId().compareTo(rideId) == 0 ) {
            // Intimate the rideService to end the ride
            WebClient client = WebClient.create();
            String jsonResponse = client.get()
                    .uri("ride-service:8081/rideEnded?rideId=" + rideId).retrieve()
                    .bodyToMono(String.class).block();
            // jsonResponse contains the response of the rideService, parse to Boolean
            if (Boolean.parseBoolean(jsonResponse)) {
                cab.setMinorState("available")
                    .setInitialPos(cab.getDestinationLoc())
                    .setRideId(null)
                    .setSourceLoc(null)
                    .setDestinationLoc(null);

                cabService.save(cab);
                return true;
            }
        }
        return false;
    }


    @GetMapping("customRideEnded")
    // This method ends the ride in the cab service without calling the rideService
    // It returns true if ride is ended successfully
    public Boolean customRideEnded(Long cabId,
                             Long rideId) {
        Cab cab = cabService.findById(cabId);
        if (cab != null &&
            cab.getMajorState().equals("signed-in") &&
            cab.getMinorState().equals("giving-ride") &&
            cab.getRideId().compareTo(rideId) == 0 ) {
            cab.setMinorState("available")
                .setInitialPos(cab.getDestinationLoc())
                .setRideId(null)
                .setSourceLoc(null)
                    .setDestinationLoc(null);

            cabService.save(cab);
            return true;
        }
        return false;
    }



    @GetMapping("signIn")
    // This method is called to sign-in a cab
    // It returns true if sign-in is successful
    public Boolean signIn(Long cabId,
                           Long initialPos) {
        // Checking that initial position is non-negative
        if (initialPos.compareTo(0L) < 0)
            return false;
        
        Cab cab = cabService.findById(cabId);

         // If cab is in signed-out state, then only sign-In is attempted
        if (cab != null && cab.getMajorState().equals("signed-out")) {
            // Calling rideService to sign-In the cab
            WebClient client = WebClient.create();
            String jsonResponse = client.get()
                .uri("ride-service:8081/cabSignsIn?cabId=" + cabId + "&initialPos=" + initialPos)
                .retrieve().bodyToMono(String.class).block();

            // If rideService successfully signs-in the cab then only cab is signed-in here
            if (Boolean.parseBoolean(jsonResponse)) {
                cab.setMajorState("signed-in")
                    .setMinorState("available")
                    .setInitialPos(initialPos);
                cabService.save(cab);
                return true;
            }
        }
        return false;
    }

    @GetMapping("signOut")
    // This method is called to sign-out a cab
    // It returns true if sign-out is successful
    public Boolean signOut( Long cabId) {
        Cab cab = cabService.findById(cabId);

        // If cab is in signed-in state, then only sign-out is attempted
        if ( cab != null && cab.getMajorState().equals("signed-in")) {
            WebClient client = WebClient.create();

            // Calling rideService to sign-out the cab
            String jsonResponse = client.get()
                .uri("ride-service:8081/cabSignsOut?cabId=" + cabId).retrieve()
                .bodyToMono(String.class).block();
            
            // If rideService successfully signs-out the cab then only cab is signed-out here
            if (Boolean.parseBoolean(jsonResponse)) {
                cab.setMajorState("signed-out")
                    .setMinorState(null)
                    .setInitialPos(null)
                    .setDestinationLoc(null)
                    .setRideId(null)
                    .setSourceLoc(null)
                    .setLastRide(false)
                    .setNumberRides(0L);
                cabService.save(cab);
                return true;
            }
       }
        return false;
    }


    @GetMapping("customSignOut")
    // This method signs out cabs in the cab service without calling the rideService
    // It returns true if sign-out successfully
    public Boolean customSignOut( Long cabId) {
        Cab cab = cabService.findById(cabId);
        if ( cab != null && cab.getMajorState().equals("signed-in")) {
            cab.setMajorState("signed-out")
                .setMinorState(null)
                .setInitialPos(null)
                .setDestinationLoc(null)
                .setRideId(null)
                .setSourceLoc(null)
                .setLastRide(false)
                .setNumberRides(0L);
            cabService.save(cab);
            return true;
        }
        return false;
    }


    @GetMapping("numRides")
    // This method returns the number of rides given by the cab since last sign-in
    public Long numRides(Long cabId) {
        Cab cab = cabService.findById(cabId);
        if (cab != null) {
            // No need to return 0 explicitly when signed-out as numberRides is reset on signOut
            // as the numRides are being counted after last sign-in
            return cab.getNumberRides();
        }
        return -1L;
    }
}
