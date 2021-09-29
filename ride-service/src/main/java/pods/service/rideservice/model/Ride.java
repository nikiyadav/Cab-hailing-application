package pods.service.rideservice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(chain = true)
@Getter
@Setter
@Entity
public class Ride {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    // Id given to the ride, it is auto generated
    Long rideId;

    // Id of the customer taking this ride
    Long custId = null;

    // Id of the cab giving this ride
    Long cabId = null;

    // source location of the ride
    Long sourceLoc = null;

    // destination location of the ride
    Long destinationLoc = null;

    // ride status can be ongoing or completed
    String  status = null; // completed or ongoing

    public Ride (Long rideId) {
        this.rideId = rideId;
    }

    public Ride() {
        super();
    }

}
