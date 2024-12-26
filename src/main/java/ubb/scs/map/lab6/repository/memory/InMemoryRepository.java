package ubb.scs.map.lab6.repository.memory;


import ubb.scs.map.lab6.domain.Entity;
import ubb.scs.map.lab6.domain.validators.ValidationException;
import ubb.scs.map.lab6.domain.validators.Validator;
import ubb.scs.map.lab6.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    protected Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public Optional<E> findOne(ID id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) throws ValidationException {
        if(entity==null)
            throw new IllegalArgumentException("ENTITY CANNOT BE NULL");
        validator.validate(entity);
        if(entities.containsKey(entity.getId()))
            return Optional.of(entity);
        else{
            entities.put((ID) entity.getId(),entity);
            return null;
        }
    }

    @Override
    public Optional<E> delete(ID id) {
        if(id==null){
            throw new IllegalArgumentException("ID CANNOT BE NULL");
        }
        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) {
        if(entity==null)
            throw new IllegalArgumentException("ENTITY CANNOT BE NULL");
        try{
            validator.validate(entity);
        }
        catch(ValidationException e){
            throw new IllegalArgumentException(e.getMessage());
        }
        if(findOne((ID) entity.getId())!=null)
        {
            entities.put((ID) entity.getId(),entity);
            return null;
        }
        return Optional.of(entity);
    }
}
