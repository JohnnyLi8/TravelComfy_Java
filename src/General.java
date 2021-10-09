
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

public class General {
	
	String convert_ArrayList_to_Array_separated_by(ArrayList<String> list, String feature) {
		int size = list.size();
		String str = list.get(0);
		for(int i=1; i<size; i++) {
			str = str + " " + feature + list.get(i);
		}
		return str;
	}
	
	ArrayList<String> remove_from_list_with(ArrayList<String> list, String feature){
		ArrayList<String> new_list = new ArrayList<String>();
		for(String element : list) {
			if( !(element.contains(feature)) ) {
				new_list.add(element);
			}
		}
		return new_list;
	}
	
	String get_distance_duration_of_route(String origin, String destination) throws IOException {
		ArrayList<String> route_info = read_line_from_file("runtime_files/route_driving_info");
		String driving_info;
		for(int i=0; i<route_info.size();i++) {
			String line = route_info.get(i);
			if(line.contains(origin) && line.contains(destination)) {
				String[] split = line.split(":");
				driving_info = split[1];
				//System.out.println(driving_info);
				return driving_info;
			}
		}
		System.out.printf("no driving info on route from %s to %s \n", origin, destination);
		return null;
	}
	
	void output_route_driving_into_to_txt(ArrayList<String> route_driving_info) throws FileNotFoundException {
		Formatter fmtr = new Formatter("runtime_files/route_driving_info");
		for(int i=0; i<route_driving_info.size()/4;i++) {
			fmtr.format("%s -> %s:%s-%s \n", route_driving_info.get(4*i), route_driving_info.get(4*i + 1), route_driving_info.get(4*i+2), route_driving_info.get(4*i+3));
		}
		fmtr.close();
		System.out.println("route_driving_info txt file created");
	}
	
	String get_travel_route_from(String origin, String desti, ArrayList<String> destinations) {
		String travel_route = origin;
		int start_index = destinations.indexOf(origin);
		int end_index = destinations.indexOf(desti);
		if(end_index < start_index) {
			destinations.set(end_index, "0");
			end_index = destinations.indexOf(desti);
		}
		for(int i=start_index+1; i<=end_index; i++) {
			travel_route = travel_route + " -> " + destinations.get(i);
		}
		return travel_route;
	}
	
	void generate_route(ArrayList<String> daily_hotel_plan) throws IOException {
		ArrayList<String> travel_destinations = read_line_from_file("runtime_files/final_destinations_list");
		ArrayList<String> travel_routes = new ArrayList<String>();
		//System.out.println(travel_destinations); System.out.println(daily_hotel_plan);
		String travel_route; String current_place; String hotel_place=null;
		for(int i=0; i<daily_hotel_plan.size();i++) { //it's +1 because we compare the current one with next one
			if(i==0) current_place = "airport";
			else current_place = daily_hotel_plan.get(i-1);
			hotel_place = daily_hotel_plan.get(i);
			if(current_place.equals(hotel_place)) travel_route = "explore " + hotel_place;
			else if(i==0) travel_route = current_place + " -> " + hotel_place;
			else if(i==daily_hotel_plan.size()) travel_route = current_place + " -> " + hotel_place;
			else travel_route = get_travel_route_from(current_place, hotel_place, travel_destinations);
			//System.out.println(travel_route);
			travel_routes.add(travel_route);
		}
		travel_route = hotel_place + " -> " + "airport";
		travel_routes.add(travel_route);
		//System.out.printf("the travel route is %d days \n", travel_routes.size());
		//System.out.println(travel_routes);
		output_list_to_txt("runtime_files/final_travel_route", travel_routes);
	}
	
	ArrayList<String> generate_hotel_nights_info(String[] nights_info, ArrayList<String> hotel_destinations) {
		ArrayList<String> daily_hotel_plan = new ArrayList<String>();
		for(int i=0; i<hotel_destinations.size();i++) {
			int nights = Integer.parseInt(nights_info[i]);
			for(int j=0;j<nights;j++) {
				daily_hotel_plan.add(hotel_destinations.get(i));
			}
		}
		//System.out.println(daily_hotel_plan);
		return daily_hotel_plan;
	}
	
	void format_hotel_excel_sheets_list(String file_name) throws IOException {
		ArrayList<String> destinations = read_line_from_file("runtime_files/final_destinations_list");
		for(int i=0; i<destinations.size();i++) {
			destinations.set(i,destinations.get(i)+" hotels");
		}
		ArrayList<String> hotel_excel_sheets = rename_duplicates(destinations);
		output_list_to_txt("runtime_files/hotel_excel_sheets_list", hotel_excel_sheets);
	}
	
	void output_list_to_txt(String file_name, ArrayList<String> list) throws FileNotFoundException {
		Formatter fmtr = new Formatter(file_name);
		for(int i=0;i<list.size();i++) {
			fmtr.format("%s\n", list.get(i));
		}
		fmtr.close();
		System.out.printf("%s txt file created \n", file_name);
	}
	
	ArrayList<String> rename_duplicates(ArrayList<String> list){
		ArrayList<String> renamed_list = new ArrayList<String>();
		for(int i=0; i<list.size();i++) {
			if(renamed_list.contains(list.get(i))) {
				renamed_list.add(list.get(i) + "_2");
			}else {
				renamed_list.add(list.get(i));
			}
		}
		//System.out.println(renamed_list);
		return renamed_list;
	}
	
	String[] get_destination_id_country(String folder_location, String destination) throws IOException {
		String[] id_country = new String[2]; String id=null; String desti_country=null;
		ArrayList<String> countries = read_line_from_file("runtime_files/countries_list");
		//ArrayList<String> country_files = (ArrayList<String>) countries.clone();
		for(String country:countries) {
			String file_name = folder_location + "/" + country;
			ArrayList<String> file_content = read_line_from_file(file_name);
			for(int i=0; i<file_content.size();i++) {
				String line = file_content.get(i);
				if(line.contains(destination)) {
					String[] split = line.split(":");
					id = split[1];
					desti_country = country;
					break;
				}
			}
		}
		id_country[0] = id; id_country[1] = desti_country;
		//System.out.println(Arrays.toString(id_country));
		return id_country;
	}
	
	boolean check_location_id_files_all_exist(String folder_location, ArrayList<String> countries) {
		for(int i=0; i<countries.size();i++) {
			if(!(location_file_exists(folder_location,countries.get(i)))) {
				System.out.printf("location_id file of %s does not exist \n", countries.get(i) );
				System.out.println("terminating the program");
				return false;
			}
		}
		return true;
	}
		
	
	boolean location_file_exists(String folder_location, String location_file) {
		File directory = new File("/Users/apple/Desktop/TravelComfy/_______city_ids");
		File[] files = directory.listFiles();
		for(File file : files) {
			String file_name = file.getName();
			//System.out.println(file_name);
			if(file_name.equals(location_file) ) {
				System.out.printf("location id file of %s founded \n", location_file);
				return true;
			}
		}
		return false;
	}
	
	String format_dates(String date) {
		String formatted_date = null;
		String[] split = date.split("-");
		String year = split[0]; String month = split[1]; String day = split[2];
		if(month.length()==1) month = "0" + month;
		if(day.length()==1) day = "0" + day;
		formatted_date = year + "-" + month + "-" + day;
		//System.out.println(formatted_date);
		return formatted_date;
	}
	
	int get_trip_duration(String departure_date, String return_date) {
		String increment_date = departure_date;
		int inc = 1;
		while(! (increment_date.equals(return_date))){
			increment_date = add_days_to_date(increment_date, 1);
			inc++;
		}
		int night = inc-1;
		System.out.printf("total trip duration: %s days %s nights \n", inc, night);
		return inc;
	}

	void rename_duplicates_in_hotel_dates_list(ArrayList<String> hotel_destinations, ArrayList<String> dates_list) throws FileNotFoundException {
		for(int i=0; i<hotel_destinations.size();i++) {
			hotel_destinations.set(i, hotel_destinations.get(i)+ " hotels");
		}
		hotel_destinations = rename_duplicates(hotel_destinations);
		ArrayList<String> hotel_dates_list = new ArrayList<String>();
		Formatter fmt = new Formatter("runtime_files/formatted_hotel_dates_list");
		for(int i=0;i<hotel_destinations.size();i++) {
			hotel_dates_list.add(hotel_destinations.get(i));
			hotel_dates_list.add(dates_list.get(2*i));
			hotel_dates_list.add(dates_list.get(2*i+1));
			fmt.format("%s:%s:%s\n", hotel_destinations.get(i),dates_list.get(2*i),dates_list.get(2*i+1));
		}
		fmt.close();
		System.out.println("formatted_hotel_dates_list txt file created");
	}
	
	ArrayList<String> generate_hotel_dates_list(ArrayList<String> hotel_destinations, ArrayList<String> dates_list) throws FileNotFoundException{
		ArrayList<String> hotel_dates_list = new ArrayList<String>();
		Formatter fmt = new Formatter("runtime_files/hotel_dates_list");
		for(int i=0;i<hotel_destinations.size();i++) {
			hotel_dates_list.add(hotel_destinations.get(i));
			hotel_dates_list.add(dates_list.get(2*i));
			hotel_dates_list.add(dates_list.get(2*i+1));
			fmt.format("%s:%s:%s\n", hotel_destinations.get(i),dates_list.get(2*i),dates_list.get(2*i+1));
		}
		fmt.close();
		System.out.println("hotel_dates_list created");
		return hotel_dates_list;
	}
	
	ArrayList<String> generate_dates_list(String departure_date, ArrayList<Integer> nights_info){
		ArrayList<String> hotel_dates = new ArrayList<String>();
		for(int i=0;i<nights_info.size();i++) {
			String check_out_date = add_days_to_date(departure_date, nights_info.get(i));
			hotel_dates.add(departure_date);
			hotel_dates.add(check_out_date);
			departure_date = check_out_date;
		}
		//System.out.println(hotel_dates);
		return hotel_dates;
	}
	
	int getSum(String[] nights) {
		int sum = 0;
		for(String night : nights) {
			sum = sum + Integer.parseInt(night);
		}
		return sum;
	}
	
	ArrayList<String> remove_duplicates(ArrayList<String> list) throws IOException {
		ArrayList<String> newList = new ArrayList<String>();
		for(int i=0;i<list.size();i++) {
			if(!newList.contains(list.get(i))) {
				newList.add(list.get(i));
			}
		}
		return newList;
	}
	
	ArrayList<String> read_line_from_file(String fn) throws IOException {
		File file = new File(fn);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		ArrayList<String> lines = new ArrayList<String>();
		while( (line = reader.readLine()) != null) {
			//System.out.println(line);
			lines.add(line);
		}
		reader.close();
		return lines;
	}
	
	
	void display_integer_list(List<Integer> list) {
		for(int i=0;i<list.size();i++) {
			System.out.println(list.get(i));
		}
	}
	
	void display_string_list(List<String> list) {
		for(int i=0;i<list.size();i++) {
			System.out.println(list.get(i));
		}
	}
	
	void display_float_list(List<Float> list) {
		for(int i=0;i<list.size();i++) {
			System.out.println(list.get(i));
		}
	}
	
	void display_hotel_crawler_list (List<String> list) {
		int total_size = list.size();
		int inc = total_size/5;
		System.out.println(list.get(0));
		for(int i=1;i<inc;i++) {
			System.out.printf("%s: %s   %s   %s\n",list.get(i), list.get(i+inc), list.get(i+2*inc),list.get(i+3*inc));
		}
	}
	
	String add_days_to_date(String date, int days_length) {
		String[] split = date.split("-");
		int year = Integer.parseInt(split[0]); int month = Integer.parseInt(split[1]); int day = Integer.parseInt(split[2]);
		Integer[] m_31 = new Integer[] {1,3,5,7,8,10,12}; Integer[] m_30 = new Integer[] {4,6,9,11};
		if(Arrays.asList(m_31).contains(month)) {
			day = day + days_length;
			if(day>31) {
				month++;
				day = day%31;
			}
			if(month==13) {
				year++;
				month=1;
			}
		}else if(Arrays.asList(m_30).contains(month)) {
			day = day + days_length;
			if(day>30) {
				month++;
				day = day%30;
			}
			if(month==13) {
				year++;
				month=1;
			}
		}else if(month==2) {
			if(year%4==0) {
				day = day + days_length;
				if(day>29) {
					month++;
					day=day%29;
				}
			}else if(year%4 !=0) {
				day = day + days_length;
				if(day>28) {
					month++;
					day=day%28;
				}
			}
			if(month==13) {
				year++;
				month=1;
			}
		}
		date = year + "-" + month + "-" + day;
		//System.out.println(date);
		return date;
	}
	
	void output_array_to_txt(String file_name, String[] array) throws FileNotFoundException {
		Formatter fmtr = new Formatter(file_name);
		for(int i=0;i<array.length;i++) {
			fmtr.format("%s\n", array[i]);
		}
		fmtr.close();
		System.out.printf("%s txt file created \n", file_name);
	}
		
	
}
