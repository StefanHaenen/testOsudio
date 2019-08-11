import main.domain.BusinessHourCalculator;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
        BusinessHourCalculator businessHourCalculator = new BusinessHourCalculator("09:00","15:00");
        businessHourCalculator.setOpeningHours(DayOfWeek.FRIDAY, "10:00", "17:00");
        businessHourCalculator.setOpeningHours("2010-12-24", "08:00", "13:00");
        businessHourCalculator.setClosed(DayOfWeek.SUNDAY, DayOfWeek.WEDNESDAY, DayOfWeek.WEDNESDAY);
        businessHourCalculator.setClosed("2010-12-25");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("E MMMM d HH:mm:ss yyyy");

        //gegeven voorbeelden:
        System.out.println("example #1:");
        System.out.println(businessHourCalculator.calculateDeadline(2*60*60, "2010-06-07 09:10").format(dtf));
        // => Mon Jun 07 11:10:00 2010

        System.out.println("example #2:");
        System.out.println(businessHourCalculator.calculateDeadline(15*60, "2010-06-08 14:48").format(dtf));
        // => Thu Jun 10 09:03:00 2010

        System.out.println("example #3:");
        System.out.println(businessHourCalculator.calculateDeadline(7*60*60, "2010-12-24 6:45").format(dtf));
        // => Mon Dec 27 11:00:00 2010

        //extra voorbeeld: binnenlevering na sluitingstijd, extra lange duurtijd;
        System.out.println("example #4:");
        businessHourCalculator.setClosed("2019-08-15","2019-08-16");
        businessHourCalculator.setOpeningHours("2019-08-17", "10:00", "13:00");
        System.out.println(businessHourCalculator.calculateDeadline(12*60*60,"2019-08-14 21:00").format(dtf));
    }
}
