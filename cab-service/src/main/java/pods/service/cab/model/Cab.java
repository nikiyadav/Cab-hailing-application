package pods.service.cab.model;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
// We are using method chaining so that properties can be set in a chained way
// Furthermore all the required get and set methods are being added by lombok library
@Accessors(chain = true)
@Getter
@Setter
@Entity
public class Cab {
    @Id
    private Long cabId;

    @NotNull
    // Can be either signed-in/signed-out
    private String majorState = "signed-out";
    // Can be either available/committed/giving-ride
    private String minorState = null;

    // number of rides given by cab since last sign-In
    private Long numberRides = 0L;

    // Whether last request was fulfilled
    // lastRide = true indicate that cab accepted the last ride request
    // lastRide = false indicate that cab declined the last ride request
    // cab accepts every alternate request
    private Boolean lastRide = false;

    // If giving-ride, then rideId of current ride
    private Long rideId = null;

    // Cab position
    private Long initialPos = null;

    // If giving-ride, then source location of current ride
    private Long sourceLoc = null;

    // If giving-ride, then destination location of current ride
    private Long destinationLoc = null;

    public Cab (Long id) {
        this.cabId = id;
    }

    public Cab() {
        super();
    }
}
