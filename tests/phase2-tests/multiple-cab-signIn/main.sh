curl -s "http://localhost:8081/reset"
curl -s "http://localhost:8082/reset"

# Testing sign-in same cabs from multiple scripts concurrently.
# Only one sign-in should happen

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

counter=0
cab101Status=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
cab102Status=$(curl -s "http://localhost:8081/getCabStatus?cabId=102")
cab103Status=$(curl -s "http://localhost:8081/getCabStatus?cabId=103")
cab104Status=$(curl -s "http://localhost:8081/getCabStatus?cabId=104")
cab105Status=$(curl -s "http://localhost:8081/getCabStatus?cabId=105")
cab106Status=$(curl -s "http://localhost:8081/getCabStatus?cabId=106")
cab107Status=$(curl -s "http://localhost:8081/getCabStatus?cabId=107")
cab108Status=$(curl -s "http://localhost:8081/getCabStatus?cabId=108")

# echo "$cab101Status"
# echo "$cab102Status"
# echo "$cab103Status"
# echo "$cab104Status"
# echo "$cab105Status"
# echo "$cab106Status"
# echo "$cab107Status"
# echo "$cab108Status"

if [ "${cab101Status}" = "available 0" ]
then
    echo "Cab 101 status is correct - ${cab101Status}"
    counter=$((counter+1))
else
    echo "Cab 101 status is not correct - ${cab101Status}"
fi

if [ "${cab102Status}" = "available 10" ]
then
    echo "Cab 102 status is correct - ${cab102Status}"
    counter=$((counter+1))
else
    echo "Cab 102 status is not correct - ${cab102Status}"
fi

if [ "${cab103Status}" = "available 20" ]
then
    echo "Cab 103 status is correct - ${cab103Status}"
    counter=$((counter+1))
else
    echo "Cab 103 status is not correct - ${cab103Status}"
fi

if [ "${cab104Status}" = "available 30" ]
then
    echo "Cab 104 status is correct - ${cab104Status}"
    counter=$((counter+1))
else
    echo "Cab 104 status is not correct - ${cab104Status}"
fi

if [ "${cab105Status}" = "available 40" ]
then
    echo "Cab 105 status is correct - ${cab105Status}"
    counter=$((counter+1))
else
    echo "Cab 105 status is not correct - ${cab105Status}"
fi

if [ "${cab106Status}" = "signed-out -1" ]
then
    echo "Cab 106 status is correct - ${cab106Status}"
else
    echo "Cab 106 status is not correct - ${cab106Status}"
fi

if [ "${cab107Status}" = "signed-out -1" ]
then
    echo "Cab 107 status is correct - ${cab107Status}"
else
    echo "Cab 107 status is not correct - ${cab107Status}"
fi

if [ "${cab108Status}" = "signed-out -1" ]
then
    echo "Cab 108 status is correct - ${cab108Status}"
else
    echo "Cab 108 status is not correct - ${cab108Status}"
fi

echo $counter
if [ $counter == 5 ] 
then 
    echo "Test passed: Correct number of signed cabs"
else 
    echo "Test failed: Incorrect number of signed cabs"
fi