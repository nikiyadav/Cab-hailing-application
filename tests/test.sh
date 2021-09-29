#! /bin/bash
# this test case checks whether a customer's request
# gets rejected if only one cab has signed in but it is busy.

# reset RideService and Wallet.
# every test case should begin with these two steps
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

RED='\033[0;31m'
NC='\033[0m'
GREEN='\u001b[32m'


# test will take three arguments.
# 1. url 2. url return value 3. Message to be printed
assert ()
{
    resp="$(curl -s ${1})"
    if [ "$resp" = "$2" ]
    then
        echo -e "${GREEN}$3${NC}"
    else
        echo -e  "${RED}failed${NC}"
        testPassed="no"
    fi
}

cabServiceUri="http://localhost:8080/"
rideServiceUri="http://localhost:8081/"
walletServiceUri="http://localhost:8082/"
counter=1

testPassed="yes"

# Cab 101 sign in
msg="Cab 101 sign in"
echo "Test " $((counter++))  ": " $msg
assert "${cabServiceUri}signIn?cabId=101&initialPos=0" "true" "cab 101 signed in"

msg="cab 101 status"
echo "Test  " $((counter++)) ": " $msg
assert "${rideServiceUri}getCabStatus?cabId=101" "available 0" "$msg"

msg="cab 101 sign out"
echo "Test " $((counter++)) ": " $msg
assert "${cabServiceUri}signOut?cabId=101" "true" "$msg"


msg="customer 201 request ride"
echo "Test " $((counter++)) ": " $msg
assert "${rideServiceUri}requestRide?custId=201&sourceLoc=2&destinationLoc=10" "-1" "$msg"

msg="customer 202 request ride"
echo "Test " $((counter++)) ": " $msg
assert "${rideServiceUri}requestRide?custId=202&sourceLoc=1&destinationLoc=11" "-1" "$msg"

# # Test Wallet Service
msg="deduct wallet"
echo "Test " $((counter++)) ": " $msg
assert "${walletServiceUri}deductAmount?custId=201&amount=100" "true" "$msg"

echo "Test Passing  Status: ${testPassed}"


