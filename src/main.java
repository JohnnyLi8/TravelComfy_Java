import java.util.ArrayList;

public class main {

	public static void main(String[] args) throws Exception {
		////////////////////////////////////////////////////////////////////////
		Traveler traveler = new Traveler();
		traveler.get_countries();
		traveler.getDestinations();
		traveler.convertDestinationstoCoordinates();
		////////////////////////////////////////////////////////////////////////
		GoogleMapsPlatform GoogleMaps = new GoogleMapsPlatform();
		GoogleMaps.getOptimizedRouteInfo();
		///////////////////////////////////////////////////////////////////////
		MapDisplay map = new MapDisplay();
		map.addLayers();
		map.display();
		////////////////////////////////////////////////////////////////////////
		traveler.getDepartureandReturnDates();
		traveler.getHotelsPlan();
		traveler.cnSc.close();
		////////////////////////////////////////////////////////////////////////
		Excel Excel_Plan = new Excel();
		Excel_Plan.init();
		////////////////////////////////////////////////////////////////////////
		HotelsCrawler HotelsCrawler = new HotelsCrawler();
		ArrayList<String> hotels_urls = HotelsCrawler.generate_hotels_URLs_from_txt();
		HotelsCrawler.find_destinations_hotels(hotels_urls,3);
		////////////////////////////////////////////////////////////////////////
		StayComfy StayComfy = new StayComfy();
		StayComfy.rank_hotels_with_rating();
		////////////////////////////////////////////////////////////////////////
		ActivitiesCrawler ActivitiesCrawler = new ActivitiesCrawler();
		ArrayList<String> activities_urls = ActivitiesCrawler.generate_activities_URLS_from_txt();
		ActivitiesCrawler.find_top_activities(activities_urls);
		////////////////////////////////////////////////////////////////////////
		System.out.println("Everything is ready. You may finalize your hotels and activities in the excel plan.");
	}
		
}

//task:
//init a "top activities" sheet in trip.xls after the "trip" sheet
//output top activities of places of interests to "top activities" sheet

//future improvement:
//search for flight tickets info
