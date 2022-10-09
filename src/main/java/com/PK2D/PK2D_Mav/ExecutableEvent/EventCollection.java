package com.PK2D.PK2D_Mav.ExecutableEvent;


import com.PK2D.PK2D_Mav.Util.LogPreference;
import com.PK2D.PK2D_Mav.Util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventCollection {
    private List<ExecutableEvent> events = new ArrayList<>();
    private List<String> eventID = new ArrayList<>();
    public EventCollection() {
    }

    public ExecutableEvent createEvent(String ID) {
        if (this.eventID.contains(ID)) {
            Util.log("Cannot create duplicate event: " + ID, LogPreference.EVENT_ERROR);
            return null;
        }
        this.events.add(new ExecutableEvent());
        this.eventID.add(ID);
        return this.getEvent(ID);
    }

    public ExecutableEvent getEvent(String ID) {
        ExecutableEvent event = this.events.get(indexOfID(ID));
        if (Objects.isNull(event)) Util.log("Event " + ID + " does not exist");
        return event;
    }

    private int indexOfID(String ID) {
        return this.eventID.indexOf(ID);
    }

}
