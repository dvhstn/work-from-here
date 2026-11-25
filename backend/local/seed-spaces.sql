-- Local development seed: real places to work from in Belfast and Dublin (researched November 2026).
-- Wi-Fi values are placeholders, NOT real passwords: 'ask-staff' means a source says Wi-Fi is offered; NULL means none or unknown.
-- Details may go out of date. Check each place before relying on it.
-- Safe to re-run: names that already exist (case-insensitive) are skipped.
-- Usage: docker exec -i <postgres-container> psql -U <user> -d <db> < backend/local/seed-spaces.sql

INSERT INTO spaces (name, description, type, wifi_password) VALUES
-- ===================== BELFAST =====================
-- CAFE
-- https://www.poyst.com/business/established-coffee
('Established Coffee', 'Specialty coffee shop on Hill Street in the Cathedral Quarter. Listed with free Wi-Fi, large tables and plugs, which suits laptop work.', 'CAFE', 'ask-staff'),
-- https://www.opentable.co.uk/r/general-merchants-belfast
('General Merchants (Ormeau Road)', 'Neighbourhood cafe at 361 Ormeau Road, on the corner of Deramore Avenue, south Belfast. Brunch and coffee spot near Botanic Gardens and Ormeau Park.', 'CAFE', NULL),
-- https://kaffeo.coffee/locations
('Kaffe O (Botanic Avenue)', 'Danish-inspired coffee house at 73 Botanic Avenue in the Queen''s Quarter, close to the university and Botanic Gardens.', 'CAFE', NULL),
-- https://hellorayo.co.uk/cool-fm/lifestyle/food-drink/neighbourhood-cafe-belfast-donegall-street
('Neighbourhood Cafe', 'Coffee and food spot on Donegall Street in the Cathedral Quarter, reopened in its new home in 2025. Central location for a working stop.', 'CAFE', NULL),
-- HOT_DESK
-- https://www.hubflow.com/ ; https://workin.space/en/united-kingdom/belfast/hubflow-dublin-road
('Hubflow Dublin Road', 'Flexible workspace at 65-69 Dublin Road, near Shaftesbury Square. Offers hot desks and day passes, with meeting rooms and phone booths.', 'HOT_DESK', NULL),
-- https://www.regus.com/en-gb/united-kingdom/belfast/coworking
('Regus Belfast The Linenhall', 'Regus centre at 32-38 Linenhall Street, behind City Hall. Day coworking and hot desking can be booked by the day or hour.', 'HOT_DESK', NULL),
-- https://www.spacesworks.com/en/gb/6833
('Spaces The Vantage', 'Spaces coworking at 32-36 Great Victoria Street, near the Europa Hotel. Offers cowork-by-the-hour and day access alongside dedicated desks.', 'HOT_DESK', NULL),
-- https://glandore.co/locations/ ; https://workin.space/en/united-kingdom/belfast/glandore-arthur-house-belfast
('Glandore Arthur House', 'Glandore workspace at 41 Arthur Street in central Belfast. Listed with day passes for hot desks in its shared coworking areas.', 'HOT_DESK', NULL),
-- BAR
-- https://www.nationaltrust.org.uk/visit/northern-ireland/the-crown-bar
('The Crown Bar', 'National Trust-owned Victorian pub on Great Victoria Street, opposite the Europa Hotel. Known for its ornate interior and enclosed snugs.', 'BAR', NULL),
-- https://camra.org.uk/pubs/kellys-cellars-belfast-117602
('Kelly''s Cellars', 'Historic pub on Bank Street, one of the oldest in the city centre. Known for pub food and traditional music sessions.', 'BAR', NULL),
-- https://thedirtyonion.com/
('The Dirty Onion', 'Pub in one of Belfast''s oldest buildings on Hill Street in the Cathedral Quarter.', 'BAR', NULL),
-- https://sunflowerbelfast.com/
('Sunflower Public House', 'Independent pub on the corner of Kent Street and Union Street, north of the city centre. Relaxed neighbourhood bar.', 'BAR', NULL),
-- HOTEL
-- https://www.grandcentralhotelbelfast.com/ ; https://discovernorthernireland.com/The-Grand-Central-Hotel-Belfast-P72420
('Grand Central Hotel Belfast', 'Hotel on Bedford Street in the business district, with the Observatory bar on the 23rd floor. Listed with free Wi-Fi.', 'HOTEL', 'ask-staff'),
-- https://www.themerchanthotel.com/ ; https://www.booking.com/hotel/gb/the-merchant.html
('The Merchant Hotel', '5-star hotel at 16 Skipper Street in the Cathedral Quarter, in a former bank. Listings note free Wi-Fi in public areas.', 'HOTEL', 'ask-staff'),
-- https://bullitthotel.com/ ; https://uk.hotels.com/ho550766592/bullitt-hotel-belfast-united-kingdom/
('Bullitt Hotel', 'Boutique hotel at 40a Church Lane, a short walk from City Hall. Listed with free Wi-Fi in public areas; has a lobby bar and rooftop bar.', 'HOTEL', 'ask-staff'),
-- https://www.fitzwilliamhotelbelfast.com/
('Fitzwilliam Hotel Belfast', '5-star hotel on Great Victoria Street beside the Grand Opera House, with a lounge and bar. Advertises complimentary high-speed Wi-Fi.', 'HOTEL', 'ask-staff'),
-- LIBRARY
-- https://www.librariesni.org.uk/libraries/greater-belfast/belfast-central-library/ ; https://www.librariesni.org.uk/libraries-ni-free-wi-fi-access/
('Belfast Central Library', 'Libraries NI''s main city library on Royal Avenue, with a first-floor reading room. Libraries NI offers free Wi-Fi to members.', 'LIBRARY', 'ask-staff'),
-- https://www.ireland.com/en/things-to-do/attractions/linen-hall-library/ ; https://discovernorthernireland.com/Linen-Hall-Library-Belfast-P3057
('Linen Hall Library', 'Historic independent library on Donegall Square North, opposite City Hall. Free to enter; non-members can use the fourth-floor study space.', 'LIBRARY', 'ask-staff'),
-- https://www.librariesni.org.uk/libraries/greater-belfast/ormeau-road-library/ ; https://www.librariesni.org.uk/libraries-ni-free-wi-fi-access/
('Ormeau Road Library', 'Libraries NI branch at 247 Ormeau Road, south Belfast. Quiet public library; Libraries NI offers free Wi-Fi to members.', 'LIBRARY', 'ask-staff'),
-- https://www.librariesni.org.uk/libraries/greater-belfast/lisburn-road-library/ ; https://www.librariesni.org.uk/libraries-ni-free-wi-fi-access/
('Lisburn Road Library', 'Libraries NI branch at 440 Lisburn Road, south Belfast. Quiet public library; Libraries NI offers free Wi-Fi to members.', 'LIBRARY', 'ask-staff'),
-- COWORKING
-- https://liquidspace.com/uk/northern-ireland/belfast/ormeau-baths
('Ormeau Baths', 'Coworking hub in a converted Victorian baths at 18 Ormeau Avenue, also home to Barclays Eagle Labs. Hot desks, offices, meeting rooms and a cafe.', 'COWORKING', NULL),
-- https://www.coworkingcafe.com/coworking-property/gb/northern-ireland/belfast/urban-hq/
('Urban HQ', 'Flexible workspace in Eagle Star House, 5-7 Upper Queen Street, near City Hall. Coworking desks, private offices, a coffee lounge and a roof terrace.', 'COWORKING', NULL),
-- https://wearecatalyst.org/workspaces/titanic-quarter-belfast/
('Catalyst Titanic Quarter', 'Non-profit innovation hub in Titanic Quarter, with dedicated desks, offices, meeting rooms and a cafe. Lists guest Wi-Fi among its facilities.', 'COWORKING', 'ask-staff'),
-- https://glandore.co/locations/
('Glandore Arthur Place', 'Glandore coworking and office space at Arthur Place in Belfast city centre, near CastleCourt and the main shopping streets.', 'COWORKING', NULL),

-- ===================== DUBLIN =====================
-- CAFE
-- https://www.vicecoffeeinc.com/ ; https://workation-astro.pages.dev/cities/dublin/cafes
('Vice Coffee Inc', 'Specialty coffee at 54 Middle Abbey Street, Dublin 1. Recommended for long laptop sessions, with a communal table and power along the walls.', 'CAFE', 'ask-staff'),
-- https://clementandpekoe.com/ ; https://lovindublin.com/best-of/14-best-places-to-work-or-study-in-when-you-find-yourself-in-dublin-city-centre
('Clement & Pekoe', 'Coffee and tea house on South William Street, Dublin 2. Has a communal table and suits shorter work sessions.', 'CAFE', NULL),
-- https://kaph.ie/ ; https://mckennas.guides.ie/node/3078
('Kaph', 'Independent specialty coffee shop at 31 Drury Street, Dublin 2, with seating over two floors.', 'CAFE', NULL),
-- https://www.twopupscoffee.com/
('Two Pups Coffee', 'Neighbourhood cafe at 74 Francis Street in the Liberties, Dublin 8. Coffee and brunch spot.', 'CAFE', NULL),
-- HOT_DESK
-- https://ti.to/dublin_city_hotdesks_tcube/260527 ; https://www.coworkingcafe.com/coworking-property/ie/d/dublin-city/tcube-dublin-city/
('tcube Dublin', 'Coworking at 1 O''Connell Street Lower by O''Connell Bridge. Hot desks bookable for a morning, afternoon or full day, plus 5- and 20-day passes.', 'HOT_DESK', NULL),
-- https://liquidspace.com/ie/dublin/dublin-11/talent-garden-dublin/desk-1
('Talent Garden Dublin', 'Tech campus on Claremont Avenue, Glasnevin, Dublin 11. Its day pass covers common areas, the lounge and community space.', 'HOT_DESK', NULL),
-- https://www.wework.com/en-GB/buildings/one-central-plaza--dublin ; https://wework.com/l/coworking-space/dublin
('WeWork One Central Plaza', 'WeWork building on Dame Street, Dublin 2, near Temple Bar and Trinity College. Day passes are bookable; WeWork lists high-speed Wi-Fi.', 'HOT_DESK', 'ask-staff'),
-- https://hubblehq.com/pass/office-space/8263/iconic-offices-the-greenway
('Iconic Offices The Greenway', 'Day desks at 112-114 St Stephen''s Green, Dublin 2. The day pass includes Wi-Fi, coffee machines and kitchen access.', 'HOT_DESK', 'ask-staff'),
-- BAR
-- https://thehoxton.com/dublin/library-bar/ ; https://thehoxton.com/dublin/
('The Library Bar at The Hoxton', 'Bar and restaurant in The Hoxton hotel at 1-5 Exchequer Street, Dublin 2. The Hoxton offers complimentary Wi-Fi throughout.', 'BAR', 'ask-staff'),
-- https://visitdublin.com/the-long-hall-pub
('The Long Hall', 'Victorian pub at 51 South Great George''s Street, Dublin 2, near Dublin Castle. Known for its preserved Victorian interior.', 'BAR', NULL),
-- https://www.hotpress.com/lifestyle-sports/five-reasons-to-love-grogans-23149940
('Grogan''s Castle Lounge', 'Family-run pub at 15 South William Street, Dublin 2, with a gallery space upstairs. Known for toasties served all day.', 'BAR', NULL),
-- https://www.visitdublin.com/kehoes
('Kehoe''s', 'Heritage pub at 9 South Anne Street, just off Grafton Street, Dublin 2. Preserved Victorian interior with snugs.', 'BAR', NULL),
-- HOTEL
-- https://theshelbourne.com/ ; https://nz.hotels.com/ho114527/the-shelbourne-autograph-collection-dublin-ireland
('The Shelbourne', 'Historic hotel on St Stephen''s Green, Dublin 2, whose Lord Mayor''s Lounge overlooks the Green. Listings note free Wi-Fi in some public areas.', 'HOTEL', 'ask-staff'),
-- https://www.doylecollection.com/hotels/the-westbury-hotel ; https://doylecollection.com/hotels/the-westbury-hotel/rooms/services-amenities
('The Westbury', '5-star hotel on Balfe Street off Grafton Street, Dublin 2. Its first-floor lounge, The Gallery, is a meeting spot; free Wi-Fi throughout the hotel.', 'HOTEL', 'ask-staff'),
-- https://thedeanhotels.com/locations/dublin-centre
('The Dean Dublin', 'Design hotel on Harcourt Street, Dublin 2, close to St Stephen''s Green. Lists free Wi-Fi among its amenities.', 'HOTEL', 'ask-staff'),
-- https://www.thealexhotel.ie/
('The Alex Hotel', 'Boutique hotel in Dublin 2 city centre. Lists free Wi-Fi among its amenities.', 'HOTEL', 'ask-staff'),
-- LIBRARY
-- https://www.dublincity.ie/libraries/find-library/dublin-city-library-and-archive ; https://www.dublincity.ie/residential/libraries/using-your-library/computers-and-internet
('Dublin City Library and Archive', 'City library and archive on Pearse Street, Dublin 2. Open study space in the reading room; free Wi-Fi and eduroam.', 'LIBRARY', 'ask-staff'),
-- https://www.dublincity.ie/libraries/find-library/central-library
('Central Library (Ilac Centre)', 'Dublin City Libraries'' central branch in the Ilac Centre off Henry Street, Dublin 1. Free Wi-Fi and eduroam; open late on weekdays.', 'LIBRARY', 'ask-staff'),
-- https://www.dublincity.ie/libraries/find-library/kevin-street-library
('Kevin Street Library', 'Dublin City Libraries branch on Kevin Street, Dublin 8, a short walk from St Patrick''s Cathedral. Free Wi-Fi and eduroam.', 'LIBRARY', 'ask-staff'),
-- https://www.dublincity.ie/libraries/find-library/rathmines-library
('Rathmines Library', 'Dublin City Libraries branch on Rathmines Road, Dublin 6, in a historic Carnegie library building. Free Wi-Fi in the branch.', 'LIBRARY', 'ask-staff'),
-- COWORKING
-- https://dogpatchlabs.com/
('Dogpatch Labs', 'Startup hub in the CHQ Building on Custom House Quay, Dublin 1, with coworking desks and offices in vaulted dockside spaces.', 'COWORKING', NULL),
-- https://www.huckletree.com/coworking-space/dublin/pearse-street ; https://www.huckletree.com/daypass-meeting-room-terms-and-conditions
('Huckletree Dublin', 'Coworking in the Academy building, 42 Pearse Street, Dublin 2, near Pearse DART. Day passes cover hot desks and breakout areas on weekdays.', 'COWORKING', 'ask-staff'),
-- https://glandore.co/locations/
('Glandore Fitzwilliam Hall', 'Glandore coworking and offices at Fitzwilliam Hall, Fitzwilliam Place, Dublin 2, in the Georgian business district.', 'COWORKING', NULL),
-- https://www.iconicoffices.com/ ; https://liquidspace.com/ie/d/dublin-8/iconic-offices-the-masonry-dublin-8
('Iconic Offices The Masonry', 'Coworking in a refurbished historic building at 151 Thomas Street, Dublin 8. Iconic Offices day desks include Wi-Fi and kitchen access.', 'COWORKING', 'ask-staff')
ON CONFLICT DO NOTHING;
