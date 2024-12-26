package ubb.scs.map.lab6.repository.file;

import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.domain.validators.Validator;

public class UserRepository extends AbstractFileRepository<Long, User>{
    public UserRepository(Validator<User> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public User createEntity(String line) {
        String[] splited = line.split(";");
        User u = new User(splited[1], splited[2],splited[3],splited[4],splited[5]);
        u.setId(Long.parseLong(splited[0]));
        return u;
    }

    @Override
    public String saveEntity(User entity) {
        String s = entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
        return s;
    }



}
