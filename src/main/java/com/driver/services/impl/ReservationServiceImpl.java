package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;

    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        //Reserve a spot in the given parkingLot such that the total price is minimum. Note that the price per hour for each spot is different
        //Note that the vehicle can only be parked in a spot having a type equal to or larger than given vehicle
        //If parkingLot is not found, user is not found, or no spot is available, throw "Cannot make reservation" exception.

        User user;
        ParkingLot parkingLot;
        Spot spot = null;

        String wheeler;
        if (numberOfWheels <= 2) {
            wheeler = "TWO_WHEELER";
        } else if (numberOfWheels <= 4) {
            wheeler = "FOUR_WHEELER";
        } else {
            wheeler = "OTHERS";
        }

        int totalCost = Integer.MAX_VALUE;
        boolean marker = true;

        try {
            user = userRepository3.findById(userId).get();
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
            for (Spot s : parkingLot.getSpotList()) {
                if (!s.getOccupied() && s.getPricePerHour() < totalCost && s.getSpotType().equals(SpotType.valueOf(wheeler))) {
                    totalCost = s.getPricePerHour();
                    spot = s;
                    marker = false;
                }
            }
            if (marker) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception("Cannot make reservation");
        }

        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);

        user.getReservationList().add(reservation);
        reservation.setUser(user);

        spot.setOccupied(true);
        spot.getReservationList().add(reservation);
        reservation.setSpot(spot);

        parkingLot.getSpotList().add(spot);

        reservationRepository3.save(reservation);

        return reservation;
    }
}
