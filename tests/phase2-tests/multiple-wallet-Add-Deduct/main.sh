#! /bin/sh
# every test case should begin with these two steps
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

# 20 concurrent requests are sent to wallet, 10 request deduct 100 units
# 10 requests add 100 units
# Testing that final balance is equal to initial balance

initialBalance=$(curl -s "http://localhost:8082/getBalance?custId=201")

echo "Initial account balance:" $initialBalance

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
bash sh2.sh &
bash sh2.sh &
bash sh2.sh &
bash sh2.sh &
bash sh2.sh &
bash sh2.sh &
bash sh2.sh &
bash sh2.sh &
bash sh2.sh &
bash sh2.sh

wait

finalBalance=$(curl -s "http://localhost:8082/getBalance?custId=201")

echo "Final account balance: " $finalBalance

if [ "$initialBalance" = "$finalBalance" ]
then 
    echo "Test Passed: yes"
else
    echo "Test Passed: no"
fi