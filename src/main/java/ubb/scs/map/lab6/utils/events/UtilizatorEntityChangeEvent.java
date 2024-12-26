package ubb.scs.map.lab6.utils.events;


import ubb.scs.map.lab6.domain.User;

public class UtilizatorEntityChangeEvent implements Event {
    private ChangeEventType type;
    private User data, oldData;

    public UtilizatorEntityChangeEvent(ChangeEventType type,User data) {
        this.type = type;
        this.data = data;
    }
    public UtilizatorEntityChangeEvent(ChangeEventType type, User data, User oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getData() {
        return data;
    }

    public User getOldData() {
        return oldData;
    }
}