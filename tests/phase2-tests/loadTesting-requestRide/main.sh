curl -s "http://localhost:8081/reset"
curl -s "http://localhost:8082/reset"

# Testing total number of rides given by a cab
# Customer 201 requests ride continously.
signInStatus=$(curl -s "http://localhost:8080/signIn?cabId=101&initialPos=0")

if $signInStatus = "true"
then
    echo "cab 101 signed in"
fi


bash sh1.sh &
bash sh1.sh &
bash sh1.sh &
bash sh1.sh &
bash sh1.sh &
bash sh1.sh &
bash sh1.sh &
bash sh1.sh &
bash sh1.sh &
bash sh1.sh &
bash sh1.sh &
bash sh1.sh &
bash sh1.sh &
bash sh1.sh &
bash sh1.sh 

wait

cabStatus=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")

echo "$cabStatus"

numRides=$(curl -s "http://localhost:8080/numRides?cabId=101")

if [ "$numRides" = "45" ]
then
    echo "Correct number of rides"
else
    echo "Wrong number of rides"
fi
