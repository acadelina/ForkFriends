package ubb.scs.map.lab6.domain;

import java.time.LocalDateTime;
import java.util.Optional;

public class Message extends Entity<Long>{
    private String message;
    private Long idSender;
    private Long idReceiver;
    private LocalDateTime date;
    private Long reply;

    public Message(String message, Long idSender, Long idReceiver, LocalDateTime date) {
        this.message = message;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.date = date;
        this.reply = null;
    }

    public Message(String message, Long idSender, Long idReceiver, LocalDateTime date,Long reply) {
        this.message = message;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.date = date;
        this.reply = reply;
    }

    public Message()
    {
        this.date=LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Long getIdSender() {
        return idSender;
    }
    public void setIdSender(Long idSender) {
        this.idSender = idSender;
    }
    public Long getIdReceiver() {
        return idReceiver;
    }
    public void setIdReceiver(Long idReceiver) {
        this.idReceiver = idReceiver;
    }
    public LocalDateTime getDate() {
        return date;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }


}
