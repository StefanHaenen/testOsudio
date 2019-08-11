package main.exceptions;

public class BusinessHourCalculatorException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public BusinessHourCalculatorException(String message){super(message);};
}
