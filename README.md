# Duplex Nexus OSC

Duplex Nexus OSC allows [Nexus UI](http://www.nexusosc.com) control surfaces to be quickly constructed via OSC with out the hassle of writing any JavaScript or HTML.

In contrast to the OSC servers provided for NexusUI it also broadcasts the current state of its widget to all users connected to the system allowing developers to easily build interfaces that multiple users can interact with at the same time.

## Installing the server

1.  Install [node.js](http://nodejs.org).

2.  Download and unpack this [repository archive](https://github.com/triss/duplex-nexus-osc/archive/master.zip).

    Or clone the repsoitory using git: ``` git clone https://github.com/triss/duplex-nexus-osc.git ```

3.  Move in to the folder you just created: ```cd duplex-nexus-osc```

4.  Install dependancies with node's package manager: ``` npm install ```

## Starting Duplex Nexus OSC server

Run:
```
node nxserver.js
```

## Viewing your Nexus UI

Point a browser at:
```
http://<your-ip-addresss>:8080
```

## Using Duplex Nexus OSC via OSC

### Adding/updating/deleting widgets via OSC

Duplex Nexus OSC responds to the following OSC messages:

Message           | Params              | Example 
------------------|---------------------|-----------------------------------------
\nexus\create     | name type x y w h   | \nexus\create mySlider slider 0 0 25 100
\nexus\update     | name property value | \nexus\update mySlider value 0.5
\nexus\delete     | name                | \nexus\delete mySlider
\nexus\osc_client | ip port             | \nexus\osc_client "127.0.0.1" 9999

Valid Nexus widgets types include: dial, position, keyboard, button, toggle, slider, multislider, matrix, select, tilt, metroball, pixels, colors, sandbox, joints, comment, message, number, banner, multitouch.

The properties that can be updated for each of the widget types are documented in the [Nexus API](http://nexusosc.com/api/).

### Recieivng messages from Nexus OSC

Nexus UI sends back data about the state of the UI in messages as follows.
```
/mySlider/value 0.5
```

## Using the SuperCollider extensions

```SuperCollider
(
// create a Sine oscilator synth with amp and freq as paramaters
a = { |freq=200 amp=0.5| SinOsc.ar(freq) * amp }.play;


z = NxPosition();
z.action = { |nx| 
    a.set(
        \freq, \freq.asSpec.map(nx.x) 
        \amp, \amp.asSpec.map(nx.y)
    )
}
)
```
