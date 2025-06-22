package com.example.hotelchatbot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.hotelchatbot.service.AmenitiesService;
import com.example.hotelchatbot.service.FaqService;
import com.example.hotelchatbot.service.HotelRoomService;
import com.example.hotelchatbot.service.HotelService;
import com.example.hotelchatbot.service.RoomTypeService;
import com.example.hotelchatbot.domain.Amenities;
import com.example.hotelchatbot.domain.Hotel;
import com.example.hotelchatbot.domain.HotelRoom;
import com.example.hotelchatbot.domain.RoomType;

@Component
public class HotelDataLoader implements CommandLineRunner {

    @Autowired
    private AmenitiesService amenitiesService;

    @Autowired
    private RoomTypeService roomTypeService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private FaqService faqService;

    @Autowired
    private HotelRoomService hotelRoomService;

    @Override
    public void run(String... args) {

        Amenities wifi = new Amenities();
        wifi.setName("Free Wi-Fi");
        amenitiesService.saveAmenity(wifi);

        Amenities tv = new Amenities();
        tv.setName("TV");
        amenitiesService.saveAmenity(tv);

        Amenities minibar = new Amenities();
        minibar.setName("Mini-bar");
        amenitiesService.saveAmenity(minibar);

        Amenities safe = new Amenities();
        safe.setName("Room Service");
        amenitiesService.saveAmenity(safe);

        Amenities coffeeMaker = new Amenities();
        coffeeMaker.setName("Coffee Maker");
        amenitiesService.saveAmenity(coffeeMaker);

        Amenities poolAccess = new Amenities();
        poolAccess.setName("Pool Access");
        amenitiesService.saveAmenity(poolAccess);

        Amenities freeBreakfast = new Amenities();
        freeBreakfast.setName("Free Breakfast");
        amenitiesService.saveAmenity(freeBreakfast);

        // room type
        RoomType singleRoom = new RoomType();
        singleRoom.setName("Single Room");
        roomTypeService.saveRoomType(singleRoom);

        RoomType doubleRoom = new RoomType();
        doubleRoom.setName("Double Room");
        roomTypeService.saveRoomType(doubleRoom);

        RoomType luxuryRoom = new RoomType();
        luxuryRoom.setName("Luxury Room");
        roomTypeService.saveRoomType(luxuryRoom);

        RoomType familySuite = new RoomType();
        familySuite.setName("Family Suite");
        roomTypeService.saveRoomType(familySuite);

        // hotel 1
        Set<Amenities> warwickHotelAmenities = new HashSet<>();
        warwickHotelAmenities.add(wifi);
        warwickHotelAmenities.add(tv);
        warwickHotelAmenities.add(minibar);
        warwickHotelAmenities.add(poolAccess);
        warwickHotelAmenities.add(freeBreakfast);

        Set<Amenities> warwickRoomAmenities = new HashSet<>();
        warwickRoomAmenities.add(wifi);
        warwickRoomAmenities.add(tv);
        warwickRoomAmenities.add(safe);
        warwickRoomAmenities.add(coffeeMaker);
        warwickRoomAmenities.add(poolAccess);
        warwickRoomAmenities.add(freeBreakfast);

        HotelRoom warwickRoom1 = new HotelRoom();
        warwickRoom1.setType(singleRoom);
        warwickRoom1.setNumRooms(10);
        warwickRoom1.setPrice(200);
        warwickRoom1.setDiscount(10);
        warwickRoom1.setDescription("Single Room with city view, single bed, and modern amenities.");
        warwickRoom1.setPolicies("No smoking. No pets.");
        warwickRoom1.setAmenities(warwickRoomAmenities);
        hotelRoomService.saveHotelRoom(warwickRoom1);

        HotelRoom warwickRoom2 = new HotelRoom();
        warwickRoom2.setType(familySuite);
        warwickRoom2.setNumRooms(5);
        warwickRoom2.setPrice(350);
        warwickRoom2.setDiscount(15);
        warwickRoom2.setDescription("Family Suite with separate living area, premium amenities.");
        warwickRoom2.setPolicies("No smoking. No pets.");
        warwickRoom2.setAmenities(warwickRoomAmenities);
        hotelRoomService.saveHotelRoom(warwickRoom2);

        Set<HotelRoom> warwickRooms = new HashSet<>();
        warwickRooms.add(warwickRoom1);
        warwickRooms.add(warwickRoom2);

        Hotel hotel = new Hotel();
        hotel.setName("Warwick San Francisco");
        hotel.setAddress("490 Geary St");
        hotel.setCity("San Francisco");
        hotel.setState("California");
        hotel.setStarRating(4);
        hotel.setAveragePrice(150);
        hotel.setAmenities(warwickHotelAmenities);
        hotel.setDiscount(30.0);
        hotel.setDescription("Where History Meets the Moment Warwick San Francisco hotel is a captivating reflection of the city in which it resides, striking a perfect balance between authentic history and modern hospitality. Ideally located just steps from bustling Union Square, the Theatre District, and its world-class shopping and landmark attractions, this luxury boutique hotel is a residential-style hideaway in downtown San Francisco.");
        hotel.setEmail("res.sf@warwickhotels.com");
        hotel.setPhoneNumber("415-928-7900");
        hotel.setImageUrl("https://x.cdrst.com/foto/hotel-sf/9b82/granderesp/foto-hotel-122db701.jpg");
        hotel.setTimesBooked(100);
        hotel.setRooms(warwickRooms);
        hotelService.saveHotel(hotel);

        // hotel 2
        Set<Amenities> mgmHotelAmenities = new HashSet<>();
        mgmHotelAmenities.add(wifi);
        mgmHotelAmenities.add(tv);
        mgmHotelAmenities.add(minibar);
        mgmHotelAmenities.add(poolAccess);
        mgmHotelAmenities.add(freeBreakfast);

        Set<Amenities> mgmRoomAmenities = new HashSet<>();
        mgmRoomAmenities.add(wifi);
        mgmRoomAmenities.add(tv);
        mgmRoomAmenities.add(safe);
        mgmRoomAmenities.add(poolAccess);
        mgmRoomAmenities.add(freeBreakfast);

        HotelRoom mgmRoom1 = new HotelRoom();
        mgmRoom1.setType(doubleRoom);
        mgmRoom1.setNumRooms(20);
        mgmRoom1.setPrice(220);
        mgmRoom1.setDiscount(12);
        mgmRoom1.setDescription("Double Room with Strip view, two beds, and luxury amenities.");
        mgmRoom1.setPolicies("No smoking. No pets.");
        mgmRoom1.setAmenities(mgmRoomAmenities);
        hotelRoomService.saveHotelRoom(mgmRoom1);

        HotelRoom mgmRoom2 = new HotelRoom();
        mgmRoom2.setType(luxuryRoom);
        mgmRoom2.setNumRooms(8);
        mgmRoom2.setPrice(400);
        mgmRoom2.setDiscount(18);
        mgmRoom2.setDescription("Luxury Room with panoramic views, living area, and exclusive services.");
        mgmRoom2.setPolicies("No smoking. No pets.");
        mgmRoom2.setAmenities(mgmRoomAmenities);
        hotelRoomService.saveHotelRoom(mgmRoom2);

        Set<HotelRoom> mgmRooms = new HashSet<>();
        mgmRooms.add(mgmRoom1);
        mgmRooms.add(mgmRoom2);

        Hotel hotel2 = new Hotel();
        hotel2.setName("MGM Grand Hotel & Casino");
        hotel2.setAddress("3799 Las Vegas Blvd S.");
        hotel2.setCity("Las Vegas");
        hotel2.setState("Nevada");
        hotel2.setStarRating(4);
        hotel2.setAveragePrice(150);
        hotel2.setAmenities(mgmHotelAmenities);
        hotel2.setDiscount(20.0);
        hotel2.setDescription("MGM Grand Las Vegas is The Entertainment Authority—renowned for its star-studded events at the MGM Grand Garden Arena, spectacular entertainment like KÀ™ by Cirque du Soleil, world class dining featuring celebrity chefs and unbeatable nightlife.");
        hotel2.setEmail("mgmlobbyconcierge@lv.mgmgrand.com");
        hotel2.setPhoneNumber("877-880-0880");
        hotel2.setImageUrl("https://cf.bstatic.com/xdata/images/hotel/max1024x768/561245406.jpg?k=5cd2120ed20e80cb72f52cdc30c5483f9a4ef0c3e0289641de7029a9f0cb4556&o=");
        hotel2.setTimesBooked(100);
        hotel2.setRooms(mgmRooms);
        hotelService.saveHotel(hotel2);

    hotelService.saveHotel(hotel);

    // faqs for hotel one
    faqService.addFaq("What time is check-in?", "Check-in starts at 2 PM.", hotel);
    faqService.addFaq("Is breakfast included?", "Yes, complimentary breakfast is included.", hotel);
    faqService.addFaq("Do you allow pets?", "No, we do not allow pets in the hotel.", hotel);
    faqService.addFaq("What is the cancellation policy?", "You can cancel your booking up to 24 hours before check-in without a fee.", hotel);
    faqService.addFaq("Do you have a gym?", "Yes, we have a fully equipped gym available for guests.", hotel);
    faqService.addFaq("What is the Wi-Fi policy?", "Free Wi-Fi is available throughout the hotel.", hotel);
    faqService.addFaq("Is parking available?", "Yes, we offer free parking for all guests.", hotel);
    faqService.addFaq("What time do I need to check-out by?", "Check-out ends at 11 AM.", hotel);
    faqService.addFaq("Do you have a pool?", "Yes, we have an indoor swimming pool open from 6 AM to 10 PM.", hotel);
    faqService.addFaq("Can I request a late check-out?", "Late check-out can be arranged based on availability, please contact the front desk.", hotel);
    faqService.addFaq("Do you offer room service?", "Yes, room service is available from 6 AM to 11 PM.", hotel);
    faqService.addFaq("What amenities are available in the room?", "Rooms are equipped with a TV, mini-fridge, safe, and coffee maker.", hotel);
    faqService.addFaq("Is there a business center?", "Yes, we have a business center with computers and printers available for guests.", hotel);
    faqService.addFaq("Do you have a shuttle service?", "Yes, we offer a free shuttle service to the airport every hour.", hotel);

    hotelService.saveHotel(hotel2);

    // faqs for MGM Grand Hotel & Casino
    faqService.addFaq("What time is check-in?", "Check-in starts at 2 PM.", hotel2);
    faqService.addFaq("Is breakfast included?", "Yes, complimentary breakfast is included.", hotel2);
    faqService.addFaq("Do you allow pets?", "No, we do not allow pets in the hotel.", hotel2);
    faqService.addFaq("What is the cancellation policy?", "You can cancel your booking up to 24 hours before check-in without a fee.", hotel2);
    faqService.addFaq("Do you have a gym?", "Yes, we have a fully equipped gym available for guests.", hotel2);
    faqService.addFaq("What is the Wi-Fi policy?", "Free Wi-Fi is available throughout the hotel.", hotel2);
    faqService.addFaq("Is parking available?", "Yes, we offer free parking for all guests.", hotel2);
    faqService.addFaq("What time do I need to check-out by?", "Check-out ends at 11 AM.", hotel2);
    faqService.addFaq("Do you have a pool?", "Yes, we have an indoor swimming pool open from 6 AM to 10 PM.", hotel2);
    faqService.addFaq("Can I request a late check-out?", "Late check-out can be arranged based on availability, please contact the front desk.", hotel2);
    faqService.addFaq("Do you offer room service?", "Yes, room service is available from 6 AM to 11 PM.", hotel2);
    faqService.addFaq("What amenities are available in the room?", "Rooms are equipped with a TV, mini-fridge, safe, and coffee maker.", hotel2);
    faqService.addFaq("Is there a business center?", "Yes, we have a business center with computers and printers available for guests.", hotel2);
    faqService.addFaq("Do you have a shuttle service?", "Yes, we offer a free shuttle service to the airport every hour.", hotel2);

    }
}