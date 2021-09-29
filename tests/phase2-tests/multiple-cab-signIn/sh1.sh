signInStatus=$(curl -s "http://localhost:8080/signIn?cabId=101&initialPos=0")

if [ "$signInStatus" = "true" ]
then
    echo "cab 101 signed in"
fi

signInStatus=$(curl -s "http://localhost:8080/signIn?cabId=102&initialPos=10")

if [ "$signInStatus" = "true" ]
then
    echo "cab 102 signed in"
fi

signInStatus=$(curl -s "http://localhost:8080/signIn?cabId=103&initialPos=20")

if [ "$signInStatus" = "true" ]
then
    echo "cab 103 signed in"
fi

signInStatus=$(curl -s "http://localhost:8080/signIn?cabId=104&initialPos=30")

if [ "$signInStatus" = "true" ]
then
    echo "cab 104 signed in"
fi

signInStatus=$(curl -s "http://localhost:8080/signIn?cabId=105&initialPos=40")

if [ "$signInStatus" = "true" ]
then
    echo "cab 105 signed in"
fi



