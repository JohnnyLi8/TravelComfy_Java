

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Traveler {
	
	GoogleMapsPlatform googlemaps = new GoogleMapsPlatform();
	General general = new General();
	Scanner cnSc = new Scanner(System.in);
	String departure_date = null;
	String return_date = null;
	int trip_duration;
	Excel excel = new Excel();
	
	void get_countries() throws FileNotFoundException {
		System.out.println("Please enter the countries you would like to visit (separated by '/'):");
		String countries = cnSc.nextLine();
		String split[] = countries.split("/");
		String country;
		Formatter fmt = new Formatter("runtime_files/countries_list");
		for(int p=0;p<split.length;p++) {
			if(Character.isWhitespace(split[p].charAt(0))==true) {
				country = split[p].substring(1);
			}else {
				country = split[p];
			}
			fmt.format("%s\n", country);
		}
		fmt.close();
		System.out.println("countries_list txt file created");
	}
	
	
	void getHotelsPlan() throws IOException {
		ArrayList<String> destinations = general.read_line_from_file("runtime_files/final_destinations_list");
		ArrayList<Integer> night_info = new ArrayList<Integer>();
		ArrayList<String> hotel_destinations = new ArrayList<String>();
		System.out.println(destinations);
		System.out.println("please enter the number of nights you would like to stay for each of the above destinations in a list[]");
		String nights_info = cnSc.nextLine();
		nights_info = nights_info.substring(1, nights_info.length()-1);
		String[] nights = nights_info.split(", ");
		int total_nights_stayed = general.getSum(nights);
		if(total_nights_stayed != trip_duration-1) {
			System.out.println("your entered hotel plan does not match the trip duration");
			System.exit(0);
		}
		System.out.printf("you will be staying with hotels for %d nights\n", total_nights_stayed);
		for(int i=0; i<nights.length;i++) {
			if(Integer.parseInt(nights[i]) !=0) {
				hotel_destinations.add(destinations.get(i));
				night_info.add(Integer.parseInt(nights[i]));
			}
		}
		//System.out.println(hotel_destinations);
		//System.out.println(night_info);
		ArrayList<String> dates_list = general.generate_dates_list(departure_date, night_info);
		general.output_list_to_txt("runtime_files/hotel_destinations_list", hotel_destinations);
		general.generate_hotel_dates_list(hotel_destinations, dates_list);  //generate a hotel_dates_list file for hotels init in excel
		ArrayList<String> daily_hotel_plan = general.generate_hotel_nights_info(nights, destinations); //need to pass in destinations, not hotel_destinations
		general.output_list_to_txt("runtime_files/daily_hotel_plan", daily_hotel_plan);
		general.rename_duplicates_in_hotel_dates_list(hotel_destinations, dates_list);
		general.generate_route(daily_hotel_plan);
		//excel.fillin_hotel_dates(hotel_destinations, hotel_dates_list);
	}
	
	String[] getDepartureandReturnDates() throws FileNotFoundException {
		System.out.println("Please enter the date you would arrive in your first destination (yyyy-mm-dd):");
		departure_date = cnSc.nextLine();
		System.out.println("Please enter the date you will leave your last destination (yyyy-mm-dd):");
		return_date = cnSc.nextLine();
		String[] dates = new String[2];
		dates[0] = departure_date;
		dates[1] = return_date;
		trip_duration = general.get_trip_duration(departure_date, return_date);
		Formatter fmt = new Formatter("runtime_files/dates");
		fmt.format("%s\n", departure_date); fmt.format("%s\n", return_date);
		fmt.close();
		return dates;
	}

	
	void convertDestinationstoCoordinates() throws IOException {
		Formatter fmt1 = new Formatter("runtime_files/destinations_coordinates_list");
		ArrayList<String> destinations = general.read_line_from_file("runtime_files/destinations_list_geocoding");
		for(String destination : destinations) {
			Double[] coordinate = googlemaps.getPlaceCoordinate(destination);
			fmt1.format("[%s,%s]\n", coordinate[0], coordinate[1]);
		}
		System.out.println("destinations_coordinates_list txt file created");
		fmt1.close();
	}
	
	void getDestinations() {
		System.out.println("Please enter the places you would like to visit (separated by '/'):");
		String spots = cnSc.nextLine();
		String split[] = spots.split("/");
		if(split.length==1) {
			System.out.println("You can not enter only one destination");
			return;
		}
		try
		{
			String destination = null;
			Formatter fmt = new Formatter("runtime_files/destinations_list");
			Formatter fmt2 = new Formatter("runtime_files/destinations_list_geocoding");
			Formatter fmt3 = new Formatter("runtime_files/destinations_list_directions");
			for(int p=0;p<split.length;p++) {
				if(Character.isWhitespace(split[p].charAt(0))==true) {
					destination = split[p].substring(1);
				}else {
					destination = split[p];
				}
				fmt.format("%s\n", destination); //writing exactly the inputs in Java to txt
				StringBuilder d = new StringBuilder(destination);
				for(int i=0;i<destination.length();i++) {
					if(Character.isWhitespace(d.charAt(i)) == true) {
						d.setCharAt(i, '+');
					}
				}
				destination = String.valueOf(d);
				fmt3.format("%s\n", destination);//convert whites spaces into '+' sign for Directions API
				if(destination.contains(",") && !destination.contains(",+")) {
					destination = addPlusafterComma(destination);
				}
				fmt2.format("%s\n", destination);
			}
			System.out.println("destinations_list, destinations_list_geocoding, and destinations_list_directions txt files created");
			fmt.close();
			fmt2.close();
			fmt3.close();
		}catch(Exception e) {
			System.out.println("fail to create destinations txt file");
			e.printStackTrace();
		}
			
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String addPlusafterComma(String d) {
		int index = 0;
		for(int i=0;i<d.length();i++) {
			if( d.charAt(i) == ',') {
				index = i;
				//System.out.println(i);
				break;
			}
		}
		String s = d.substring(0, index+1) + "+" + d.substring(index+1, d.length());
		//System.out.println("modified:" + s);
		return s;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////