NxButton : NxWidget {
    var <press;
}

NxColors : NxWidget {
    var <r, <g, <b;
}

NxComment : NxWidget {
    var <text;
}

NxDial : NxWidget {
    var <value;
}

NxJoints : NxWidget {
    var <x, <y;

    var <node0, <node1, <node2, <node3, 
        <node4, <node5, <node6, <node7; 
}

NxKeyboard : NxWidget {
    // add support for midi property?
    var <on, <note;
}

NxMatrix : NxWidget {
    var <row, <col, <value;
}

NxMessage : NxWidget {
    var <message;
}

NxMouse : NxWidget {
    var <x, <y, <deltax, <deltay;
}

NxMultiSlider : NxWidget {
    var <list;
    
    *new { |bounds serverAddr|
        ^super.new(bounds, serverAddr).initMs;
    }

    initMs {
        list = 0 ! 16;
    }

    list_ { |l|
        // if list comes in as a string, split it on spaces and convert to i
        // floats - Nexus OSC sends lists as strings
        if(l.isString) {
            list = l.split(" ").collect(_.asFloat); 
            this.instVarPut(\list, list);
            action.(this);
        } {
            this.updateProperty(\list, l.join($ ));
        };
    }
}

NxNumber : NxWidget {
    var <value;
}

NxPosition : NxWidget {
    var <x, <y;
    var <state;
}

NxRange : NxWidget {
    var <start, <stop, <size;
}

NxToggle : NxWidget {
    var <value;
}

NxTilt : NxWidget {
    var <x, <y, <z;
}

NxTypewriter : NxWidget {
    var <key, <ascii, <on;
}

NxWheel : NxWidget {
    var <value;
}

NxSlider : NxWidget {
    var <value;
}
