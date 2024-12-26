package ubb.scs.map.lab6.service;

import ubb.scs.map.lab6.domain.Message;
import ubb.scs.map.lab6.domain.Tuple;
import ubb.scs.map.lab6.repository.file.FriendshipDBRepository;
import ubb.scs.map.lab6.repository.file.MessageDBRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class ServiceMessage {
    private MessageDBRepository messageDBRepository;
    private FriendshipDBRepository friendshipDBRepository;

    public ServiceMessage(MessageDBRepository messageDBRepository, FriendshipDBRepository friendshipDBRepository) {
        this.messageDBRepository = messageDBRepository;
        this.friendshipDBRepository = friendshipDBRepository;
    }

    public void addMessage(Message message) throws ServiceException {
        if(friendshipDBRepository.findOne(new Tuple<>(message.getIdSender(),message.getIdReceiver())).isEmpty()&& friendshipDBRepository.findOne(new Tuple<>(message.getIdReceiver(),message.getIdSender())).isEmpty()) {
            throw new ServiceException("The users are not friends!");
        }

        messageDBRepository.save(message);
    }

    public Message findById(Long id) {
        AtomicReference<Message> message= new AtomicReference<>();
        getMessages().forEach(c->{
            if(c.getId() == id) {
                message.set(c);
            }
        });
        return message.get();
    }

    public void setReply(Message message,Long reply) {
        message.setReply(reply);
        messageDBRepository.update(message);
    }

    public Iterable<Message> getMessages() {
        List<Message> messages = new ArrayList<>();
        messageDBRepository.findAll().forEach(messages::add);

        messages.sort(Comparator.comparing(Message::getDate));
        return messages;
    }

    private boolean isPartOfConversation(Message message,Long id1,Long id2) {
        return((message.getIdSender().equals(id1)&&message.getIdReceiver().equals(id2))||(message.getIdSender().equals(id2)&&message.getIdReceiver().equals(id1)));
    }

    public List<Message> getConversation(Long id1,Long id2) {
        List<Message> messages =(List<Message>)getMessages();
        List<Message> conversation= new ArrayList<>();

        for(Message message:messages) {
            if(isPartOfConversation(message,id1,id2)) {
                conversation.add(message);
            }
        }
        return conversation;
    }
}
