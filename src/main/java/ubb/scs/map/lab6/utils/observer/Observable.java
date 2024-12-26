package ubb.scs.map.lab6.utils.observer;


import ubb.scs.map.lab6.utils.events.Event;

public interface Observable {
    void addObserver(Observer e);
    void removeObserver(Observer e);
    void notifyObservers();
}
