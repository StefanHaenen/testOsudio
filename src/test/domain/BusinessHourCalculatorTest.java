package test.domain;

import main.domain.BusinessHourCalculator;
import main.domain.OpeningHours;

import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class BusinessHourCalculatorTest {
    private BusinessHourCalculator businessHourCalculator;

    @Before
    public void before()  {
        businessHourCalculator = new BusinessHourCalculator("09:00", "15:00");
        businessHourCalculator.setOpeningHours(DayOfWeek.FRIDAY, "10:00", "17:00");
        businessHourCalculator.setClosed(DayOfWeek.SUNDAY, DayOfWeek.WEDNESDAY);

    }

    @Test
    public void testGetNextBusinessDay()  {
        assertEquals(businessHourCalculator.getNextBusinessDay(LocalDate.of(2019,8,10)),
                LocalDate.of(2019,8,12));

    }

    @Test
    public void calculateDeadlineTestGegevenVoorbeeld1(){
        assertEquals(businessHourCalculator.calculateDeadline(2*60*60, "2010-06-07 09:10"),
                LocalDateTime.of(2010, 06, 07, 11, 10, 0));
    }

    @Test
    public void calculateDeadlineTestGegevenVoorbeeld2(){
        assertEquals(businessHourCalculator.calculateDeadline(15*60, "2010-06-08 14:48"),
                LocalDateTime.of(2010, 06, 10, 9, 3, 0));
    }

    @Test
    public void calculateDeadlineTGestegevenVoorbeeld3(){
        businessHourCalculator.setOpeningHours("2010-12-24", "08:00", "13:00");
        businessHourCalculator.setClosed("2010-12-25");
        assertEquals(businessHourCalculator.calculateDeadline(7*60*60, "2010-12-24 6:45"),
                LocalDateTime.of(2010, 12, 27, 11, 0, 0));
    }

    @Test
    public void calculateDeadlineTestExtraVoorbeeld1(){
        businessHourCalculator.setClosed("2019-08-15","2019-08-16");
        businessHourCalculator.setOpeningHours("2019-08-17", "10:00", "13:00");
        assertEquals(businessHourCalculator.calculateDeadline((12*60+42)*60,"2019-08-14 21:00"),
                LocalDateTime.of(2019, 8, 20, 12, 42, 0));
    }

}
