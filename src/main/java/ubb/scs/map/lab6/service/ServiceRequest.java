package ubb.scs.map.lab6.service;

import com.sun.net.httpserver.Request;
import ubb.scs.map.lab6.domain.FriendRequest;
import ubb.scs.map.lab6.domain.Friendship;
import ubb.scs.map.lab6.domain.Tuple;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.domain.validators.ValidationException;
import ubb.scs.map.lab6.repository.file.*;
import ubb.scs.map.lab6.utils.events.ChangeEventType;
import ubb.scs.map.lab6.utils.events.Event;
import ubb.scs.map.lab6.utils.events.FriendshipEntityChangeEvent;
import ubb.scs.map.lab6.utils.events.RequestEntityChangeEvent;
import ubb.scs.map.lab6.utils.observer.Observable;
import ubb.scs.map.lab6.utils.observer.Observer;

import javax.swing.event.ChangeEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ServiceRequest implements Observable {
    private final FriendshipDBRepository friendshipRepository;
    private final UserDBRepository userRepository;
    private final RequestDBRepository requestDBRepository;
    private final List<Observer> observers = new ArrayList<>();

    public ServiceRequest(FriendshipDBRepository friendshipRepository, UserDBRepository userRepository, RequestDBRepository requestDBRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.requestDBRepository = requestDBRepository;
    }

    public void addRequest(FriendRequest request) {
        if(friendshipRepository.findOne(new Tuple<>(request.getId().getE2(), request.getId().getE1())).isPresent() || friendshipRepository.findOne(request.getId()).isPresent() ) {
            throw new ValidationException("Friendship already exists");
        }

        if(requestDBRepository.findOne(request.getId()).isPresent()) {
            throw new ValidationException("Request already exists");
        }

        requestDBRepository.save(request);
        RequestEntityChangeEvent event = new RequestEntityChangeEvent(ChangeEventType.ADD,request);
        notifyObservers();
    }


    public void respondRequest(Tuple<Long,Long> id, String answer) {
        Optional<FriendRequest> request = requestDBRepository.findOne(id);
        if(request.isEmpty()) {
            throw new ValidationException("Request not found");
        }

        if("APPROVED".equals(answer)) {
            Friendship friendship = new Friendship();
            friendship.setId(id);
            friendship.setDate(LocalDateTime.now());
            friendshipRepository.save(friendship);
            FriendshipEntityChangeEvent event = new FriendshipEntityChangeEvent(ChangeEventType.ADD, friendship);
            notifyObservers();
            Optional<FriendRequest> req= requestDBRepository.delete(id);
            notifyObservers();
        }
        else {
            Optional<FriendRequest> req= requestDBRepository.delete(id);
            notifyObservers();
        }
    }

    public List<User> pendingRequests(Long idUser)
    {
        List<User> users = new ArrayList<>();
        userRepository.findOne(idUser).ifPresentOrElse(
                user->{
                    requestDBRepository.findAll().forEach(request->
                    {
                        if(Objects.equals(request.getId().getE2(), idUser)) {
                            userRepository.findOne(request.getId().getE1()).ifPresent(users::add);
                        }
                    });
                },
                ()->{
                    throw new ValidationException("User not found");
                });
        return users;
    }


    @Override
    public void addObserver(Observer e) {
            observers.add(e);
    }

    @Override
    public void removeObserver(Observer e) {
            observers.remove(e);
    }

    @Override
    public void notifyObservers() {
            observers.forEach(observer -> observer.update());
    }
}
