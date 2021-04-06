// https://stremler.io/2020-05-31-rsocket-messaging-with-spring-boot-and-rsocket-js/

const {
  RSocketClient,
  JsonSerializer,
  IdentitySerializer
} = require('rsocket-core');
const RSocketWebSocketClient = require('rsocket-websocket-client').default;
var client = undefined;

function addErrorMessage(prefix, error) {
  var ul = document.getElementById("messages");
  var li = document.createElement("li");
  li.appendChild(document.createTextNode(prefix + error));
  ul.appendChild(li);
}

function reloadMessages(message) {
  var ul = document.getElementById("messages");
  var all_li = ul.getElementsByTagName("li");

  for (let i = 0; i < all_li.length; i++) {
    const li = all_li[i];
    if (li.innerText.includes(message['id']))
      return;
  }

  var li = document.createElement("li");
  li.appendChild(document.createTextNode(JSON.stringify(message)));
  ul.appendChild(li);
}

function main() {
  if (client !== undefined) {
    client.close();
    document.getElementById("messages").innerHTML = "";
  }

  // Create an instance of a client
  client = new RSocketClient({
    serializers: {
      data: JsonSerializer,
      metadata: IdentitySerializer
    },
    setup: {
      // ms btw sending keepalive to server
      keepAlive: 60000,
      // ms timeout if no keepalive response
      lifetime: 180000,
      // format of `data`
      dataMimeType: 'application/json',
      // format of `metadata`
      metadataMimeType: 'message/x.rsocket.routing.v0',
    },
    transport: new RSocketWebSocketClient({
      url: 'ws://localhost:8080/rsocket'
    }),
  });

  // Open the connection
  client.connect().subscribe({
    onComplete: socket => {
      document.getElementById("request-response").onclick = requestResponse(socket);
      document.getElementById("request-response").disabled = false;

      document.getElementById("fire-and-forget").onclick = fireAndForgot(socket);
      document.getElementById("fire-and-forget").disabled = false;

      document.getElementById("stream").onclick = stream(socket);
      document.getElementById("stream").disabled = false;

      document.getElementById("kafka-subscribe").onclick = kafkaSubscribe(socket);
      document.getElementById("kafka-subscribe").disabled = false;

      document.getElementById("publish").onclick = publish(socket);
      document.getElementById("publish").disabled = false;
    },
    onError: error => {
      console.log(error);
      addErrorMessage("Connection has been refused due to ", error);
    },
    onSubscribe: cancel => {
      /* call cancel() to abort */
    }
  });
}

function requestResponse(socket){
   return function(){
        console.info("requestResponse", socket)
        socket.requestResponse({
            data: {"payload" : "request"},
            metadata: String.fromCharCode('request-response'.length) + 'request-response'
        }).subscribe({
            onComplete: msg => {
                console.log('Message received: ', msg.data);
                document.getElementById('request-reply').innerHTML = JSON.stringify(msg.data);
            },
            onError: error => {
               console.log('error received: ', error);
            }
        });
   }
}

function fireAndForgot(socket){
    return function(){
        console.info("fireAndForgot")
        socket.fireAndForget({
            data: {"payload" : "request"},
            metadata: String.fromCharCode('fire-and-forget'.length) + 'fire-and-forget'
        });
    }
}

function stream(socket){
    return function(){
            console.info("stream")
            var requestStreamSubscription = null;
            socket.requestStream({
               data: {"payload" : "stream id"},
                metadata: String.fromCharCode('stream'.length) + 'stream'
            }).subscribe({
                onSubscribe: sub => {
                    requestStreamSubscription = sub;
                    requestStreamSubscription.request(1);
                     document.getElementById("stream").onclick = function(){
                         requestStreamSubscription.cancel();
                         document.getElementById("stream").innerHTML = "stream";
                         document.getElementById("stream").onclick = stream(socket);
                     };
                     document.getElementById("stream").innerHTML = "unsubscribe";
                },
                onError: error => {
                    console.error("onError", error)
                },
                onNext: msg => {
                   console.info("onNext", msg)
                   document.getElementById('current-stream-element').innerHTML = JSON.stringify(msg.data);
                   requestStreamSubscription.request(1)
                },
                onComplete: msg => {
                    console.info("onComplete", msg)
                },
            });
    }
}
function kafkaSubscribe(socket){
    return function(){
            var requestSize = parseInt(document.getElementById("request-size").value, 10);
            console.info("kafkaSubscribe", requestSize)
            var remains = requestSize;
            var requestStreamSubscription = null;
            socket.requestStream({
               data: {"group" : "my user id"},
                metadata: String.fromCharCode('subscribe'.length) + 'subscribe'
            }).subscribe({
                onSubscribe: sub => {
                     requestStreamSubscription = sub;
                     requestStreamSubscription.request(requestSize);
                     remains = requestSize;
                     document.getElementById("kafka-subscribe").onclick = function(){
                         requestStreamSubscription.cancel();
                         document.getElementById("kafka-subscribe").innerHTML = "kafka-subscribe";
                         document.getElementById("kafka-subscribe").onclick = kafkaSubscribe(socket);
                     };
                     document.getElementById("kafka-subscribe").innerHTML = "unsubscribe";
                },
                onError: error => {
                    console.error("onError", error)
                },
                onNext: msg => {
                   console.info("onNext", msg)
                   document.getElementById('current-kafka-event').innerHTML = JSON.stringify(msg.data);
                   remains = remains - 1;
                   if(remains == 0){
                      requestStreamSubscription.request(requestSize)
                      remains = requestSize;
                   }
                },
                onComplete: msg => {
                    console.info("onComplete", msg)
                },
            });
    }
}


function publish(socket){
   return function(){
        document.getElementById("publish").onclick = null;
        document.getElementById("publish").disabled = true;
        var events = document.getElementById("events").value;
        console.info("publish", events)
        socket.requestResponse({
            data: events,
            metadata: String.fromCharCode('publish'.length) + 'publish'
        }).subscribe({
            onComplete: msg => {
                console.log('Message received: ', msg.data);
                document.getElementById("publish").onclick = publish(socket);
                document.getElementById("publish").disabled = false;
            },
            onError: error => {
               console.log('error received: ', error);
            }
        });
   }
}


document.addEventListener('DOMContentLoaded', main);
