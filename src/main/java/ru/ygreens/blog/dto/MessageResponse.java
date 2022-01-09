package ru.ygreens.blog.dto;

public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }private String password;

    public void setMessage(String message) {
        this.message = message;
    }
}
