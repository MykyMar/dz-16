package Booker;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDates {
    private String checkin; // Формат String для використання рандомного підбору дат
    private String checkout;
}
