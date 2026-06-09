package com.example.gymhome.model;

public class GenericResponse {
    private boolean success; // trạng thái: true nếu thành công, false nếu không thành công
    private String message;
    private String error;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getError() { return error; }
}
