[![Build Status](https://www.travis-ci.com/barakb/starters.svg?branch=master)](https://www.travis-ci.com/barakb/starters)
### An example of reactive feature flag spring boot starter 

* configdb-starter a starter that ease the consumption of config db.
* features-starter a starter for reactive feature service built on top of configdb-starter
* features-example an example of reactive spring boot main that uses feature flags.

How to run


```bash
gradle clean :features-example:bootRun
```

https://reflectoring.io/spring-boot-dev-tools/


### An example of kafka spring-boot web and back using rsocket
* kafka-rsocket 

#### How to build
Based on https://stremler.io/2020-05-31-rsocket-messaging-with-spring-boot-and-rsocket-js/
* Install node and browserify, if you have nix you can use the provided shell.nix
* from the js dir type npm install to install all dependencies
* from the same dir run `npm run build` (yes it is awkward I know) this build the app.js in the resources/public dir
* run the spring boot app with `gradle  :kafka-rsocket:bootRun`
* Install kafka locally and create the topic events

#### How to run
Open a browser at http://localhost:8080
You will see.


### An example of rsocket and Avro - not ready yet.
* rsocket-avro
#### How to run 
`gradle  :rsocket-avro:bootRun`

