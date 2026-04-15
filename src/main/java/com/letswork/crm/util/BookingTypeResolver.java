package com.letswork.crm.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.DiscriminatorValue;

import org.springframework.stereotype.Component;

import com.letswork.crm.entities.Booking;
import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;
import com.letswork.crm.entities.DayPassBookingDirect;
import com.letswork.crm.entities.DayPassBookingThroughBundle;
import com.letswork.crm.entities.DayPassBundleBooking;

@Component
public class BookingTypeResolver {

    private final Map<String, Class<? extends Booking>> bookingTypeMap = new HashMap<>();

    @PostConstruct
    public void init() {
        register(ConferenceBookingDirect.class);
        register(ConferenceBundleBooking.class);
        register(ConferenceRoomBookingThroughBundle.class);
        register(DayPassBundleBooking.class);
        register(DayPassBookingThroughBundle.class);
        register(DayPassBookingDirect.class);
    }

    private void register(Class<? extends Booking> clazz) {
        DiscriminatorValue annotation = clazz.getAnnotation(DiscriminatorValue.class);

        if (annotation != null) {
            bookingTypeMap.put(annotation.value(), clazz);
        }
    }

    public Class<? extends Booking> resolve(String bookingType) {
        return bookingTypeMap.get(bookingType);
    }
}
