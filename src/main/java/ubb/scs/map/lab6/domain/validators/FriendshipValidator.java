package ubb.scs.map.lab6.domain.validators;

import ubb.scs.map.lab6.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getId().getE1().equals("")|| entity.getId().getE2().equals(""))
            throw new ValidationException("Users are required");
        if(entity.getId().getE1().equals(entity.getId().getE2()))
            throw new ValidationException("Users can't be equal");

    }
}
