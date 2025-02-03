Building
-------------
In lib folder put built jar 'commons-collections4-4.5.0-M4-SNAPSHOT.jar' if it not exists

run
`mvn clean install`

Run test from console
-------------
1. `mvn clean package`
2. For example:
java -jar .\target\benchmark.jar PatriciaBenchmarkAddExistingValue -i 5 -wi 7
