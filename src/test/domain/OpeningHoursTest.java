package test.domain;

import main.domain.OpeningHours;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class OpeningHoursTest {

    @Test
    public void aantalSecondenTussenNegenEnVijfUur(){
        OpeningHours openingHours = new OpeningHours(LocalTime.of(9,0), LocalTime.of(17,0));
        assertEquals(openingHours.secondsBetweenOpeningAndClosing(),8*60*60);
    }
}
