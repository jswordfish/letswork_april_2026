package com.letswork.crm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.letswork.crm.entities.Seat;
import com.letswork.crm.entities.UserSeatMapping;
import com.letswork.crm.enums.SeatType;
import com.letswork.crm.repo.SeatRepository;
import com.letswork.crm.repo.UserSeatMappingRepository;
import com.letswork.crm.service.SeatService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SeatServiceIntegrationTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserSeatMappingRepository userSeatMappingRepository;

    @Autowired
    private SeatService seatService;

    private final String companyId = "LW";
    private final String location = "Mulund";

    @BeforeEach
    void setup() {
        
        userSeatMappingRepository.deleteAll();
        seatRepository.deleteAll();

        
        seatRepository.save(Seat.builder()
                .companyId(companyId)
                .location(location)
                .seatType(SeatType.DEDICATED)
                .seatNumber(1)
                .costPerDay(500)
                .costPerMonth(8000)
                .build());

        seatRepository.save(Seat.builder()
                .companyId(companyId)
                .location(location)
                .seatType(SeatType.DEDICATED)
                .seatNumber(2)
                .costPerDay(600)
                .costPerMonth(9000)
                .build());

        seatRepository.save(Seat.builder()
                .companyId(companyId)
                .location(location)
                .seatType(SeatType.FLEXI)
                .seatNumber(1)
                .costPerDay(300)
                .costPerMonth(5000)
                .build());
    }

    @Test
    void testSaveSeatSuccessfully() {
        Seat newSeat = Seat.builder()
                .companyId(companyId)
                .location(location)
                .seatType(SeatType.SHARED_CABIN)
                .seatNumber(1)
                .costPerDay(400)
                .costPerMonth(7000)
                .build();

        Seat saved = seatRepository.save(newSeat);

        assertNotNull(saved.getId());
        assertEquals(SeatType.SHARED_CABIN, saved.getSeatType());
    }

    @Test
    void testPreventDuplicateSeatNumberPerType() {
        Seat duplicate = Seat.builder()
                .companyId(companyId)
                .location(location)
                .seatType(SeatType.DEDICATED)
                .seatNumber(1)
                .costPerDay(550)
                .costPerMonth(8200)
                .build();

        Seat updated = seatService.saveOrUpdate(duplicate);

        assertEquals(550, updated.getCostPerDay());
        assertEquals(8200, updated.getCostPerMonth());
    }

    @Test
    void testGetTotalSeats() {
        long totalDedicatedSeats = seatService.getTotalSeats(companyId, location, SeatType.DEDICATED);
        long totalFlexiSeats = seatService.getTotalSeats(companyId, location, SeatType.FLEXI);

        assertEquals(2, totalDedicatedSeats);
        assertEquals(1, totalFlexiSeats);
    }

    @Test
    void testGetAvailableSeats_NoUsers() {
        long available = seatService.getAvailableSeats(companyId, location, SeatType.DEDICATED);
        assertEquals(2, available);
    }

    @Test
    void testGetAvailableSeats_WithUsers() {
        userSeatMappingRepository.save(UserSeatMapping.builder()
                .companyId(companyId)
                .location(location)
                .seatType(SeatType.DEDICATED)
                .seatNumber(1)
                .email("user1@test.com")
                .numberOfDays(5)
                .build());

        long available = seatService.getAvailableSeats(companyId, location, SeatType.DEDICATED);
        assertEquals(1, available); 
    }

    @Test
    void testGetAvailableSeats_AllOccupied() {
        userSeatMappingRepository.saveAll(List.of(
                UserSeatMapping.builder()
                        .companyId(companyId)
                        .location(location)
                        .seatType(SeatType.DEDICATED)
                        .seatNumber(1)
                        .email("u1@test.com")
                        .numberOfDays(3)
                        .build(),
                UserSeatMapping.builder()
                        .companyId(companyId)
                        .location(location)
                        .seatType(SeatType.DEDICATED)
                        .seatNumber(2)
                        .email("u2@test.com")
                        .numberOfDays(2)
                        .build()
        ));

        long available = seatService.getAvailableSeats(companyId, location, SeatType.DEDICATED);
        assertEquals(0, available);
    }

    @Test
    void testGetAvailableSeats_InvalidLocation() {
        long available = seatService.getAvailableSeats(companyId, "Pune", SeatType.DEDICATED);
        assertEquals(0, available);
    }

    @Test
    void testGetAvailableSeats_InvalidCompany() {
        long available = seatService.getAvailableSeats("INVALID", location, SeatType.DEDICATED);
        assertEquals(0, available);
    }
}
