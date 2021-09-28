package com.parkit.parkingsystem.integration;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private String REGISTRATION_NUMBER = "ABCDEF";

    @Mock
    private static InputReaderUtil inputReaderUtil;
    
    @Mock
    private static Ticket ticket;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(REGISTRATION_NUMBER);
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
    	
        when(inputReaderUtil.readSelection()).thenReturn(1);
        
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
       
        Ticket ticket = ticketDAO.getTicket(REGISTRATION_NUMBER);
    	assertNotEquals(ticket, null);
    	assertEquals(ticket.getVehicleRegNumber(), REGISTRATION_NUMBER);
    	assertEquals(ticket.getParkingSpot().isAvailable(), false);
        
    }

    @Test
    public void testParkingLotExit(){
        
        Date date = DateUtils.addMinutes(new Date(), -15);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(new ParkingSpot(1,ParkingType.CAR, true));
        ticket.setVehicleRegNumber(REGISTRATION_NUMBER);
        ticket.setPrice(0);
        ticket.setInTime(date);
        ticket.setOutTime(null);
        ticketDAO.saveTicket(ticket); 
        
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        
    	Ticket outTicket = ticketDAO.getTicket(REGISTRATION_NUMBER);
    	assertNotEquals(outTicket, null);
    	//Price should be free because the execution of the test is less than 30 minutes
    	assertEquals(outTicket.getPrice(), 0);
    	//Check if out time is null
    	assertNotNull(outTicket.getOutTime());
    }
}
