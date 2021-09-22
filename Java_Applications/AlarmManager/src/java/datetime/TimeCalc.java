/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datetime;

import java.time.LocalDateTime;

/**
 *
 * @author Kosta
 */
public class TimeCalc {
    public static boolean isSameTime(LocalDateTime ldt, LocalDateTime rdt) {
        if (rdt.getYear() == ldt.getYear() && rdt.getMonth() == ldt.getMonth()
                && rdt.getDayOfMonth() == ldt.getDayOfMonth()
                && rdt.getHour() == ldt.getHour()
                && rdt.getMinute() == ldt.getMinute()) {
            return true;
        }
        return false;
    }
}
