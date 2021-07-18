package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inTimestamp = ticket.getInTime().getTime();
        long outTimestamp = ticket.getOutTime().getTime();

        //We get the time elapsed in milliseconds by making the difference between inTime and outTimestamp
        //Then we convert the elapsed time in hour
        double duration = (((outTimestamp - inTimestamp)/1000)/60)/60.0f;
        
        if(duration < 0.5) {
        	duration = 0;
        }
       

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
        
        //Check if we have to apply the discount
        if(ticket.isEligibleForDiscount()) {
			double discount = ticket.getPrice() * 0.05;
        	ticket.setPrice(ticket.getPrice() - discount);
		}

    }
}