package ubb.scs.map.lab6.repository.file;

import ubb.scs.map.lab6.domain.Friendship;
import ubb.scs.map.lab6.domain.Tuple;
import ubb.scs.map.lab6.domain.validators.Validator;

public class FriendshipRepository extends AbstractFileRepository<Tuple<Long,Long>, Friendship> {


    public FriendshipRepository(Validator<Friendship> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public Friendship createEntity(String line) {
        String[] splited=line.split(";");
        Tuple<Long,Long> tuple=new Tuple(Long.parseLong(splited[0]),Long.parseLong(splited[1]));
        Friendship friendship = new Friendship();
        friendship.setId(tuple);
        return friendship;
    }

    @Override
    public String saveEntity(Friendship entity) {
        Tuple<Long,Long> tuple=entity.getUsers();
        String line=tuple.getE1().toString()+";"+tuple.getE2().toString();
        return line;
    }


}
