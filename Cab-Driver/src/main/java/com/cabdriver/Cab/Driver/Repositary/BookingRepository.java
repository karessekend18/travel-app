package com.cabdriver.Cab.Driver.Repositary;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cabdriver.Cab.Driver.models.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

//@Repository
//public interface BookingRepository extends JpaRepository<Booking, Integer>{

//    @Query(value = "select *from booking where status =:sate", nativeQuery = true)
//    public List<Booking> getBookingByStatus(String state);


//}
@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query(value = "SELECT * FROM booking WHERE status = :state", nativeQuery = true)
    List<Booking> getBookingByStatus(@RequestParam ("state") String state);
}

