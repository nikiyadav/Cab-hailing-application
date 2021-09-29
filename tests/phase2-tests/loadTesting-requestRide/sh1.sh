


echo "pid = "$$
rideReqStatus="-1"
cabLoc=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
cabLoc=$(echo "$cabLoc" | awk '{print $2}')
echo "pid = "$$ " loop 1 running.. cabLoc = "$cabLoc

while [ "$rideReqStatus" = "-1" ]
do
    cabLoc=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
    cabLoc=$(echo "$cabLoc" | awk '{print $2}')
    echo "$ calling rideRequest with source loc/cabloc = $cabLoc"
    rideReqStatus=$(curl -s \
                   "http://localhost:8081/requestRide?custId=201&sourceLoc=${cabLoc}&destinationLoc=10")
done

echo "pid = $$,ride status ${rideReqStatus}"

rideId=$(echo $rideReqStatus | awk '{print $1;}')

rideEndResp=$(curl -s "http://localhost:8080/rideEnded?cabId=101&rideId=${rideId}")

echo "pid = $$, $rideId ended with response $rideEndResp"
cabLoc=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
echo "pid = "$$ " loop 1 ended.. cabLoc = "$cabLoc


# ___________________________________________________________________________

rideReqStatus="-1"

cabLoc=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
cabLoc=$(echo "$cabLoc" | awk '{print $2}')
echo "pid = "$$ " loop 2 running.. cabLoc = "$cabLoc

while [ "$rideReqStatus" = "-1" ]
do
    cabLoc=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
    cabLoc=$(echo "$cabLoc" | awk '{print $2}')
    rideReqStatus=$(curl -s \
                         "http://localhost:8081/requestRide?custId=201&sourceLoc=${cabLoc}&destinationLoc=10")
done

echo "pid = $$,ride status ${rideReqStatus}"

rideId=$(echo $rideReqStatus | awk '{print $1;}')

rideEndResp=$(curl -s "http://localhost:8080/rideEnded?cabId=101&rideId=${rideId}")

echo "pid = $$, $rideId ended with response $rideEndResp"
cabLoc=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
echo "pid = "$$ " loop 2 ended.. cabLoc = "$cabLoc
#______________________________________________________________________________



cabLoc=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
cabLoc=$(echo "$cabLoc" | awk '{print $2}')
echo "pid = "$$ " loop 3 running.. cabLoc = "$cabLoc

rideReqStatus="-1"

while [ "$rideReqStatus" = "-1" ]
do
    cabLoc=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
    cabLoc=$(echo "$cabLoc" | awk '{print $2}')
    rideReqStatus=$(curl -s \
                    "http://localhost:8081/requestRide?custId=201&sourceLoc=${cabLoc}&destinationLoc=10")
done

echo "pid = $$, ride status ${rideReqStatus}"

rideId=$(echo $rideReqStatus | awk '{print $1;}')

rideEndResp=$(curl -s "http://localhost:8080/rideEnded?cabId=101&rideId=${rideId}")

echo "pid = $$, $rideId ended with response $rideEndResp"
cabLoc=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
echo "pid = "$$ " loop 3 ended.. cabLoc = "$cabLoc

# ______________________________________________________________________________
