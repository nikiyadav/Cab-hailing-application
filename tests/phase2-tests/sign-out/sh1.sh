# cust 201 requests ride


signOutStatus=$(curl -s "http://localhost:8080/signOut?cabId=101")

echo ${signOutStatus}