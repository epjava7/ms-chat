// package com.example.hotelchatbot;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;
// import org.springframework.beans.factory.annotation.Autowired;
// import com.example.hotelchatbot.service.FaqService;

// @Component
// public class HotelFaqDataLoader implements CommandLineRunner {

//     @Autowired
//     private FaqService faqService;

//     // some todos
//     // can also add rest endpoints for FAQ entry management

//     @Override
//     public void run(String... args) {
//         faqService.addFaq("What time is check-in?", "Check-in starts at 2 PM.");
//         faqService.addFaq("Is breakfast included?", "Yes, complimentary breakfast is included.");
//         faqService.addFaq("Do you allow pets?", "No, we do not allow pets in the hotel.");
//         faqService.addFaq("What is the cancellation policy?", "You can cancel your booking up to 24 hours before check-in without a fee.");
//         faqService.addFaq("Do you have a gym?", "Yes, we have a fully equipped gym available for guests.");
//         faqService.addFaq("What is the Wi-Fi policy?", "Free Wi-Fi is available throughout the hotel.");
//         faqService.addFaq("Is parking available?", "Yes, we offer free parking for all guests.");
//         faqService.addFaq("What time do I need to check-out by?", "Check-out ends at 11 AM.");
//         faqService.addFaq("Do you have a pool?", "Yes, we have an indoor swimming pool open from 6 AM to 10 PM.");
//         faqService.addFaq("Can I request a late check-out?", "Late check-out can be arranged based on availability, please contact the front desk.");
//         faqService.addFaq("Do you offer room service?", "Yes, room service is available from 6 AM to 11 PM.");
//         faqService.addFaq("What amenities are available in the room?", "Rooms are equipped with a TV, mini-fridge, safe, and coffee maker.");
//         faqService.addFaq("Is there a business center?", "Yes, we have a business center with computers and printers available for guests.");
//         faqService.addFaq("Do you have a shuttle service?", "Yes, we offer a free shuttle service to the airport every hour.");
//     }
// }