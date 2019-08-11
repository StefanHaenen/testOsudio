package main.domain;

import main.exceptions.BusinessHourCalculatorException;
import main.exceptions.OpeningHoursException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class BusinessHourCalculator {
    private static DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm");
    private Set<DayOfWeek> closedDaysOfWeek = new TreeSet<>();
    private Set<LocalDate> closedSpecificDates = new TreeSet<>();
    private OpeningHours defaultOpeningHours = new OpeningHours();
    private Map<DayOfWeek, OpeningHours> differentOpeningHoursDayOfWeek = new TreeMap<>();
    private Map<LocalDate, OpeningHours> differentOpeningHoursSpecificDates = new TreeMap<>();

    //Constructors
    public BusinessHourCalculator(String defaultOpeningTime, String defaultClosingTime)
            throws OpeningHoursException {
        this.defaultOpeningHours = new OpeningHours(  LocalTime.parse(defaultOpeningTime),
                                                        LocalTime.parse(defaultClosingTime));
    }

    //Setters
    public void setOpeningHours(DayOfWeek dayOfWeek, String openingTimeString, String closingTimeString)
            throws OpeningHoursException{
        LocalTime openingTime = LocalTime.parse(openingTimeString);
        LocalTime closingTime = LocalTime.parse(closingTimeString);
        differentOpeningHoursDayOfWeek.put(dayOfWeek, new OpeningHours(openingTime, closingTime));
    }

    public void setOpeningHours(String localDateString,String openingTimeString, String closingTimeString)
            throws OpeningHoursException {
        LocalDate localDate = LocalDate.parse(localDateString);
        LocalTime openingTime = LocalTime.parse(openingTimeString);
        LocalTime closingTime = LocalTime.parse(closingTimeString);
        differentOpeningHoursSpecificDates.put(localDate, new OpeningHours(openingTime, closingTime));
    }

    public void setClosed(DayOfWeek... daysOfWeek){
        closedDaysOfWeek.addAll(Arrays.asList(daysOfWeek));
    }

    public void setClosed(String... closedDates){
        closedSpecificDates.addAll(Arrays.stream(closedDates).map(x->LocalDate.parse(x)).collect(Collectors.toList()));
    }

    //Methods
    public OpeningHours getOpeningHoursForDate(LocalDate startingDate){
        DayOfWeek startingDayOfWeek = DayOfWeek.from(startingDate);

        //afwijkende openingsuren voor een specifieke datum krijgen prioriteit
        if(differentOpeningHoursSpecificDates.containsKey(startingDate)){
            return differentOpeningHoursSpecificDates.get(startingDate);
        }
        else{
            //afwijkende openingsuren voor een weekdag krijgen voorrang op de standaardopeningsuren
            if(differentOpeningHoursDayOfWeek.containsKey(DayOfWeek.from(startingDayOfWeek))){
                return differentOpeningHoursDayOfWeek.get(startingDayOfWeek);
            }
            else {
                //standaardopeningsuren
                return defaultOpeningHours;
            }
        }
    }

    public LocalDate getNextBusinessDay(LocalDate startingDate){
        LocalDate nextBusinessDay = startingDate;
        do {
            nextBusinessDay = nextBusinessDay.plusDays(1);
        } while(closedDaysOfWeek.contains(DayOfWeek.from(nextBusinessDay))
                || closedSpecificDates.contains(nextBusinessDay));
        return nextBusinessDay;
    }


    public LocalDateTime calculateDeadline(long timeInterval, String startingDateTimeString)
        throws BusinessHourCalculatorException {

        if(timeInterval <= 0) throw new BusinessHourCalculatorException("timeInterval should be a positive number");

        LocalDateTime startingDateTime = LocalDateTime.parse(startingDateTimeString, DTF);
        DayOfWeek startingDayOfWeek = DayOfWeek.from(startingDateTime);
        LocalDate startingDate = LocalDate.from(startingDateTime);
        LocalTime startingTime = LocalTime.from(startingDateTime);

        //indien het starttijdstip op een sluitingsdag of na openingstijd valt, ga naar de volgende werkdag
        while(closedDaysOfWeek.contains(startingDayOfWeek)
                || closedSpecificDates.contains(startingDate)
                || startingTime.isAfter(getOpeningHoursForDate(startingDate).getClosingTime())){
           startingDate = getNextBusinessDay(startingDate);
           startingTime = getOpeningHoursForDate(startingDate).getOpeningTime();
           startingDateTime = startingDateTime.of(startingDate,startingTime);
           startingDayOfWeek = DayOfWeek.from(startingDate);
        }

        //indien het starttijdstip voor openingstijd valt, corrigeer starttijdstip naar openingstijd
        if(startingTime.isBefore(getOpeningHoursForDate(startingDate).getOpeningTime())) {
            startingTime = getOpeningHoursForDate(startingDate).getOpeningTime();
            startingDateTime = startingDateTime.of(startingDate, startingTime);
        }

        //volgende sluitingstijd als referentiepunt
        LocalTime nextClosingTime = getOpeningHoursForDate(startingDate).getClosingTime();
        LocalDateTime nextClosingDateTime = LocalDateTime.of(startingDate, nextClosingTime);

        if(startingDateTime.plusSeconds(timeInterval).isBefore(nextClosingDateTime)){
            //indien de job binnen de werkdag voltooid kan worden
            return startingDateTime.plusSeconds(timeInterval);
        }
        else {
            //indien de job pas op een volgende werkdag voltooid kan worden
            long remainingTimeInterval = timeInterval
                    - Duration.between(startingDateTime, nextClosingDateTime).getSeconds();

            LocalDate nextBusinessDay = getNextBusinessDay(startingDate);
            OpeningHours openingHoursNextBusinessDay = getOpeningHoursForDate(nextBusinessDay);

            while (openingHoursNextBusinessDay.getOpeningTime().plusSeconds(remainingTimeInterval)
                        .isAfter(openingHoursNextBusinessDay.getClosingTime())) {
                    nextBusinessDay = getNextBusinessDay(nextBusinessDay);
                    openingHoursNextBusinessDay = getOpeningHoursForDate(nextBusinessDay);
                    remainingTimeInterval -= openingHoursNextBusinessDay.secondsBetweenOpeningAndClosing();
            }
            return LocalDateTime.of(nextBusinessDay,openingHoursNextBusinessDay.getOpeningTime()
                    .plusSeconds(remainingTimeInterval));
        }
    }
}
