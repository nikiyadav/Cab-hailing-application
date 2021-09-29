#! /bin/bash
# this test case checks whether a customer's ride request
# gets rejected if 3 cabs are requested but all decline the request.

source ./assert.sh

# reset RideService and Wallet.
# every test case should begin with these two steps
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

counter=1

# Cab 101 sign in
msg="Cab 101 sign in"
echo "Test " $((counter++))  ": " $msg
assert "${cabServiceUri}signIn?cabId=101&initialPos=0" "true" "cab 101 signed in"

msg="cab 101 status"
echo "Test  " $((counter++)) ": " $msg
assert "${rideServiceUri}getCabStatus?cabId=101" "available 0" "$msg"

# Cab 102 sign in
msg="Cab 102 sign in"
echo "Test " $((counter++))  ": " $msg
assert "${cabServiceUri}signIn?cabId=102&initialPos=0" "true" "cab 102 signed in"

msg="cab 102 status"
echo "Test  " $((counter++)) ": " $msg
assert "${rideServiceUri}getCabStatus?cabId=102" "available 0" "$msg"

# Cab 103 sign in
msg="Cab 103 sign in"
echo "Test " $((counter++))  ": " $msg
assert "${cabServiceUri}signIn?cabId=103&initialPos=0" "true" "cab 103 signed in"

msg="cab 103 status"
echo "Test  " $((counter++)) ": " $msg
assert "${rideServiceUri}getCabStatus?cabId=103" "available 0" "$msg"

# Cab 104 sign in
msg="Cab 104 sign in"
echo "Test " $((counter++))  ": " $msg
assert "${cabServiceUri}signIn?cabId=104&initialPos=0" "true" "$msg"

msg="cab 104 status"
echo "Test  " $((counter++)) ": " $msg
assert "${rideServiceUri}getCabStatus?cabId=104" "available 0" "$msg"

msg="customer 201 request ride"
echo "Test " $((counter++)) ": " $msg
assert! "${rideServiceUri}requestRide?custId=201&sourceLoc=2&destinationLoc=30" "-1" "$msg" #cab 101 serves this request
rideId1=$resp

msg="customer 202 request ride"
echo "Test " $((counter++)) ": " $msg
assert! "${rideServiceUri}requestRide?custId=202&sourceLoc=5&destinationLoc=20" "-1" "$msg" #cab 102 serves this request
rideId2=$resp

msg="customer 203 request ride"
echo "Test " $((counter++)) ": " $msg
assert! "${rideServiceUri}requestRide?custId=203&sourceLoc=3&destinationLoc=10" "-1" "$msg" 
rideId3=$resp


rideId1=$(echo $rideId1 | awk '{print $1;}')
msg="${rideId1} has ended"
echo "Test " $((counter++)) ": " $msg
assert "${cabServiceUri}rideEnded?cabId=101&rideId=${rideId1}" "true" "$msg"


rideId2=$(echo $rideId2 | awk '{print $1;}')
msg="${rideId2} has ended"
echo "Test " $((counter++)) ": " $msg
assert "${cabServiceUri}rideEnded?cabId=102&rideId=${rideId2}" "true" "$msg"

rideId3=$(echo $rideId3 | awk '{print $1;}')
msg="${rideId3} has ended"
echo "Test " $((counter++)) ": " $msg
assert "${cabServiceUri}rideEnded?cabId=103&rideId=${rideId3}" "true" "$msg"

msg="customer 201 request ride"
echo "Test " $((counter++)) ": " $msg
assert! "${rideServiceUri}requestRide?custId=201&sourceLoc=2&destinationLoc=40" "-1" "$msg"
#cab 104 serves this request

# Cabs 103,102,101 are requested in increasing order as their positions are 10,20,30 respectively.
# Their last-ride value changes to "false"
msg="customer 201 request ride"
echo "Test " $((counter++)) ": " $msg
assert "${rideServiceUri}requestRide?custId=201&sourceLoc=2&destinationLoc=10" "-1" "$msg"

