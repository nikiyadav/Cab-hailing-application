package pods.service.rideservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import pods.service.rideservice.model.CabService;
import pods.service.rideservice.model.Cab;
import pods.service.rideservice.model.Ride;
import pods.service.rideservice.model.RideService;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import org.springframework.data.domain.Sort;
import java.lang.Math;



@Transactional(isolation = Isolation.REPEATABLE_READ)
@EnableAutoConfiguration
@RestController
public class RideServiceController {

    @Autowired
    private RideService rideService;
    @Autowired
    private CabService cabService;

    @PersistenceContext
    EntityManager em;

    @GetMapping("/")
    public String index() {
        return "Hello rideService";
    }

    @GetMapping("init")
    // This method is used to initialise the cab database
    public void init() {
        System.out.println("Initiating cab database");
        try {
            File myFile = new File("/mnt/mount-point/IDs.txt");
            Scanner myReader = new Scanner(myFile);
            myReader.nextLine();
            while (myReader.hasNext()) {
                String data = myReader.nextLine();

                System.out.println(data);
                if (data.equals("****"))
                    break;
                Long id = Long.parseLong(data);
                Cab cab = new Cab(id);

                cabService.save(cab);
            }
        } catch (Exception e) {

            e.getMessage();
        }
    }

    @GetMapping("rideEnded")
    // This method is called to end the ride.
    // It returns true if ride is ended successfully
    public Boolean rideEnded(Long rideId) {
        Cab cab = cabService.findByRideId(rideId);
        Ride ride = rideService.findById(rideId);

        System.out.println("Cab Id " + cab.getCabId() + "with ride Id " + cab.getRideId() + " to be cancelled "
                + ride.getRideId());
        // If the give ride exists and is in ongoing state, then its state is changed to completed
        // & cab state is changed to avaiable. Fields related to ride are also reset.
        // cab position is set to ride's destination location
        if (ride != null && ride.getStatus() != null && ride.getStatus().equals("ongoing")) {
            cab.setMinorState("available").setRideId(null).setSourceLoc(null).setInitialPos(cab.getDestinationLoc())
                    .setDestinationLoc(null);
            ride.setStatus("completed");

            rideService.save(ride);
            cabService.save(cab);
            return true;
        }
        return false;
    }

    @GetMapping("cabSignsIn")
    // This method is called to sign-in a cab from cabService
    // It returns true if sign-in is successful
    public boolean cabSignsIn(Long cabId, Long initialPos) {
        // Checking that initial position is non-negative
        if (initialPos.compareTo(0L) < 0)
            return false;

        Cab cab = cabService.findById(cabId);
        System.out.println("cabSignsIn called with cabId =" + cabId + " initial position = " + initialPos);
        
        // If cab is in signed-out state, then only sign-In is attempted
        if (cab != null && cab.getMajorState().equals("signed-out")) {
            // cab major state is changed to "signed-in", minor state is changed to "available"
            // & cab position is set to initialPosition
            cab.setMajorState("signed-in").setMinorState("available").setInitialPos(initialPos);
            cabService.save(cab);
            return true;
        }
        return false;
    }

    @GetMapping("cabSignsOut")
    // This method is called to sign-out a cab from cabService
    // It returns true if sign-out is successful
    public boolean cabSignsOut(Long cabId) {
        Cab cab = cabService.findById(cabId);
        System.out.println("cabSignsOut called with cabId =" + cabId);
        
        // If cab is in signed-in and available state, then only sign-Out is attempted
        if (cab != null && cab.getMajorState().equals("signed-in") && cab.getMinorState().equals("available")) {
            // major state is changed to "signed-out", numberRides set to 0 and other fields are reset
            cab.setMajorState("signed-out").setMinorState(null).setInitialPos(null).setDestinationLoc(null)
                    .setSourceLoc(null).setRideId(null).setLastRide(false).setNumberRides(0L);
            cabService.save(cab);
            return true;
        }
        return false;
    }

    @GetMapping("requestRide")
    // This method is called by a customer to request a ride
    // Its inputs are customer Id, source location, destination location
    // It returns -1 if request fails
    // It returns rideID, cabId and fare if request succeeds
    public String requestRide(Long custId, Long sourceLoc, Long destinationLoc) {
        // Checking that source location and destination location are non-negative
        if (sourceLoc.compareTo(0L) < 0 || destinationLoc.compareTo(0L) < 0)
            return "-1";
        
        /* This code converts the list of all cabs and convers it into a stream and
           then filters the available cabs and sorts them based on the distance from the sourceLoc
           and returns the closest 3 cabs */
        List<Cab> cabsAll = cabService.findAll();
        Stream<Cab> cabsStreams = cabsAll.stream()
            .filter(cab -> cab.getMajorState().equals("signed-in") && cab.getMinorState().equals("available"))
            .sorted((cab1, cab2) -> new Long(Math.abs(cab1.getInitialPos() - sourceLoc))
                    .compareTo(new Long(Math.abs(cab2.getInitialPos() - sourceLoc))))
            .limit(3);
        List<Cab> cabs = cabsStreams.collect(Collectors.toList());

        WebClient client = WebClient.create();
        // The available cabs are requested in order of their distance form the source location
        for (Cab cab : cabs) {
            System.out.println("Cabid " + cab.getCabId() +
                             " minorState " + cab.getMinorState() +
                             " customer " + custId);

            // creating a new ride, setting source location, destination location and customer Id
            Ride ride = new Ride();
            ride.setCustId(custId)
                .setSourceLoc(sourceLoc)
                .setDestinationLoc(destinationLoc);
            // saving the ride in database
            rideService.save(ride);

            // send request to the particular cab to accept the ride
            String jsonResponse = client.get()
                    .uri("cab-service:8080/requestRide?cabId=" + cab.getCabId() + "&rideId=" + ride.getRideId()
                            + "&sourceLoc=" + sourceLoc + "&destinationLoc=" + destinationLoc)
                    .retrieve().bodyToMono(String.class).block();
            
            if (Boolean.parseBoolean(jsonResponse)) {
                // Cab accepted ride-request
                System.out.println("requestRide: Computing fare: cabLoc = " + cab.getInitialPos() + " sourceLoc = " + sourceLoc +
                " dest Loc = " + destinationLoc);
                Long fare = (Math.abs(cab.getInitialPos() - sourceLoc) + Math.abs(sourceLoc - destinationLoc)) * 10;
                System.out.println("Fare computed = " + fare);

                // send request to walletService to deduct the fare from the customer account
                String walletJsonResponse = client.get()
                        .uri("wallet-service:8082/deductAmount?custId=" + custId + "&amount=" + fare).retrieve()
                        .bodyToMono(String.class).block();

                // Check response of walletService.deductAmount
                if (Boolean.parseBoolean(walletJsonResponse)) {
                    // Wallet deduction successful, intimate the cab service to start the ride

                    String cabJsonResponse = client.get()
                            .uri("cab-service:8080/rideStarted?cabId=" + cab.getCabId() + "&rideId=" + ride.getRideId())
                            .retrieve().bodyToMono(String.class).block();

                    // check response of cabService.rideStarted.
                    if (Boolean.parseBoolean(cabJsonResponse)) {
                        // since the ride has started, change the states of the local cab to giving-ride
                        System.out.println(cab.getMinorState()
                                + " This current minor state being changed to giving-ride  by customer" + custId);

                        // update cab Id in ride database
                        ride.setSourceLoc(sourceLoc).setDestinationLoc(destinationLoc).setCustId(custId)
                                .setStatus("ongoing").setCabId(cab.getCabId());
                        rideService.save(ride);

                        // update cab state to giving-ride, source loc, dest loc, increment number of rides and 
                        // set lastRide to true
                        cab.setMinorState("giving-ride")
                            .setRideId(ride.getRideId())
                            .setLastRide(true)
                            .setNumberRides(cab.getNumberRides() + 1)
                            .setSourceLoc(sourceLoc)
                            .setDestinationLoc(destinationLoc);
                        cabService.save(cab);

                        return String.format(ride.getRideId() + " " + cab.getCabId() + " " + fare);
                    }
                }
                // If wallet transaction fails or cal to cabService.rideStarted fails then ride is cancelled
                client.get()
                        .uri("cab-service:8080/rideCancelled?cabId=" + cab.getCabId() + "&rideId=" + ride.getRideId())
                        .retrieve().bodyToMono(String.class).block();
                
                // setting lastRide = true since the cab was interested in giving the ride
                cab.setLastRide(true);
            } else {
                // Cab was available, but did not accept ride as cab refuses alternate rides
                cab.setLastRide(false);
            }
            // If unsuccessful, then delete the ride.
            rideService.delete(ride);
            cabService.save(cab);
        }
        // rideService.delete(ride);
        return "-1";
    }

    @GetMapping("getCabStatus")
    // This method returns the cab status
    // If cab is in available state, then it returns "available and cab position"
    // If cab is in giving-ride/committed state, then it returns "giving-ride/committed
    //, cab position, customer Id, destination location"
    public String getCabStatus(Long cabId) {
        Cab cab = cabService.findById(cabId);

        if (cab != null && cab.getMajorState().equals("signed-in")) {
            String minorState = cab.getMinorState();
            if (minorState.equals("available")) {
                return String.format("%s %s", minorState, cab.getInitialPos());
            }
            Ride ride = rideService.findById(cab.getRideId());
            // if cab is in committed state, take lastPos as cab's initialPos
            String lastPos = cab.getInitialPos().toString();
            if (minorState.equals("giving-ride")) {
                // If cab is in giving-ride state, take lastPos as sourLoc
                lastPos = cab.getSourceLoc().toString();
            }
            // cab is either either committed or giving-ride state
            System.out.println("Minor state " + minorState);
            System.out.println("lastPos: " + lastPos);
            System.out.println("ride: " + ride);
            if (ride != null)
                System.out.println("ride.custid: " + ride.getCustId());
            System.out.println("cab.destinationLoc: " + cab.getDestinationLoc());
            
            return String.format("%s %s %s %s", minorState, lastPos, ride.getCustId().toString(),
                    cab.getDestinationLoc().toString());
        }
        return "signed-out -1";
    }


    @GetMapping("reset")
    // This method resets the cab database in cabService and rideService
    // First rides are ended for all the cabs in giving-ride state
    // Then cabs are signed-out
    public void reset() {
        WebClient client = WebClient.create();

        List<Cab> cabs = cabService.findMinorStateGivingRide();
        for (Cab cab : cabs) {
            Ride ride = rideService.findById(cab.getRideId());
            client.get().uri("cab-service:8080/customRideEnded?cabId=" + cab.getCabId() + "&rideId=" + ride.getRideId())
                    .retrieve().bodyToMono(String.class).block();
            rideEnded(ride.getRideId());
        }

        cabs = cabService.findMajorStateSignedIn();
        for (Cab cab : cabs) {
            client.get().uri("cab-service:8080/customSignOut?cabId=" + cab.getCabId()).retrieve().bodyToMono(String.class)
                    .block();
            cabSignsOut(cab.getCabId());
        }
    }
}
