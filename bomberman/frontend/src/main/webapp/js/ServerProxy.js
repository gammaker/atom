ServerProxy = Class.extend({

    host: window.location.host,
    url: window.location.host,

    socket: null,

    handler: {},

    init: function() {
        this.handler['Replica'] = gMessages.handleReplica;
        this.handler['Possess'] = gMessages.handlePossess;

        var self = this;
        gInputEngine.subscribe('up', function() {
            self.socket.send('MU')
        });
        gInputEngine.subscribe('down', function() {
            self.socket.send('MD')
        });
        gInputEngine.subscribe('left', function() {
            self.socket.send('ML')
        });
        gInputEngine.subscribe('right', function() {
            self.socket.send('MR')
        });
        gInputEngine.subscribe('bomb', function() {
            self.socket.send('PB')
        });

        this.initSocket();
    },

    initSocket: function() {
        var self = this;
        this.socket = new WebSocket("ws://" + this.url + "/events");

        this.socket.onopen = function() {
            console.log("Connection established.");
            self.socket.send("Token " + Utils.getParameterByName("token"));
            self.socket.heartbeat = setInterval(function() {
                self.socket.send("HB");
            }, 10000);
        };

        this.socket.onclose = function(event) {
            clearInterval(self.socket.heartbeat);
            if (event.wasClean) {
                console.log('closed');
            } else {
                console.log('alert close');
            }
            console.log('Code: ' + event.code + ' cause: ' + event.reason);
        };

        this.socket.onmessage = function(event) {
            //split message in format topic(params)
            var bracketPos = event.data.indexOf('(');
            var topic = event.data.substr(0, bracketPos);
            var params = event.data.substr(bracketPos + 1, event.data.length - bracketPos - 2);
            if (self.handler[topic] === undefined)
                return;

            self.handler[topic](params);
        };

        this.socket.onerror = function(error) {
            console.log("Error " + error.message);
        };
    }

});
