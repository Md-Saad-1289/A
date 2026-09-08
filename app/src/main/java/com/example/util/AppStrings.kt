package com.example.util

import com.example.model.AppLanguage

object AppStrings {
    fun t(key: String, lang: AppLanguage): String {
        val entry = translations[key] ?: return key
        return when (lang) {
            AppLanguage.BANGLA -> entry.second
            AppLanguage.ENGLISH -> entry.first
        }
    }

    private val translations = mapOf(
        "app_name" to Pair("TruckMate", "ট্রাকমেট"),
        "tagline" to Pair("Your Load Our Ride", "আপনার পণ্য আমাদের বাহন"),
        "splash_sub" to Pair("Book Trucks & Pickups for a Smoother Tomorrow", "সহজ পণ্য পরিবহনের জন্য আজই ট্রাক ও পিকআপ বুক করুন"),
        "onboarding_title_1" to Pair("Book Trucks & Pickups for a Smoother Tomorrow", "সহজ পণ্য পরিবহনের জন্য আজই ট্রাক ও পিকআপ বুক করুন"),
        "onboarding_desc_1" to Pair("Find reliable trucks and pickups nearby with instant rates and trusted drivers.", "মুহূর্তেই পান নির্ভরযোগ্য ট্রাক ও পিকআপ সাশ্রয়ী ভাড়ায় এবং বিশ্বস্ত ড্রাইভার।"),
        "onboarding_title_2" to Pair("Live Tracking & Fair Driver Bidding", "লাইভ ট্র্যাকিং ও সরাসরি ড্রাইভার বিডিং"),
        "onboarding_desc_2" to Pair("Negotiate directly with verified drivers or track your cargo journey in real-time.", "ড্রাইভারদের সরাসরি অফার থেকে সেরা ভাড়া বেছে নিন ও লাইভ ম্যাপে পণ্য ট্র্যাক করুন।"),
        "onboarding_title_3" to Pair("Safe Delivery & Seamless Payments", "নিরাপদ ডেলিভারি ও সহজ ডিজিটাল পেমেন্ট"),
        "onboarding_desc_3" to Pair("Move home or business cargo safely with bKash, Cards, or Cash on Delivery.", "বাসা বদল বা ব্যবসায়িক মালামাল পৌঁছান নিরাপদে, সাথে বিকাশ বা ক্যাশ অন ডেলিভারি সুবিধা।"),
        "skip" to Pair("Skip", "এড়িয়ে যান"),
        "get_started" to Pair("Get Started", "শুরু করুন"),
        "welcome_back" to Pair("Welcome Back", "স্বাগতম"),
        "enter_mobile" to Pair("Enter your mobile number to continue", "চালিয়ে যেতে আপনার মোবাইল নম্বর লিখুন"),
        "mobile_number" to Pair("Mobile number", "মোবাইল নম্বর"),
        "get_otp" to Pair("Get OTP", "ওটিপি পান"),
        "or" to Pair("OR", "অথবা"),
        "continue_google" to Pair("Continue with Google", "গুগল দিয়ে চালিয়ে যান"),
        "verify_otp" to Pair("Verify OTP", "ওটিপি যাচাই করুন"),
        "otp_code" to Pair("Enter 4-digit verification code", "৪ সংখ্যার যাচাইকরণ কোড লিখুন"),
        "resend_otp" to Pair("Resend OTP", "পুনরায় ওটিপি পাঠান"),
        "verify_proceed" to Pair("Verify & Proceed", "যাচাই করে এগিয়ে যান"),

        // Home
        "need_truck" to Pair("Need a Truck or Pickup?", "ট্রাক অথবা পিকআপ লাগবে?"),
        "book_minutes" to Pair("Book in minutes, move your goods", "মিনিটেই বুক করুন, পণ্য স্থানান্তর করুন"),
        "truck" to Pair("Truck", "ট্রাক"),
        "pickup" to Pair("Pickup", "পিকআপ"),
        "bidding" to Pair("Bidding", "বিডিং"),
        "fast_booking" to Pair("Fast Booking", "দ্রুত বুকিং"),
        "popular_vehicles" to Pair("Popular Vehicles", "জনপ্রিয় যানবাহন"),
        "see_all" to Pair("See all", "সব দেখুন"),
        "home" to Pair("Home", "হোম"),
        "bookings" to Pair("Bookings", "বুকিংস"),
        "wallet" to Pair("Wallet", "ওয়ালেট"),
        "profile" to Pair("Profile", "প্রোফাইল"),

        // Vehicle Selection
        "select_vehicle" to Pair("Select Vehicle", "যানবাহন নির্বাচন করুন"),
        "all" to Pair("All", "সব"),
        "covered_van" to Pair("Covered Van", "কাভার্ড ভ্যান"),
        "next" to Pair("Next", "পরবর্তী"),
        "capacity" to Pair("Capacity", "ধারণক্ষমতা"),
        "volume" to Pair("Volume", "আয়তন"),

        // Location
        "pickup_drop_location" to Pair("Pickup & Drop Location", "পিকআপ এবং ড্রপ লোকেশন"),
        "pickup_location" to Pair("Pickup Location", "পিকআপের স্থান"),
        "drop_location" to Pair("Drop Location", "গন্তব্য স্থান"),
        "select_map_search" to Pair("Select from map or search", "ম্যাপ বা সার্চ থেকে নির্বাচন করুন"),
        "search_location" to Pair("Search location...", "লোকেশন খুঁজুন..."),

        // Booking Details
        "booking_details" to Pair("Booking Details", "বুকিং বিবরণ"),
        "vehicle_type" to Pair("Vehicle Type", "যানবাহনের ধরণ"),
        "change" to Pair("Change", "পরিবর্তন"),
        "date_time" to Pair("Date & Time", "তারিখ ও সময়"),
        "goods_category" to Pair("Goods Category", "পণ্যের ধরণ"),
        "service_type" to Pair("Service Type", "সার্ভিসের ধরণ"),
        "open_pickup" to Pair("Open Pickup", "খোলা পিকআপ"),
        "house_moving" to Pair("House Moving", "বাসা বদল"),
        "special_instruction" to Pair("Special instruction (optional)", "বিশেষ নির্দেশনা (ঐচ্ছিক)"),
        "special_placeholder" to Pair("e.g. Handle with care", "যেমন: সাবধানে বহন করুন"),
        "get_fare_estimate" to Pair("Get Fare Estimate", "ভাড়ার আনুমানিক হিসাব দেখুন"),

        // Fare Estimate
        "fare_estimate" to Pair("Fare Estimate", "ভাড়ার হিসাব"),
        "estimated_fare" to Pair("Estimated Fare", "আনুমানিক ভাড়া"),
        "base_fare" to Pair("Base Fare", "বেস ভাড়া"),
        "per_km" to Pair("Per KM", "প্রতি কিমি"),
        "promo_discount" to Pair("Promo Discount", "প্রমো ছাড়"),
        "total" to Pair("Total", "সর্বমোট"),
        "fare_note" to Pair("Final fare may vary based on actual distance and traffic.", "আসল দূরত্ব এবং ট্রাফিকের ওপর ভিত্তি করে ভাড়ার কিছুটা পরিবর্তন হতে পারে।"),
        "book_now" to Pair("Book Now", "এখনই বুক করুন"),
        "try_bidding" to Pair("Try Bidding", "বিডিং করুন"),
        "promo_code" to Pair("Promo Code", "প্রমো কোড"),
        "apply" to Pair("Apply", "প্রয়োগ করুন"),
        "promo_applied" to Pair("Promo Applied!", "প্রমো কোড সফল হয়েছে!"),

        // Bidding
        "bidding_desc" to Pair("You'll receive bids from available drivers. Choose the best offer.", "কাছাকাছি ড্রাইভারদের থেকে অফার পাবেন। সেরা অফারটি গ্রহণ করুন।"),
        "select" to Pair("Select", "গ্রহণ করুন"),
        "suggested_range" to Pair("Suggested Fare Range", "পরামর্শিত ভাড়ার সীমা"),
        "min_away" to Pair("min away", "মিনিট দূরে"),
        "trips" to Pair("trips", "ট্রিপ"),

        // Live Tracking
        "driver_assigned" to Pair("Driver Assigned", "ড্রাইভার নির্ধারিত"),
        "driver_on_way" to Pair("Your driver is on the way!", "আপনার ড্রাইভার পথে আছে!"),
        "call" to Pair("Call", "কল"),
        "chat" to Pair("Chat", "চ্যাট"),
        "cancel" to Pair("Cancel", "বাতিল"),
        "arriving_in" to Pair("Arriving in", "পৌঁছাবে"),
        "distance" to Pair("Distance", "দূরত্ব"),
        "status_searching" to Pair("Searching", "খোঁজা হচ্ছে"),
        "status_assigned" to Pair("Assigned", "নির্ধারিত"),
        "status_arriving" to Pair("Arriving", "আসছে"),
        "status_in_transit" to Pair("In Transit", "চলমান"),
        "status_completed" to Pair("Completed", "সম্পন্ন"),
        "simulate_next" to Pair("Simulate Trip Progress", "ট্রিপ প্রগ্রেস সিমুলেট করুন"),

        // Trip Completed
        "trip_completed" to Pair("Trip Completed!", "ট্রিপ সম্পন্ন হয়েছে!"),
        "thank_you" to Pair("Thank you for using TruckMate", "ট্রাকমেট ব্যবহারের জন্য ধন্যবাদ"),
        "driver" to Pair("Driver", "ড্রাইভার"),
        "vehicle" to Pair("Vehicle", "যানবাহন"),
        "trip_fare" to Pair("Trip Fare", "ট্রিপ ভাড়া"),
        "payment_method" to Pair("Payment Method", "পরিশোধের মাধ্যম"),
        "cash_on_delivery" to Pair("Cash on Delivery", "ক্যাশ অন ডেলিভারি"),
        "view_receipt" to Pair("View Receipt", "রসিদ দেখুন"),
        "rate_driver" to Pair("Rate Driver", "ড্রাইভারকে রেটিং দিন"),
        "submit_rating" to Pair("Submit Feedback", "মতামত জমা দিন"),
        "how_was_trip" to Pair("How was your trip with", "আপনার ট্রিপ কেমন ছিল"),

        // History
        "trip_history" to Pair("Trip History", "ট্রিপের ইতিহাস"),
        "completed_badge" to Pair("Completed", "সম্পন্ন"),
        "cancelled_badge" to Pair("Cancelled", "বাতিল"),
        "rebook" to Pair("Rebook", "পুনরায় বুক করুন"),

        // Wallet
        "available_balance" to Pair("Available Balance", "বর্তমান ব্যালেন্স"),
        "add_money" to Pair("Add Money", "টাকা যোগ করুন"),
        "transactions" to Pair("Transactions", "লেনদেন"),
        "payment_methods" to Pair("Payment Methods", "পরিশোধের মাধ্যম"),
        "bkash" to Pair("bKash", "বিকাশ"),
        "nagad" to Pair("Nagad", "নগদ"),
        "card" to Pair("Credit / Debit Card", "ক্রেডিট / ডেবিট কার্ড"),

        // Saved Addresses
        "saved_addresses" to Pair("Saved Addresses", "সংরক্ষিত ঠিকানা"),
        "add_new_address" to Pair("Add New Address", "নতুন ঠিকানা যোগ করুন"),
        "home_title" to Pair("Home", "বাসা"),
        "office_title" to Pair("Office", "অফিস"),
        "warehouse_title" to Pair("Warehouse", "গুদাম"),

        // Profile & Settings
        "settings" to Pair("Settings", "সেটিংস"),
        "language" to Pair("Language", "ভাষা"),
        "dark_mode" to Pair("Dark Mode", "ডার্ক মোড"),
        "light_mode" to Pair("Light Mode", "লাইট মোড"),
        "help_support" to Pair("Help & Support", "সাহায্য ও সাপোর্ট"),
        "notifications" to Pair("Notifications", "বিজ্ঞপ্তি"),
        "logout" to Pair("Logout", "লগআউট"),

        // Home Enhancements
        "fare_calculator" to Pair("Instant Fare Calculator", "তাৎক্ষণিক ভাড়া পরিমাপক"),
        "quick_routes" to Pair("Popular Quick Routes", "জনপ্রিয় রুটসমূহ"),
        "special_offers" to Pair("Special Offers & Promos", "বিশেষ অফার ও প্রোমো"),
        "logistics_solutions" to Pair("Solutions by Need", "আপনার প্রয়োজন অনুযায়ী সেবা"),
        "home_shifting" to Pair("Home Shifting", "বাসা বদল"),
        "home_shifting_sub" to Pair("With verified helpers & safety", "অভিজ্ঞ লোডিং লেবার ও যত্নশীল সেবা"),
        "commercial_cargo" to Pair("Business Cargo", "বাণিজ্যিক মালামাল"),
        "commercial_cargo_sub" to Pair("Factory & wholesale delivery", "ফ্যাক্টরি ও পাইকারি মালামাল পরিবহন"),
        "construction_materials" to Pair("Construction Cargo", "নির্মাণ সামগ্রী"),
        "construction_materials_sub" to Pair("Rods, cement & tiles delivery", "রড, সিমেন্ট ও টাইলস পরিবহন"),
        "express_parcel" to Pair("Express Goods", "জরুরি ডেলিভারি"),
        "express_parcel_sub" to Pair("Fast pickup under 1 hour", "১ ঘণ্টার মধ্যে দ্রুত পরিবহন"),
        "why_truckmate" to Pair("Why Choose TruckMate?", "কেন ট্রাকমেট বেছে নেবেন?"),
        "verified_drivers" to Pair("100% Verified Drivers", "১০০% ভেরিফাইড ড্রাইভার"),
        "verified_drivers_sub" to Pair("NID & license verified", "জাতীয় পরিচয়পত্র ও লাইসেন্স পরীক্ষিত"),
        "live_gps" to Pair("Live GPS Tracking", "লাইভ গুগল ম্যাপ ট্র্যাকিং"),
        "live_gps_sub" to Pair("Track turn-by-turn live", "লাইভ ম্যাপে পণ্য মনিটর করুন"),
        "direct_bidding" to Pair("Fair Driver Bidding", "সরাসরি ড্রাইভার বিডিং"),
        "direct_bidding_sub" to Pair("Get the best negotiable rates", "মধ্যস্বত্বভোগী ছাড়া সাশ্রয়ী ভাড়া"),
        "support_24_7" to Pair("24/7 Helpline & Support", "২৪/৭ জরুরি হেল্পলাইন"),
        "support_24_7_sub" to Pair("Direct assistance anytime", "যেকোনো সহায়তায় কল করুন"),
        "fleet_status" to Pair("Live Dhaka Logistics Stand", "লাইভ তেজগাঁও ট্রাক স্ট্যান্ড আপডেট"),
        "trucks_ready" to Pair("84+ Trucks & Pickups Ready Nearby", "৮৪+ ট্রাক ও পিকআপ এখন প্রস্তুত"),
        "avg_arrival" to Pair("Avg. Arrival: 8-12 min", "গড় পৌঁছানোর সময়: ৮-১২ মিনিট"),
        "call_helpline" to Pair("Call Helpline (16247)", "কল করুন হেল্পলাইনে (১৬২৪৭)"),
        "whatsapp_support" to Pair("WhatsApp Support", "হোয়াটসঅ্যাপ সাপোর্ট")
    )
}
