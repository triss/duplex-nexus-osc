NxWidget {
    classvar widgetCounter = 0;
    var type;

    var <>serverAddr;
    var oscResponders;
    var oscName;
    
    var <bounds;
    var properties;

    var <>action;

    *new { |bounds serverAddr|
        ^super.new.init(bounds, serverAddr);
    }

    init { |bnds sAddr|
        var widgetProperties;

        // if no serverAddr specified use default port
        serverAddr = sAddr ?? { NetAddr("127.0.0.1", 4444) };

        // if no bounds passed in set some
        bounds = bnds ?? { Rect(0, 0, 100, 100) };

        // set up dictionary of osc responders so we can clean up easily
        oscResponders = IdentityDictionary();
        
        // This iinitialisation routine use meta programming to make creation
        // of new widget types as light as possible

        // Look up the class name of this widget, drop 'Nx' from the start to
        // understand what type of Nexus widget this object represents
        type = this.class.name.asString.drop(2).toLower; 

        // generate an OSC name and create the widghet on the server
        oscName = 'sc' ++ type ++ NxWidget.nextWidgetNumber;
        this.create;

        // get the list of properties this widget exposes from the variable 
        // names defined by the sub class
        widgetProperties = 
            IdentitySet.newFrom(this.class.instVarNames) 
            - IdentitySet.newFrom(NxWidget.instVarNames); 

        widgetProperties.do { |wp|
            // work out OSC path for this property
            var responderPath = if(wp == \value) {
                '/' ++ oscName;
            } {
                '/' ++ oscName ++ '/' ++ wp;
            };

            // add an osc responder for this property
            oscResponders[wp] = OSCFunc({ |msg|
                this.instVarPut(wp, msg[1]);
                action.(this);
            }, responderPath);

            // create setters for properties that don't have them
            if(this.respondsTo(wp.asSetter).not) {
                this.addUniqueMethod(wp.asSetter, { |obj value|
                    this.instVarPut(wp, value);
                    this.updateProperty(wp, value);
                });
            };
        };
    }

    color_ { |c| 
        this.updateProperty('color', c.hexString);
    }

    bounds_ { |rect|
        bounds = rect;
        this.create;
    }

    // each widget needs a name - we use this counter to produce one for 
    // each widget we create
    *nextWidgetNumber {
        widgetCounter = widgetCounter + 1;
        ^widgetCounter;
    }

    // OSC communication with server
    create {
        serverAddr.sendMsg(
            '/nexus/create', oscName, type, 
            bounds.left, bounds.top,
            bounds.width, bounds.height
        );
    }

    updateProperty { |property value| 
        serverAddr.sendMsg(
            '/nexus/update', oscName, property, value
        );
    }

    remove {
        serverAddr.sendMsg(
            '/nexus/delete', oscName
        );
    }
}
