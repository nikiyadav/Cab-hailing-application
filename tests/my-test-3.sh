#! /bin/bash

source ./assert.sh

# A basic test workflow to sign into cab, get status information, and check that
# request ride fails when customer wallet balance is lower than the fare.

# test will take three arguments.
# 1. url 2. url return value 3. Message to be printed
curl -s http://localhost:8081/reset

curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

cabServiceUri="http://localhost:8080/"
rideServiceUri="http://localhost:8081/"
walletServiceUri="http://localhost:8082/"
counter=1

# Cab 101 sign in
msg="Cab 101 sign in"
# echo "Test " $((counter++))  ": " $msg
assert "${cabServiceUri}signIn?cabId=101&initialPos=0" "true" "cab 101 signed in"

msg="cab 101 status"
# echo "Test " $((counter++)) ": " $msg
assert "${rideServiceUri}getCabStatus?cabId=101" "available 0" "$msg"

msg="Check wallet balance of customer 202"
# echo "Test " $((counter++)) ": " $msg
assert "${walletServiceUri}getBalance?custId=202" "10000" "$msg"
amount=$resp

msg="Request ride by customer 202, from sourceLoc 10. Ride request failed."
# echo "Test  " $((counter++)) ": " $msg
assert! "${rideServiceUri}requestRide?custId=202&sourceLoc=10&destinationLoc=60" "-1" "$msg"

msg="Check wallet balance of customer 202"
# echo "Test " $((counter++)) ": " $msg
assert "${walletServiceUri}getBalance?custId=202" "9400" "$msg"
amount=$resp

msg="cab 101 status"
# echo "Test  " $((counter++)) ": " $msg
assert "${rideServiceUri}getCabStatus?cabId=101" "giving-ride 10 202 60" "$msg"
