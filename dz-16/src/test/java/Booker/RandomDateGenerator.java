package Booker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RandomDateGenerator {

    private final Random random = new Random();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Метод для отримання випадкових дат checkin та checkout і повернення об'єкта BookingDates
    public BookingDates generateRandomBookingDates() {
        LocalDate today = LocalDate.now();
        int randomCheckinDays = random.nextInt(30); // Випадкове число днів для checkin
        int randomCheckoutDays = randomCheckinDays + random.nextInt(7) + 1; // Checkout через 1-7 днів після checkin

        // Генерація випадкових дат
        LocalDate checkinDate = today.plusDays(randomCheckinDays);
        LocalDate checkoutDate = today.plusDays(randomCheckoutDays);

        // Форматування дат у формат yyyy-MM-dd
        String formattedCheckin = checkinDate.format(formatter);
        String formattedCheckout = checkoutDate.format(formatter);

        return BookingDates.builder()
                .checkin(formattedCheckin)  // Передаємо відформатований рядок
                .checkout(formattedCheckout)
                .build();
    }
}
