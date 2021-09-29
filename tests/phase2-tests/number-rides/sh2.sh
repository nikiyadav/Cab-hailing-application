# cust 201 requests ride

rideReqStatus="-1"

while [ "$rideReqStatus" = "-1" ]
do
    rideReqStatus=$(curl -s "http://localhost:8081/requestRide?custId=201&sourceLoc=10&destinationLoc=100")
done

echo ${rideReqStatus}

rideId=$(echo $rideReqStatus | awk '{print $1;}')

rideEndResp=$(curl -s "http://localhost:8080/rideEnded?cabId=101&rideId=${rideId}")

echo "$rideId ended with response $rideEndResp"


