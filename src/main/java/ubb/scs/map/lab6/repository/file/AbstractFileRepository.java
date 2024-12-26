package ubb.scs.map.lab6.repository.file;

import ubb.scs.map.lab6.domain.Entity;
import ubb.scs.map.lab6.domain.validators.Validator;
import ubb.scs.map.lab6.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Optional;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E>{
    private String filename;

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        filename=fileName;
        loadData();
    }


    public abstract E createEntity(String line);
    public abstract String saveEntity(E entity);


    @Override
    public Optional<E> save(E entity) {
        Optional<E> e = super.save(entity);
        if (e == null)
            writeToFile();
        return e;
    }

    private void writeToFile() {

        try  ( BufferedWriter writer = new BufferedWriter(new FileWriter(filename))){
            for (E entity: entities.values()) {
                String ent = saveEntity(entity);
                writer.write(ent);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadData(){
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;
            while ((line = reader.readLine()) != null) {
                E ent=createEntity(line);
                super.save(ent);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> ent=super.delete(id);
        writeToFile();
        return ent;
    }

    @Override
    public Optional<E> update(E entity) {
        Optional<E> ent=super.update(entity);
        writeToFile();
        return ent;
    }
}
