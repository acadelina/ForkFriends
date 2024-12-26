package ubb.scs.map.lab6.service;

import ubb.scs.map.lab6.domain.Friendship;
import ubb.scs.map.lab6.domain.Tuple;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.domain.validators.ValidationException;
import ubb.scs.map.lab6.repository.Repository;
import ubb.scs.map.lab6.repository.file.FriendshipRepoPage;
import ubb.scs.map.lab6.repository.file.Page;
import ubb.scs.map.lab6.repository.file.Pageable;
import ubb.scs.map.lab6.utils.events.ChangeEventType;
import ubb.scs.map.lab6.utils.events.Event;
import ubb.scs.map.lab6.utils.events.FriendshipEntityChangeEvent;
import ubb.scs.map.lab6.utils.observer.Observable;
import ubb.scs.map.lab6.utils.observer.Observer;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceFriendship implements Observable {

    Repository<Long, User> repoUser;
    Repository<Tuple<Long,Long>, Friendship> repoFriendship;
    FriendshipRepoPage<Tuple<Long,Long>,Friendship> repoFriendshipPage;
    List<Observer> observers = new ArrayList<>();

    public ServiceFriendship(FriendshipRepoPage<Tuple<Long,Long>, Friendship>  repoFriendship, Repository<Long, User>  repoUser) {
        this.repoUser = repoUser;
        this.repoFriendshipPage = repoFriendship;
    }

    public List<Friendship> getFriendships() {
            List<Friendship> friendships = new ArrayList<>();
            repoFriendshipPage.findAll().forEach(friendships::add);
            //repoFriendshipPage.findAll().forEach(fr.get);
            return friendships;
    }

    public List<User> getUsers()
    {
        List<User> users=new ArrayList<>();
        repoUser.findAll().forEach(users::add);
        return users;
    }


    public void addFriendship(Long id1,Long id2) throws ServiceException {
            User user1,user2;
            try {
                user1 = repoUser.findOne(id1).orElseThrow(ValidationException::new);
                user2 = repoUser.findOne(id2).orElseThrow(ValidationException::new);
            }
            catch(ValidationException e) {
                throw new ServiceException("User not found");
            }


            getFriendships().forEach(
                    f->{
                        if((f.getId().getE1().equals(user1.getId())&&f.getId().getE2().equals(user2.getId()))||(f.getId().getE1().equals(user2.getId())&&f.getId().getE2().equals(user1.getId())))
                        {    throw new ValidationException("Friendship already exist!");}
                    }
            );


            user1.addFriend(user2);
            user2.addFriend(user1);
            Friendship friendship=new Friendship(user1,user2);
            repoFriendshipPage.save(friendship);
            //FriendshipEntityChangeEvent event = new FriendshipEntityChangeEvent(ChangeEventType.ADD, friendship);
            notifyObservers();
    }


    public void removeFriendship(Tuple<Long,Long> id) throws ServiceException {
        User user1,user2;
        Tuple<Long,Long> idF=id;
        boolean exist= getFriendships().stream()
                .anyMatch(it-> Objects.equals(id,it.getId()));
        Tuple<Long,Long> id2= new Tuple<>(id.getE2(), id.getE1());
        if(!exist) {
            exist = getFriendships().stream().anyMatch(it -> Objects.equals(id2, it.getId()));
            idF=id2;

        }
//        if(!exist)
//            throw new ServiceException("Friendship does not exist!");
        try {
            user1 = repoUser.findOne(id.getE1()).orElseThrow(ValidationException::new);
            user2 = repoUser.findOne(id.getE2()).orElseThrow(ValidationException::new);
        }catch(ValidationException e)
        {
            throw new ServiceException("User not found");
        }
        user1.removeFriend(user2);
        user2.removeFriend(user1);
        repoFriendshipPage.delete(id);
        notifyObservers();
    }

    /*
     *
     * @param id -the id of one of the users from the friendships that we want to delete
     *
     */
    public void deleteAllFriendships(Long id) throws ValidationException {
        getFriendships().forEach(
                f->{
                    if(f.getId().getE1().equals(id)||f.getId().getE2().equals(id))
                        try{
                            removeFriendship(f.getId());

                        } catch (ServiceException e) {
                            throw new ValidationException(e);
                        }
                }
        );

    }

    /*
    *
    * @return - the number of communities
    *
    */
    public int communitiesNumber()
    {
        List<Long> visitedUsers=new ArrayList<>();
        List<User> users=getUsers();
        AtomicInteger count= new AtomicInteger();
        users.forEach(u->{
            if(!visitedUsers.contains(u.getId()))
            {
                List<User> comunity=new ArrayList<>();
                try {
                    DFS(u.getId(),visitedUsers,comunity);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                count.getAndIncrement();
            }
        });

        return count.get();
    }


    /*
    *
    * @return- a string with the users that form the most social community
    *
    */
    public String socialCommunity()
    {

        List<Long> visitedUsers = new ArrayList<>();


        AtomicInteger max= new AtomicInteger(-1);
        Iterable<User> all = repoUser.findAll();


        AtomicReference<List<User>> socialCommunity = new AtomicReference<>(new ArrayList<>());
        StringBuilder rez = new StringBuilder();

        all.forEach(it->{
            if(!visitedUsers.contains(it.getId())){
                List<User> currentCommunity = new ArrayList<>();
                try {
                    DFS(it.getId(), visitedUsers, currentCommunity);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if (currentCommunity.size() > max.get()) {
                    max.set(currentCommunity.size());
                    socialCommunity.set(currentCommunity);
                }
            }
        });


        socialCommunity.get().forEach(user-> rez.append(user.getId()+"."+user.getFirstName()).append(" ").append(user.getLastName()).append("  "));

        return rez.toString();
    }

    public Collection<User> getFriends(User user)
    {
        Collection<User> friends=new ArrayList<>();
        getFriendships().stream().filter(fr->fr.getUsers().getE1().equals(user.getId())||fr.getUsers().getE2().equals(user.getId()))
                .forEach(fr->{
                    Long idFr=fr.getId().getE1().equals(user.getId())?fr.getId().getE2():fr.getId().getE1();
                    User friend=repoUser.findOne(idFr).orElseThrow(ValidationException::new);
                    friends.add(friend);
        });
        return friends;
    }

    private void DFS(Long userId, List<Long> visitedUsers, List<User> community) throws SQLException {

        visitedUsers.add(userId);

        Optional<User> user=repoUser.findOne(userId);
        if(user.isPresent()){

            community.add(user.get());

            List<Friendship> friendships = getFriendships();

            friendships.stream()
                    .filter(friendship -> friendship.getId().getE1().equals(userId) || friendship.getId().getE2().equals(userId))
                    .forEach(friendship ->
                    {
                        Long friendId = (friendship.getId().getE1().equals(userId) ? friendship.getId().getE2() : friendship.getId().getE1());
                        if (!visitedUsers.contains(friendId)) {
                            try {
                                DFS(friendId, visitedUsers, community);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
        }
    }


    public Page<Friendship> findAllUserFriends(Pageable pageable,User user){
        Page<Friendship> pgF=repoFriendshipPage.findAllUserFriends(pageable,user.getId());
        List<User> users=new ArrayList<>();
//        for(Friendship f:pgF.getElementsOnPage())
//        {
//            Long friendId;
//            if(f.getId().getE1().equals(user.getId()))
//                friendId=f.getId().getE2();
//            else
//                friendId=f.getId().getE1();
//            User friend=repoUser.findOne(friendId).orElseThrow(ValidationException::new);
//            users.add(friend);
//        }
        Page<User> pgU=new Page<User>(users,users.size());
        return pgF;
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
        observers.forEach(o->o.update());
    }
}
