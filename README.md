# Duplex Nexus OSC

Duplex Nexus OSC allows [Nexus UI](http://www.nexusosc.com) control surfaces to be constructed via OSC with out the hassle of writing any JavaScript or HTML.

In contrast to the OSC servers provided for Nexus UI it also broadcasts the current state of its widget to all users connected to the system allowing interfaces that multiple users can interact with at the same time to be built.

## Installing the server

1.  Install [node.js](http://nodejs.org).

2.  Download and unpack this [repository archive](https://github.com/triss/duplex-nexus-osc/archive/master.zip).

    Or clone the repsoitory using git: 

    ``` git clone https://github.com/triss/duplex-nexus-osc.git ```

3.  Move in to the folder you just created: 

    ```cd duplex-nexus-osc```

4.  Install dependancies with node's package manager: 

    ``` npm install ```

## Starting the Duplex Nexus OSC server

Launch the server from the folder you just unpacked with node:
```
node nxserver.js
```

## Viewing your Nexus UI

Point as many web browsers as you like at, clients can be added and removed at will:
```
http://<your-servers-ip-addresss>:8080
```

## Interacting with Duplex Nexus OSC via OSC

### Adding/updating/deleting widgets via OSC

Duplex Nexus OSC responds to the following OSC messages:

Message           | Params              | Example 
------------------|---------------------|-----------------------------------------
\nexus\create     | name type x y w h   | \nexus\create mySlider slider 0 0 25 100
\nexus\update     | name property value | \nexus\update mySlider value 0.5
\nexus\delete     | name                | \nexus\delete mySlider
\nexus\osc_client | ip port             | \nexus\osc_client "127.0.0.1" 9999

Valid Nexus widgets types include: dial, position, keyboard, button, toggle, slider, multislider, matrix, select, tilt, pixels, colors, joints, comment, message, number, and multitouch.

The properties that can be updated for each of the widget types are documented in the [Nexus API](http://nexusosc.com/api/).

### Recieving messages from Nexus OSC

Nexus UI sends back data about the state of the UI in messages as follows.
```
/<name>/<property> <value>
```
Some examples might be:

```
/mySlider/value 0.5
/mySlider/dial 0.5
/myPosition/x 0.133
/myPosition/y 0.77
/myColor/r 127
/myColor/g 10
/myColor/b 10
```

## SuperCollider 

### Installing the Extension

Copy SuperCollider\NexusWidgetsExtension folder to SuperColliders Extension folder (Select File -> Open user suport directory in the SC IDE if you don't know where this is and then the Extension folder within that if you don't know where this is).

### Using the Extension

Start the server and connect clients as described above.

Here's a very simple example to get you started:

```SuperCollider
(
// create a Sine oscilator synth with amp and freq as paramaters
a = { |freq=200 amp=0.5| SinOsc.ar(freq) * amp }.play;

// create an NxPosition (XY) widget on the default Duplex Nexus OSC server
// by default widgets fill 100% of the screen
z = NxPosition();
z.action = { |nx| 
    a.set(
        \freq, \freq.asSpec.map(nx.x) 
        \amp, \amp.asSpec.map(nx.y)
    )
}
)
```

See the Nx*Blah* classes in SuperColliders help system for more info.
