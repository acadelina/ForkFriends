package ubb.scs.map.lab6.domain.validators;

import ubb.scs.map.lab6.domain.Entity;

public class EntityValidator implements Validator <Entity> {

    @Override
    public void validate(Entity entity) throws ValidationException {
        if(entity.getId()==null)
            throw new ValidationException("Entity must have an ID");
    }
}
