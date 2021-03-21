### An example of reactive feature flag spring boot starter 

* configdb-starter a starter that ease the consumption of config db.
* features-starter a starter for reactive feature service built on top of configdb-starter
* features-example an example of reactive spring boot main that uses feature flags.

How to run

- with gradle: 
```bash
gradle clean :features-example:bootRun`
```
- with maven:
```bash
mvn clean spring-boot:run -pl features-example
```