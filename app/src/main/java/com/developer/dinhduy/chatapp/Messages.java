package com.developer.dinhduy.chatapp;

public class Messages {

    private  String message,type;
    private Boolean seen;
    private long timesamp;
    private String from;

    public Messages(String type, Boolean seen, long timesamp, String from) {
        this.type = type;
        this.seen = seen;
        this.timesamp = timesamp;
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Messages() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String mesages) {
        this.message = mesages;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return timesamp;
    }

    public void setTime(long time) {
        this.timesamp = time;
    }
}
