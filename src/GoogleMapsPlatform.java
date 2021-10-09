

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;

public class GoogleMapsPlatform {
	General general = new General();
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void getOptimizedRouteInfo() throws IOException {
		getOptimizedRouteOrder(); //come out with the final_destinations_list
		getFinalDestinationCoordinates(); //get final_destinations_coordinates_list for MapDisplay
		getOptimizedRouteCoordinates(); //get routes points along the final route for MapDisplay
		beautify_text_file("runtime_files/final_destinations_list_directions");
		compare_routes("runtime_files/destinations_list", "runtime_files/final_destinations_list");
		getDrivingInfo();
		//general.format_hotel_excel_sheets_list("runtime_files/final_destinations_list");
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void getOptimizedRouteCoordinates() throws IOException {
		//String[] wp = getWaypoints("runtime_files/destinations_list_directions");
		//System.out.println("original route:" + Arrays.toString(wp));
		String[] waypoints = getWaypoints("runtime_files/final_destinations_list_directions");
		//System.out.println("suggested route:" + Arrays.toString(waypoints));
		String url_firstpart = "https://maps.googleapis.com/maps/api/directions/json?";
		String url_units = "&units=metric";
		String url_key = "&key=";
		String url_str = null;
		if(waypoints.length==2) {
			String url_origin = String.format("origin=%s", waypoints[0]);
			String url_destination = String.format("&destination=%s", waypoints[1]);
			url_str = url_firstpart + url_origin + url_destination + url_units + url_key;
		}else {
			String url_origin = String.format("origin=%s", waypoints[0]);
			String url_destination = String.format("&destination=%s", waypoints[waypoints.length-1]);
			url_str = url_firstpart + url_origin + url_destination + "&waypoints=optimize:true|";
			for(int w=0;w<waypoints.length-2;w++) {
				url_str = url_str + "via:" + waypoints[w+1] + "|";
			}
			url_str = url_str.substring(0, url_str.length()-1);
			url_str = url_str + url_units + url_key;
			//System.out.println("route url(optimized): "+ url_str);	
		}
		
		try
		{
			URL url = new URL(url_str);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			Formatter fmtr = new Formatter("runtime_files/final_route_coordinates_list");
			String line;
			//String[] order = null;
			boolean atSteps = false;
			while((line = in.readLine()) != null) {
				if(line.contains("steps")) {
					atSteps = true;
				}
				if(line.contains("end_location") && atSteps) {
					line = in.readLine();
					String latsplit[] = line.split(":");
					String lat_str = latsplit[1];
					lat_str = lat_str.substring(1, lat_str.length()-1);
					Double lat_double = Double.parseDouble(lat_str);
					//lat_double = round(lat_double, 2);
					//System.out.println(lat_double);
					line = in.readLine();
					String longsplit[] = line.split(":");
					String long_str = longsplit[1];
					long_str = long_str.substring(1);
					Double long_double = Double.parseDouble(long_str);
					//long_double = round(long_double, 2);
					//System.out.println(long_double);
					fmtr.format("[%f,%f]\n", lat_double, long_double);
				}
			}
			fmtr.close();
			System.out.println("final_route_coordinates_list created.");
			getMapsURL();
		}catch(MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		}catch(IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
		
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void getOptimizedRouteOrder() throws IOException {
		//System.out.println("getting route coordinates information from Google Maps");
		String[] waypoints = getWaypoints("runtime_files/destinations_list_directions");
		//System.out.println("original route: " + Arrays.toString(waypoints));
		String url_firstpart = "https://maps.googleapis.com/maps/api/directions/json?";
		String url_units = "&units=metric";
		String url_key = "&key=";
		String url_str = null;
		if(waypoints.length==2) {
			String url_origin = String.format("origin=%s", waypoints[0]);
			String url_destination = String.format("&destination=%s", waypoints[1]);
			url_str = url_firstpart + url_origin + url_destination + url_units + url_key;
		}else {
			String url_origin = String.format("origin=%s", waypoints[0]);
			String url_destination = String.format("&destination=%s", waypoints[waypoints.length-1]);
			url_str = url_firstpart + url_origin + url_destination + "&waypoints=optimize:true|";
			for(int w=0;w<waypoints.length-2;w++) {
				url_str = url_str + waypoints[w+1] + "|";
			}
			url_str = url_str.substring(0, url_str.length()-1);
			url_str = url_str + url_units + url_key;
			//System.out.println("route url(not optimized): "+ url_str);	
		}
		
		try
		{
			URL url = new URL(url_str);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			Formatter fmtr = new Formatter("runtime_files/original_route_coordinates_list");
			Formatter nf = new Formatter("runtime_files/final_destinations_list_directions");
			String line;
			String[] order = null;
			boolean atSteps = false;
			boolean gotWaypointOrder = false;
			while((line = in.readLine()) != null) {
				
				if(line.contains("steps")) {
					atSteps = true;
				}
				if(line.contains("end_location") && atSteps) {
					line = in.readLine();
					String latsplit[] = line.split(":");
					String lat_str = latsplit[1];
					lat_str = lat_str.substring(1, lat_str.length()-1);
					Double lat_double = Double.parseDouble(lat_str);
					//System.out.println(lat_double);
					line = in.readLine();
					String longsplit[] = line.split(":");
					String long_str = longsplit[1];
					long_str = long_str.substring(1);
					Double long_double = Double.parseDouble(long_str);
					//System.out.println(long_double);
					fmtr.format("[%f,%f]\n", lat_double, long_double);
				}
				
				if(line.contains("waypoint_order") && !gotWaypointOrder) {
					String[] orderSplit = line.split(":");
					String orderInfo = orderSplit[1];
					//System.out.println(orderInfo);
					if(orderInfo.equals(" []")) {
						///////////////////////////////////////////////////////////////////////////////////////////////////////
						System.out.println("the entered route is already in the optimized order");
						///////////////////////////////////////////////////////////////////////////////////////////////////////
					}
					orderInfo = orderInfo.substring(3, orderInfo.length()-2);
					order = orderInfo.split(", ");
					//System.out.println(Arrays.toString(order));
					gotWaypointOrder = true;
				}
			}
			int[] Order = new int [order.length];
			for(int p=0; p<Order.length;p++) {
				Order[p] = Integer.parseInt(order[p]);
			}
			//System.out.println(Arrays.toString(Order));
			String [] final_places = new String[Order.length];
			nf.format("%s\n", waypoints[0]);
			//System.out.println(Arrays.toString(waypoints));
			for(int p=0;p<final_places.length;p++) {
				//System.out.println(waypoints[1+Order[p]]);
				nf.format("%s\n",waypoints[1+Order[p]]);
			}
			//System.out.println(Arrays.toString(final_places));
			nf.format("%s\n", waypoints[waypoints.length-1]);
			in.close();
			fmtr.close();
			nf.close();
			System.out.println("final_destinations_list_directions and original_route_coordinates_list txt files created.");
		}catch(MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		}catch(IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String[] getDistanceandDuration(String origin, String destination) {
		String[] RouteInfo = new String [2];
		RouteInfo[0] = getDrivingDistance(origin, destination);
		RouteInfo[1] = getDrivingDuration(origin, destination);
		return RouteInfo;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void getDrivingInfo() throws IOException {
		ArrayList<String> destinations = general.read_line_from_file("runtime_files/final_destinations_list");
		ArrayList<String> route_driving_info = new ArrayList<String>();
		String url_firstpart = "https://maps.googleapis.com/maps/api/directions/json?";
		String url_units = "&units=metric";
		String url_key = "&key=";
		for(int i=0;i<destinations.size()-1;i++) { //it's -1 because we are getting driving route(interval)
			String origin = destinations.get(i); String desti = destinations.get(i+1);
			String url_origin = String.format("origin=%s", origin);
			String url_destination = String.format("&destination=%s", desti);
			URL url = new URL(url_firstpart + url_origin + url_destination + url_units + url_key);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line; String distance_str = null; String duration_str = null;
			boolean gotDis = false; boolean gotDur = false;
			while((line = in.readLine()) != null && (!gotDis || !gotDur)) {
				if(line.contains("distance") && !gotDis) {
					line = in.readLine();
					String split[] = line.split(":");
					distance_str = split[1];
					distance_str = distance_str.substring(2,distance_str.length()-2);
					gotDis = true;
					//System.out.println("driving distance information found");
				}
				if(line.contains("duration") && !gotDur) {
					line = in.readLine();
					String split[] = line.split(":");
					duration_str = split[1];
					duration_str = duration_str.substring(2,duration_str.length()-2);
					gotDur = true;
				}	
			}
			in.close();
			System.out.println("got route driving distance and duration info");
			route_driving_info.add(origin); route_driving_info.add(desti);
			route_driving_info.add(distance_str); route_driving_info.add(duration_str);
			//System.out.println(origin+" -> " + desti+ ":"+ distance_str + "-" + duration_str);
		}
		general.output_route_driving_into_to_txt(route_driving_info);
		//System.out.println(route_driving_info);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String getDrivingDistance(String origin, String destination){
		//System.out.println("getting driving distance information from Google Maps");
		try
		{
			String url_firstpart = "https://maps.googleapis.com/maps/api/directions/json?";
			String url_origin = String.format("origin=%s", origin);
			String url_destination = String.format("&destination=%s", destination);
			String url_units = "&units=metric";
			String url_key = "&key=";
			URL url = new URL(url_firstpart + url_origin + url_destination + url_units + url_key);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			boolean gotDis = false;
			while((line = in.readLine()) != null) {
				if(line.contains("distance") && !gotDis) {
					line = in.readLine();
					String split[] = line.split(":");
					String distance_str = split[1];
					distance_str = distance_str.substring(2,distance_str.length()-2);
					gotDis = true;
					in.close();
					System.out.println("driving distance information found");
					//System.out.printf("Distance: %s \n", distance_str);
					return distance_str;
				}
			}
			in.close();
			return null;
		}catch(MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		}catch(IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
		return null;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String getDrivingDuration(String origin, String destination) {
		//System.out.println("getting driving duration information from Google Maps");
		try
		{
			String url_firstpart = "https://maps.googleapis.com/maps/api/directions/json?";
			String url_origin = String.format("origin=%s", origin);
			String url_destination = String.format("&destination=%s", destination);
			String url_key = "&key=";
			URL url = new URL(url_firstpart + url_origin + url_destination + url_key);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			boolean gotDur = false;
			while((line = in.readLine()) != null) {
				if(line.contains("duration") && !gotDur) {
					line = in.readLine();
					String split[] = line.split(":");
					String duration_str = split[1];
					duration_str = duration_str.substring(2,duration_str.length()-2);
					gotDur = true;
					in.close();
					System.out.println("driving duration information found");
					//System.out.printf("Duration: %s \n", duration_str);
					return duration_str;
				}
			}
			return null;
		}catch(MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		}catch(IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
		return null;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	Double[] getPlaceCoordinate(String address) {
		//System.out.println("getting address' coordinate from Google Maps");
		try
		{
			String url_firstpart = "https://maps.googleapis.com/maps/api/geocode/json?";
			String url_address = String.format("&address=%s", address);
			String url_key = "&key=";
			URL url = new URL(url_firstpart + url_address + url_key);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			String latitude_str, longitude_str;
			Double latitude_double, longitude_double;
			boolean gotCordi = false;
			while((line = in.readLine()) != null) {
				if(line.contains("location") && !gotCordi) {
					line = in.readLine();
					String split[] = line.split(":");
					latitude_str = split[1];
					latitude_str = latitude_str.substring(1,latitude_str.length()-1);
					line = in.readLine();
					String split2[] = line.split(":");
					longitude_str = split2[1];
					longitude_str = longitude_str.substring(1, longitude_str.length());
					gotCordi = true;
					latitude_double = Double.parseDouble(latitude_str);
					longitude_double = Double.parseDouble(longitude_str);
					//latitude_double = round(latitude_double,2);
					//longitude_double = round(longitude_double,2);
					//System.out.println(latitude_double);
					//System.out.println(longitude_double);
					Double[] addressCoordinate = new Double[2];
					addressCoordinate[0] = latitude_double;
					addressCoordinate[1] = longitude_double;
					System.out.println("place coordinate found");
					return addressCoordinate;
				}
			}
			return (Double[]) null;
		}catch(MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		}catch(IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
		return (Double[]) null;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	Double[] getCityCoordinate(String address) {
		//System.out.println("getting address' coordinate from Google Maps");
		try
		{
			String url_firstpart = "https://maps.googleapis.com/maps/api/geocode/json?";
			String url_address = String.format("&address=%s", address);
			String url_key = "&key=";
			URL url = new URL(url_firstpart + url_address + url_key);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			String latitude_str, longitude_str;
			Double latitude_double, longitude_double;
			boolean gotCordi = false;
			while((line = in.readLine()) != null) {
				if(line.contains("location") && !gotCordi) {
					line = in.readLine();
					String split[] = line.split(":");
					latitude_str = split[1];
					latitude_str = latitude_str.substring(1,latitude_str.length()-1);
					line = in.readLine();
					String split2[] = line.split(":");
					longitude_str = split2[1];
					longitude_str = longitude_str.substring(1, longitude_str.length());
					gotCordi = true;
					latitude_double = Double.parseDouble(latitude_str);
					longitude_double = Double.parseDouble(longitude_str);
					//latitude_double = round(latitude_double,2);
					//longitude_double = round(longitude_double,2);
					//System.out.println(latitude_double);
					//System.out.println(longitude_double);
					Double[] addressCoordinate = new Double[2];
					addressCoordinate[0] = latitude_double;
					addressCoordinate[1] = longitude_double;
					System.out.println("city coordinate found");
					return addressCoordinate;
				}
			}
			return (Double[]) null;
		}catch(MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		}catch(IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
		return (Double[]) null;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	Double[] getNationalParkInfoCenterCoordinate(String ori_address) throws IOException {
		String processed_address = processAddressInput(ori_address);
		String url_str1, url_str2;
		String url_firstpart = "https://maps.googleapis.com/maps/api/geocode/json?";
		String url_address1 = String.format("&address=%s+Visitor+Center", processed_address);
		String url_address2 = String.format("&address=%s+Information+Center", processed_address);
		String url_key = "&key=";
		url_str1 = url_firstpart + url_address1 + url_key;
		url_str2 = url_firstpart + url_address2 + url_key;
		//System.out.println(url_str1);
		//System.out.println(url_str2);
		URL url1 = new URL(url_str1);
		URL url2 = new URL(url_str2);
		BufferedReader in1 = new BufferedReader(new InputStreamReader(url1.openStream()));
		BufferedReader in2 = new BufferedReader(new InputStreamReader(url2.openStream()));
		String line, lat1_str = "a", long1_str = "b", lat2_str = "c", long2_str = "d";
		String address1 = "a", address2 = "b";
		while((line = in1.readLine()) != null) {
			if( line.contains("formatted_address") ) {
				String[] split = line.split(":");
				address1 = split[1];
				address1 = address1.substring(2, address1.length()-2);
			}
			if( line.contains("location") ) {
				String line1 = in1.readLine();
				String line2 = in1.readLine();
				lat1_str = line1.split(":")[1];
				lat1_str = lat1_str.substring(1, lat1_str.length()-1);
				long1_str = line2.split(":")[1];
				long1_str = long1_str.substring(1);
				//System.out.println(lat1_str+","+long1_str);
				break;
			}
		}
		while((line = in2.readLine()) != null) {
			if( line.contains("formatted_address") ) {
				String[] split = line.split(":");
				address2 = split[1];
				address2 = address2.substring(2, address2.length()-2);
			}
			if( line.contains("location") ) {
				String line1 = in2.readLine();
				String line2 = in2.readLine();
				lat2_str = line1.split(":")[1];
				lat2_str = lat2_str.substring(1, lat2_str.length()-1);
				long2_str = line2.split(":")[1];
				long2_str = long2_str.substring(1);
				//System.out.println(lat2_str+","+long2_str);
				break;
			}
		}
		if(address1.equals(address2) && lat1_str.equals(lat2_str) && long1_str.equals(long2_str)) {
			//System.out.println(lat1_str+","+long1_str);
			Double lat_double = Double.parseDouble(lat1_str);
			Double long_double = Double.parseDouble(long1_str);
			Double[] address_coordinate = new Double [2];
			address_coordinate[0] = lat_double;
			address_coordinate[1] = long_double;
			System.out.println("national park info/visitor center coordinate found");
			return address_coordinate;
		}else {
			return null;
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String getMapsURL() throws IOException {
		//System.out.println("creating GoogleMaps url");
		String[] waypoints = getWaypoints("runtime_files/final_destinations_list_directions");
		String url_str = null;
		String url_firstpart = "https://www.google.com/maps/dir/?api=1";
		String url_travelMode = "&travelmode=driving";
		if(waypoints.length==2) {
			String url_origin = String.format("&origin=%s", waypoints[0]);
			String url_destination = String.format("&destination=%s", waypoints[1]);
			url_str = url_firstpart + url_origin + url_destination + url_travelMode;
		}else {
			String url_origin = String.format("&origin=%s", waypoints[0]);
			String url_destination = String.format("&destination=%s", waypoints[waypoints.length-1]);
			url_str = url_firstpart + url_origin + url_destination + url_travelMode + "&waypoints=";
		}
		for(int w=0;w<waypoints.length-2;w++) {
			url_str = url_str + waypoints[w+1] + "%7C";
		}
		url_str = url_str.substring(0, url_str.length()-3);
		//System.out.println("Google Maps url created");
		System.out.printf("google maps reference url: %s\n",url_str);
		return url_str;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void getFinalDestinationCoordinates() throws IOException {
		General general = new General();
		Formatter fmt1 = new Formatter("runtime_files/final_destinations_coordinates_list");
		ArrayList<String> original_destinations = general.read_line_from_file("runtime_files/destinations_list_directions");
		ArrayList<String> original_destinations_coordinates = general.read_line_from_file("runtime_files/destinations_coordinates_list");
		ArrayList<String> final_destinations = general.read_line_from_file("runtime_files/final_destinations_list_directions");
		for(String destination : final_destinations) {
			int index = original_destinations.indexOf(destination);
			fmt1.format("%s\n", original_destinations_coordinates.get(index));
		}
		fmt1.close();
		System.out.println("final_destinations_coordinates_list txt file created");
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void getRouteCoordinates() throws IOException {
		//System.out.println("getting route coordinates information from Google Maps");
		String[] waypoints = getWaypoints("runtime_files/final_places_list");
		String url_firstpart = "https://maps.googleapis.com/maps/api/directions/json?";
		String url_units = "&units=metric";
		String url_key = "&key=";
		String url_str = null;
		if(waypoints.length==2) {
			String url_origin = String.format("origin=%s", waypoints[0]);
			String url_destination = String.format("&destination=%s", waypoints[1]);
			url_str = url_firstpart + url_origin + url_destination + url_units + url_key;
		}else {
			String url_origin = String.format("origin=%s", waypoints[0]);
			String url_destination = String.format("&destination=%s", waypoints[waypoints.length-1]);
			url_str = url_firstpart + url_origin + url_destination + "&waypoints=";
			for(int w=0;w<waypoints.length-1;w++) {
				url_str = url_str + "via:" + waypoints[w] + "|";
			}
			url_str = url_str.substring(0, url_str.length()-1);
			url_str = url_str + url_units + url_key;
			//System.out.println(url_str);	
		}
		
		try
		{
			URL url = new URL(url_str);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			Formatter fmtr = new Formatter("runtime_files/route_coordinates_list");
			String line;
			boolean atSteps = false;
			while((line = in.readLine()) != null) {
				if(line.contains("steps")) {
					atSteps = true;
				}
				if(line.contains("end_location") && atSteps) {
					line = in.readLine();
					String latsplit[] = line.split(":");
					String lat_str = latsplit[1];
					lat_str = lat_str.substring(1, lat_str.length()-1);
					Double lat_double = Double.parseDouble(lat_str);
					//lat_double = round(lat_double, 2);
					//System.out.println(lat_double);
					line = in.readLine();
					String longsplit[] = line.split(":");
					String long_str = longsplit[1];
					long_str = long_str.substring(1);
					Double long_double = Double.parseDouble(long_str);
					//long_double = round(long_double, 2);
					//System.out.println(long_double);
					fmtr.format("[%f,%f]\n", lat_double, long_double);
				}
			}
			in.close();
			fmtr.close();
			System.out.println("route_coordinates_list created.");	
		}catch(MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		}catch(IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
		
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	static String processAddressInput(String oriAddress) {
		String split[] = oriAddress.split(" ");
		String processed_address = null;
		for(int w=0;w<split.length;w++) {
			//System.out.println(split[w]);
			processed_address = processed_address + split[w] + "+";
		}
		processed_address = processed_address.substring(4, processed_address.length()-1);
		//System.out.println(processed_address);
		return processed_address;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String[] getWaypoints(String fn) throws IOException {
		
		File file = new File(fn);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		int line_num = getFileLines(fn);
		String[] waypoints = new String [line_num];
		String line = null;
		for(int l=0;l<line_num;l++) {
			line = reader.readLine();
			waypoints[l] = line;
			//System.out.println(line);
		}
		reader.close();
		return waypoints;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
		    BigDecimal bd = new BigDecimal(value);
		    bd = bd.setScale(places, RoundingMode.HALF_UP);
		    return bd.doubleValue();
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	static int getFileLines(String fn) throws IOException {
		int line_num = 0;
		File file = new File(fn);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while( reader.readLine() != null) {
			line_num++;
		}
		reader.close();
		return line_num;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void beautify_text_file(String file_name) throws IOException {
		String[] pois = getWaypoints(file_name);
		Formatter fmtr = new Formatter("runtime_files/final_destinations_list");
		for( String poi :  pois) {
			poi = poi.replaceAll("\\+", " ");
			fmtr.format("%s\n", poi);
		}
		fmtr.close();
		System.out.println("final_destinations_list created");
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void compare_routes(String ori_file, String final_file) throws IOException {
		String[] ori_pois = getWaypoints("runtime_files/destinations_list");
		String[] final_pois = getWaypoints("runtime_files/final_destinations_list");
		System.out.println("original route:" + Arrays.toString(ori_pois));
		System.out.println("suggested route:" + Arrays.toString(final_pois));
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	String getOrigin() {
		System.out.println("Origin:");
		Scanner originScanner = new Scanner(System.in);
		String origin = originScanner.next();
		originScanner.close();
		return origin;
	}
	
	String getDestination() {
		System.out.println("Destination:");
		Scanner destinationScanner = new Scanner(System.in);
		String destination = destinationScanner.next();
		destinationScanner.close();
		return destination;
	}
	*/
	
}
