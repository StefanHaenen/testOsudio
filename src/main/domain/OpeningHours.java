package main.domain;

import main.exceptions.OpeningHoursException;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

public class OpeningHours {
    private LocalTime openingTime;
    private LocalTime closingTime;

    //constructors
    public OpeningHours(LocalTime openingTime, LocalTime closingTime) throws OpeningHoursException {
        if(openingTime.isBefore(closingTime)){
            setOpeningTime(openingTime);
            setClosingTime(closingTime);
        }
        else {
            throw new OpeningHoursException("Invalid opening hours: the opening time has to be before the closing time.");
        }
    }

    public OpeningHours(){
        openingTime = LocalTime.of(8, 0);
        closingTime = LocalTime.of(20, 0);
    }

    //getters & setters
    public LocalTime getOpeningTime() {
        return openingTime;
    }

    private void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    private void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    //equals, hashCode & toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpeningHours that = (OpeningHours) o;
        return Objects.equals(openingTime, that.openingTime) &&
                Objects.equals(closingTime, that.closingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openingTime, closingTime);
    }

    @Override
    public String toString() {
        return "main.domain.OpeningHours{" +
                "OpeningTime=" + openingTime +
                ", ClosingTime=" + closingTime +
                '}';
    }

    //other methods
    public long secondsBetweenOpeningAndClosing(){
        return Duration.between(openingTime,closingTime).getSeconds();
    }
}
