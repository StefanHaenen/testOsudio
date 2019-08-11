package main.exceptions;

public class OpeningHoursException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public OpeningHoursException(String message){super(message);};
}
