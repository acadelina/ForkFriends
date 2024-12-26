package ubb.scs.map.lab6.service;

import ubb.scs.map.lab6.domain.Post;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.repository.file.PostDBRepository;
import ubb.scs.map.lab6.repository.file.UserDBRepository;
import ubb.scs.map.lab6.utils.observer.Observable;
import ubb.scs.map.lab6.utils.observer.Observer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class ServicePost implements Observable {
    private PostDBRepository repo;
    private UserDBRepository userRepo;
    private final List<Observer> observers = new ArrayList<>();

    public ServicePost(PostDBRepository repo, UserDBRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<Post>();
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .sorted(Comparator.comparing(Post::getId))
                .toList();
    }

    public List<Post> getPostsByUser(User user) {
        List<Post> posts = new ArrayList<Post>();
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(p -> p.getAuthorId().equals(user.getId()))
                .sorted(Comparator.comparing(Post::getId))
                .toList();
    }
    public Post getPost(Long id) {
        if(repo.findOne(id).isPresent()) {
            return repo.findOne(id).get();
        };
        return null;
    }
    public void addLike(Post post,Long id) {
        Optional<User> user=null;
        user=userRepo.findOne(id);
        if(user.isPresent()) {
            repo.addLike(post, user.get());
        }
        notifyObservers();
    }
    public void createPost(Post post) {
        repo.save(post);
        notifyObservers();
    }
    public void deletePost(Long id) {
        repo.delete(id);
        notifyObservers();
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
        observers.forEach(e -> e.update());
    }
}
