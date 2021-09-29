#! /bin/sh
# every test case should begin with these two steps
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

rm cust.out

# Run two test scripts in parallel
sh sh1.sh &
sh sh2.sh
# sh1 creates the output file sh1out, which contains fares
# of all rides given in sh1. Similarly, sh2out.

wait

totalFare=0
for i in $(cat sh1out sh2out);
do
    totalFare=$(expr $totalFare + $i)
done
# totalFare contains the sum cost of all rides

# Now check if the current total balance
# in all wallets is equal to 
# original total balance in all wallets (which is a constant)
# MINUS totalFare.
# Print “Test Passing Status: yes” if yes,
# else print “Test Passing Status: no”.

customer=$(cat cust.out)

if [ "$customer" = "201" ]
then
    if [ "$totalFare" = "1000" ]
    then
        echo "Correct result cust 201"
    fi
else
    if [ "$totalFare" = "1200" ]
    then
        echo "Correct result cust 202"
    fi
fi
