curl -s "http://localhost:8081/reset"
curl -s "http://localhost:8082/reset"

# Two concurrent rides are requested by customer 201
# Only one cab is signed-in
# Testing that balance is correct 
signInStatus=$(curl -s "http://localhost:8080/signIn?cabId=101&initialPos=0")

if $signInStatus = "true"
then
    echo "cab 101 signed in"
fi


bash sh1.sh & bash sh2.sh

sleep 1

cabStatus=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")

echo "$cabStatus"

if [ "${cabStatus}" = "available 100" ]
then
    echo "Cab status is correct"
else
    echo "Cab status is not correct"
fi


balance=$(curl -s "http://localhost:8082/getBalance?custId=201")

if [ "$balance" = "7200" ]
then
    echo "Correct balance status"
else
    echo "Not Correct"
fi
