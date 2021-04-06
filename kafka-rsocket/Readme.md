### An example of how to use rsocket to connect kafka to the browser

#### How to build
* Install node and browserify, if you have nix you can use the provided shell.nix
* from the js dir type npm install to install all dependencies
* from the same dir run `npm run build` (yes it is awkward I know) this build the app.js in the resources/public dir
* run the spring boot app with `gradle  :kafka-rsocket:bootRun`
* Install kafka locally and create the topic events

### How to run
Open a browser at http://localhost:8080
You will see.