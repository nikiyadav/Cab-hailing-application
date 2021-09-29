curl -s "http://localhost:8081/reset"
curl -s "http://localhost:8082/reset"


# sign in from main and multiple sign outs requests
# Testing that only one sign-out request is run successfully

signInStatus=$(curl -s "http://localhost:8080/signIn?cabId=101&initialPos=0")

if $signInStatus = "true"
then
    echo "cab 101 signed in"
fi

# parallel four signouts. Only one should succeed,  and three requests should fail
bash sh1.sh & bash sh1.sh & bash sh1.sh & bash sh1.sh

sleep 1

cabStatus=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")

echo "$cabStatus"

if [ "${cabStatus}" = "signed-out -1" ]
then
    echo "Cab status is correct"
else
    echo "Cab status is not correct"
fi
