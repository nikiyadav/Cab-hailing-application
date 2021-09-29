#! /bin/bash

# A basic test workflow to sign into cab, get status information, request ride
# check wallet functionality and repeat a ride to see if request is fulfilled.

source ./assert.sh

curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

counter=1

# Cab 101 sign in
msg="Cab 101 sign in"
echo "Test " $((counter++))  ": " $msg
assert "${cabServiceUri}signIn?cabId=101&initialPos=0" "true" "cab 101 signed in"

# Cab 102 sign in
msg="Cab 102 sign in"
echo "Test " $((counter++))  ": " $msg
assert "${cabServiceUri}signIn?cabId=102&initialPos=100" "true" "cab 102 signed in"


msg="cab 101 status"
echo "Test  " $((counter++)) ": " $msg
assert "${rideServiceUri}getCabStatus?cabId=101" "available 0" "$msg"

msg="Cab 102 status"
echo "Test  " $((counter++)) ": " $msg
assert "${rideServiceUri}getCabStatus?cabId=102" "available 100" "$msg"

msg="Check wallet balance of customer 202"
echo "Test  " $((counter++)) ": " $msg
assert "${walletServiceUri}getBalance?custId=202" "10000" "$msg"
amount=$resp

msg="Request ride by customer 202, from sourceLoc 10"
echo "Test  " $((counter++)) ": " $msg
assert! "${rideServiceUri}requestRide?custId=202&sourceLoc=10&destinationLoc=20" "-1" "$msg"

msg="The cab alloted should be 101, so check status"
echo "Test  " $((counter++)) ": " $msg
assert "${rideServiceUri}getCabStatus?cabId=101" "giving-ride 10 202 20" "$msg"

msg="Check wallet balance of customer 202"
echo "Test  " $((counter++)) ": " $msg
assert "${walletServiceUri}getBalance?custId=202" "9800" "$msg"



msg="Checking number of rides for 101"
echo "Test  " $((counter++)) ": " $msg
assert "${cabServiceUri}numRides?cabId=101" "1" "$msg"
rideId201=$resp


msg="Request ride by customer 201, from sourceLoc 40"
echo "Test  " $((counter++)) ": " $msg
assert! "${rideServiceUri}requestRide?custId=201&sourceLoc=40&destinationLoc=100" "-1" "$msg"
rideId201=$(echo $resp | awk '{print $1;}')


msg="The cab alloted should be 102 as 101 already took one ride"
echo "Test  " $((counter++)) ": " $msg
assert "${rideServiceUri}getCabStatus?cabId=102" "giving-ride 40 201 100" "$msg"

msg="Check cab 101 takes alternate requests"
echo "Test  " $((counter++)) ": " $msg
assert "${rideServiceUri}requestRide?custId=201&sourceLoc=40&destinationLoc=100" "-1" "$msg"



msg="End ride for cab 102"
echo "Test  " $((counter++)) ": " $msg
assert "${cabServiceUri}/rideEnded?cabId=102&rideId=${rideId201}" "true" "$msg"

msg="The cab alloted should be 102 as 101 lastRide happened status: "
echo "Test  " $((counter++)) ": " $msg
assert "${rideServiceUri}getCabStatus?cabId=102" "available 100" "$msg"


msg="cab 101 sign out not possible as cab is giving ride to cust 202"
echo "Test " $((counter++)) ": " $msg
assert "${cabServiceUri}signOut?cabId=101" "false" "$msg"

msg="Cab 102 sign out"
echo "Test " $((counter++)) ": " $msg
assert "${cabServiceUri}signOut?cabId=102" "true" "$msg"



