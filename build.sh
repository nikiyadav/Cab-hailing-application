cd ./cab-service/
mvn clean package -DskipTests
cd ..

cd ./wallet-service/
mvn clean package -DskipTests
cd ..

cd ./ride-service
mvn clean package -DskipTests
cd ..

