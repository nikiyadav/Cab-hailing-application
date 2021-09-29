
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

        echo -e  "${GREEN}$3${NC} ${resp}"
    else
        echo -e  "${RED}failed${NC}"
        testPassed="no"
    fi
}
assert! ()
{
    resp="$(curl -s ${1})"
    if [ "$resp" != "$2" ]
    then
        echo -e  "${GREEN}$3${NC}"
    else
        echo -e  "${RED}failed${NC}"
        testPassed="no"
    fi
}



cabServiceUri="http://localhost:8080/"
rideServiceUri="http://localhost:8081/"
walletServiceUri="http://localhost:8082/"
