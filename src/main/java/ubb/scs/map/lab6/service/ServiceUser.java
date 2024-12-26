package ubb.scs.map.lab6.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.repository.Repository;
import ubb.scs.map.lab6.utils.events.ChangeEventType;
import ubb.scs.map.lab6.utils.events.Event;
import ubb.scs.map.lab6.utils.events.UtilizatorEntityChangeEvent;
import ubb.scs.map.lab6.utils.observer.Observable;
import ubb.scs.map.lab6.utils.observer.Observer;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceUser implements Observable {

    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private Repository<Long, User> repository;
    List<Observer> observers= new ArrayList<>();

    public ServiceUser(Repository<Long, User> repository) {
        this.repository =repository;
    }


    public void addUser(String firstName, String lastName, String email,String username,String password) throws ServiceException{
        User user=new User(firstName,lastName,email,username,encoder.encode(password),null);

        AtomicBoolean exist= new AtomicBoolean(false);
        repository.findAll().forEach(it -> {
            if (Objects.equals(it.getId(), user.getId())) {
                exist.set(true);
            }
        });
        if(exist.get()){
            throw new ServiceException("User already exists");
        }

        if(repository.save(user).isEmpty())
        {
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, user);
            notifyObservers();

        }
    }

    public void updateUser(Long id,String firstName,String lastName,String email) throws ServiceException{
        User user;
        try{
            user= repository.findOne(id).orElseThrow(() -> new ServiceException("User not found with ID: " + id));
        }catch (ServiceException e) {
            System.err.println(e.getMessage());
            throw e;
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        repository.update(user);
        notifyObservers();
    }

    public void addPhoto(Long id,String firstName,String lastName,String email,byte[] photo) throws ServiceException{
        User user;
        try{
            user= repository.findOne(id).orElseThrow(() -> new ServiceException("User not found with ID: " + id));
        }catch (ServiceException e) {
            System.err.println(e.getMessage());
            throw e;
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoto(photo);
        repository.update(user);
        notifyObservers();
    }

    public void removeUser(Long id) throws ServiceException {


        try {
            User user = repository.findOne(id)
                    .orElseThrow(() -> new ServiceException("User not found with ID: " + id));
            Optional<User> user2=repository.delete(id);
            notifyObservers();
            System.out.println("User with ID " + id + " successfully deleted.");
        } catch (ServiceException e) {
            System.err.println(e.getMessage());
            throw e;
        }

    }

    public List<User> getUsers() {
        List<User> list=new ArrayList<>();
        repository.findAll().forEach(list::add);
        return list;
    }

    public User login(String username, String password) {
        Optional<User> user= getUsers().stream().filter(it -> Objects.equals(it.getUsername(), username) && encoder.matches(password,it.getPassword())).findFirst();
        if(user.isEmpty()) return null;
        return user.get();
    }

    public User findUser(Long id) {
        return repository.findOne(id).orElse(null);
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
