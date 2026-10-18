package library.subsystem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Calculates the fine for a late return. */
public class FineCalculator {
    public static final double FINE_PER_DAY = 0.50;

    public double calculate(Loan loan, LocalDate returnedOn) {
        long lateDays = ChronoUnit.DAYS.between(loan.getDueOn(), returnedOn);
        return lateDays > 0 ? lateDays * FINE_PER_DAY : 0.0;
    }
}
