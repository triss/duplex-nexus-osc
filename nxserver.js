/* This server enables nexusUI communication
   over a node websocket using socket.io
   
   With node.js installed, 
   start the server with the command:
   node nxserver.js
*/

console.log(__dirname);

////////////////////////////////////////////////////////////////////////////////
// set up basic connectivity
////////////////////////////////////////////////////////////////////////////////

// web based stuff
var connect = require('connect'),
    http = require('http'),
    
    // set up serving of static pages from this folder
    app = connect().use(connect.static(__dirname)).listen(8080),

    // set up web service port
    io = require('socket.io').listen(app);
    
    console.log('http server on 8080');

// osc client
var osc = require('node-osc'),
    oscClient = new osc.Client('localhost', 57120);
    console.log('osc client on 57120');

// osc server
var oscServer = new osc.Server(4444, '0.0.0.0');
    console.log('osc server on 4444');
    

////////////////////////////////////////////////////////////////////////////////
// widget management and messaging to web clients is handled here
////////////////////////////////////////////////////////////////////////////////

// a collection to store info about our widgets - this cache is used to update 
// new clients upon connection
var widgets = {};

// creates a widget
function createWidget(name, type, x, y, w, h) {
    // update our cache of widget information
    widgets[name] = {
        'name': name,
        'type': type,
        'x': x, 'y': y,
        'w': w, 'h': h,
        'properties': {}
    };

    // update all web clients
    io.sockets.emit('create', widgets[name]);

    console.log('widget ' + name + ' created.')
}

// sets a widgets property with value
function updateWidget(name, property, value) {
    widgets[name]['properties'][property] = value;

    // update the web clients
    io.sockets.emit('update', name, property, value);

    console.log('widget ' + name + ' ' + property + ' set to ' + value);
}

// deletes a widget
function deleteWidget(name) {
    // update our cache of widget information
    delete widgets[name];

    // update all web clients
    io.sockets.emit('delete', name);

    console.log('widget ' + name + ' deleted.')
}

// send all of the information about widgets to client on socket
function sendClientAllWidgetInfo(socket) {
    for(var w in widgets) {
        // send creation message
        socket.emit('create', widgets[w]);

        // and widgets properties
        for(var p in widgets[w].properties) {
            socket.emit(
                'update', w, p, widgets[w]['properties'][p]
            );
        }
    }
}

////////////////////////////////////////////////////////////////////////////////
// handle incoming data from OSC
////////////////////////////////////////////////////////////////////////////////

oscServer.on('message', function(oscMsg, rinfo) {
    console.log(oscMsg);

    var oscPath = oscMsg[0],
        args = oscMsg.slice(1, oscMsg.length);

    switch(oscPath) {
        // creates/updates a new widget
        // e.g. /nexus/widget mySlider slider 0 0 100 100 
        case '/nexus/create':
            createWidget.apply(this, args);
            break;

        // deletes a widget
        // e.g. /nexus/delete mySlider
        case '/nexus/delete':
            deleteWidget(args[0]); 
            break;
        
        // update a widgets property
        // e.g. /nexus/update mySlider 0.5
        case '/nexus/update':
            updateWidget.apply(this, args);
            break;

        // this message allows the osc client being sent data about widget 
        // values to be changed
        case '/nexus/osc_client':
            oscClient.host = oscMsg[1]; 
            oscClient.port = oscMsg[2]; 

            console.log(
                'Now sending OSC data to: ' + oscMsg[1] + ':' + oscMsg[2]
            );
            break;
    }
})

////////////////////////////////////////////////////////////////////////////////
// handle incoming data from web page
////////////////////////////////////////////////////////////////////////////////

io.sockets.on('connection', function (socket) {
    // when a new client connects tell them about the existing widgets    
    sendClientAllWidgetInfo(socket);

    // when we see OSC data coming back from a widget send it to osc client
    // by default nexus widgets send back info to nx
    socket.on('nx', function(data) {
        oscClient.send(data.oscName, data.value);
       
        // work out which widget param is being set
        var splitOscName = data.oscName.split('/');
        
        // update our cache with that info
        widgets[splitOscName[1]].properties[splitOscName[2]] = data.value;

        // tell other clients about change
        socket.broadcast.emit('update', splitOscName[1], splitOscName[2], data.value);
    });
});
