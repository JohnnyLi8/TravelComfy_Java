
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HotelsCrawler {
	
	Excel Excel = new Excel();
	General general = new General();
	String rooms_url_segment = "&q-rooms=1&q-room-0-adults=2&q-room-0-children=0"; //default 1 room 2 adults 0 children
	String base_url = "https://______.com/search.do?resolved-location=CITY%3A";
	String destination_id_part = "%3AUNKNOWN%3AUNKNOWN&destination-id=";
	String destination_part = "&q-destination=";
	String checkin_part = "&q-check-in=";
	String checkout_part = "&q-check-out=";
	
	void find_destinations_hotels(ArrayList<String> urls, int pages) throws IOException {
		for(int i=0; i<urls.size();i++) {
			crawl_hotels(urls.get(i), pages, i);
		}
		System.out.println("all hotels done searching");
	}
	
	
	ArrayList<String> generate_hotels_URLs_from_txt() throws IOException {
		ArrayList<String> hotel_dates_list = general.read_line_from_file("runtime_files/hotel_dates_list");
		ArrayList<String> countries = general.read_line_from_file("runtime_files/countries_list");
		if(!(general.check_location_id_files_all_exist("/Users/apple/Desktop/TravelComfy/____city_ids",countries))) System.exit(0);
		ArrayList<String> urls = new ArrayList<String>();
		for(int i=0;i<hotel_dates_list.size();i++) {
			String line = hotel_dates_list.get(i); String split[] = line.split(":");
			String destination = split[0]; String checkin_date = split[1]; String checkout_date = split[2];
			checkin_date = general.format_dates(checkin_date); checkout_date = general.format_dates(checkout_date);
			////////////////////
			String[] city_id_country = general.get_destination_id_country("/Users/apple/Desktop/TravelComfy/_____city_ids", destination);
			String destination_id = city_id_country[0]; String country = city_id_country[1];
			String formatted_destination = destination + "," + "%20" + country;
			////////////////////
			String final_url = base_url + destination_id + destination_id_part + destination_id + destination_part + formatted_destination + checkin_part + 
					checkin_date + checkout_part + checkout_date + rooms_url_segment;
			//System.out.println(final_url);
			urls.add(final_url);
		}
		return urls;
	}
	
	
	void crawl_hotels(String base_url, int pages, int sheet_index) throws IOException{
		//////////////////init variables////////////////////////////////////////////////////////////////////////////
		String url, title = null;
		List<String> hotels_names = new ArrayList<String>(); List<String> hotels_prices = new ArrayList<String>(); 
		List<String>hotels_ratings = new ArrayList<String>(); List<String> hotels_reviews = new ArrayList<String>();
		List<String>hotels_links = new ArrayList<String>();
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////
		for(int i=1;i<pages+1;i++) {
			if(i==1) url = base_url;
			else url = base_url + "&pn=" + Integer.toString(i);
			Document page_soup = Jsoup.connect(url).get();
			title = page_soup.title(); title = title.split("-")[1]; title = title.split(",")[0]; String[] title_split = title.split(" "); 
			title = title_split[title_split.length-1]; title = title + " hotels";
			if(url.contains("&pn=")) {
				//char page_num = url.charAt(url.length()-1);
				//System.out.printf("visiting page %c of %s\n",page_num,title);
			}else {
				//System.out.printf("visiting main page of %s\n",title);
			}
			Elements hotels_containers = page_soup.select("section.hotel-wrap");
			for( Element hotel_container : hotels_containers) {
				Elements hotel_name_ele = hotel_container.select("h3.p-name > a.property-name-link");
				String hotel_name = hotel_name_ele.text(); 
				Elements hotel_price_ele = hotel_container.select("div.price");
				String hotel_price = hotel_price_ele.text();
				if(hotel_price.length()==0) hotel_price = "sold out/not available";
				else {
					//System.out.println(hotel_price);
					String[] temp = hotel_price.split(" ");
					hotel_price = temp[temp.length-2];
					if(hotel_price.contains("at$")) hotel_price = hotel_price.substring(3, hotel_price.length());
					else hotel_price = hotel_price.substring(1);
					//System.out.println(hotel_price);
				}
				Elements hotel_rating_ele = hotel_container.select("strong.guest-reviews-badge");
				String hotel_rating = hotel_rating_ele.text(); String[] hotel_rating_split = hotel_rating.split(" ");
				hotel_rating = hotel_rating_split[hotel_rating_split.length-1];
				if(hotel_rating.length()==0) hotel_rating = "not available"; //if no rating available, indicate by -1
				Elements hotel_reviews_ele = hotel_container.select("div.ta-total-reviews");
				String hotel_reviews = hotel_reviews_ele.text(); hotel_reviews = hotel_reviews.split(" ")[0];
				if(hotel_reviews.length()==0) hotel_reviews = "not available";  //if no reviews available, indicate by -1
				Elements hotel_href_ele = hotel_container.select("h3.p-name > a[href]");
				String hotel_href = hotel_href_ele.attr("href");
				String hotel_link = "https://hotels.com" + hotel_href;
				//System.out.println(hotel_name + ": " + hotel_price + "  rating: "+ hotel_rating + "  " + hotel_reviews);
				//System.out.println(hotel_link);
				if(!(hotels_names.contains(hotel_name))){
					hotels_names.add(hotel_name); hotels_prices.add(hotel_price); hotels_ratings.add(hotel_rating); hotels_reviews.add(hotel_reviews); hotels_links.add(hotel_link);
				}
			}
		}
		//for loop ends 
		if (!(hotels_names.size()==hotels_prices.size() && hotels_prices.size()==hotels_ratings.size() && hotels_ratings.size()==hotels_reviews.size() && hotels_reviews.size()==hotels_links.size())) {
			System.out.println("hotel information mismatch");
			return;
		}
		List<String> hotels = new ArrayList<String>();
		hotels.add(title); hotels.addAll(hotels_names); hotels.addAll(hotels_prices); hotels.addAll(hotels_ratings); hotels.addAll(hotels_reviews); hotels.addAll(hotels_links);
		//General.displayList(hotels);
		//System.out.printf("%d hotels in %s crawled\n",hotels_names.size(), city);
		Excel.output_hotels_to_excel(hotels, sheet_index);
	}
	
	
	List<String> crawl_main_page_info(String url) throws IOException {
		///////////////////getting the title///////////////////
		Document page_soup = Jsoup.connect(url).get();
		String title = page_soup.title(); title = title.split("-")[1]; title = title.split(",")[0]; String[] title_split = title.split(" "); title = title_split[title_split.length-1];
		//String city = title;
		title = title + " hotels";
		if(url.contains("&pn=")) {
			char page_num = url.charAt(url.length()-1);
			System.out.printf("visiting page %c of %s\n",page_num,title);
		}else {
			System.out.println("visiting main page");
		}
		/////////////////////////////////////////////////////////////////
		////////////////////init lists for info storing//////////////////
		List<String> hotels_names = new ArrayList<String>(); List<String> hotels_prices = new ArrayList<String>(); 
		List<String>hotels_ratings = new ArrayList<String>(); List<String> hotels_reviews = new ArrayList<String>();
		List<String>hotels_links = new ArrayList<String>();
		/////////////////////////////////////////////////////////////////
		//////////////////crawling general hotel info////////////////////
		Elements hotels_containers = page_soup.select("section.hotel-wrap");
		for( Element hotel_container : hotels_containers) {
			Elements hotel_name_ele = hotel_container.select("h3.p-name > a.property-name-link");
			String hotel_name = hotel_name_ele.text(); //name string
			Elements hotel_price_ele = hotel_container.select("div.price");
			String hotel_price = hotel_price_ele.text(); //price string
			if(hotel_price.length()==0) hotel_price = "sold out/not available";
			else {
				//System.out.println(hotel_price);
				String[] temp = hotel_price.split(" ");
				hotel_price = temp[temp.length-2];
				if(hotel_price.contains("at$")) hotel_price = hotel_price.substring(3, hotel_price.length());
				else hotel_price = hotel_price.substring(1);
				//System.out.println(hotel_price);
			}
			Elements hotel_rating_ele = hotel_container.select("strong.guest-reviews-badge");
			String hotel_rating = hotel_rating_ele.text(); String[] hotel_rating_split = hotel_rating.split(" ");
			hotel_rating = hotel_rating_split[hotel_rating_split.length-1];
			Elements hotel_reviews_ele = hotel_container.select("span.ta-total-reviews");
			String hotel_reviews = hotel_reviews_ele.text(); hotel_reviews = hotel_reviews.split(" ")[0];//reviews string
			if(hotel_reviews.length()==0) hotel_reviews = "0";
			Elements hotel_href_ele = hotel_container.select("h3.p-name > a[href]");
			String hotel_href = hotel_href_ele.attr("href");
			String hotel_link = "https://hotels.com" + hotel_href; //link string
			//System.out.println(hotel_name + ": " + hotel_price + "  rating: "+ hotel_rating + "  " + hotel_reviews);
			//System.out.println(hotel_link);
			hotels_names.add(hotel_name); hotels_prices.add(hotel_price); hotels_ratings.add(hotel_rating); hotels_reviews.add(hotel_reviews); hotels_links.add(hotel_link);
		}
		/////////////////////////////////////////////////////////////////
		////////////////////check for size mismatch/////////////////////
		if (!(hotels_names.size()==hotels_prices.size() && hotels_prices.size()==hotels_ratings.size() && hotels_ratings.size()==hotels_reviews.size() && hotels_reviews.size()==hotels_links.size())) {
			System.out.println("hotel information mismatch");
			return null;
		}
		/////////////////////////////////////////////////////////////////
		///////////////concat all categories into one list///////////////
		List<String> hotels = new ArrayList<String>();
		hotels.add(title); hotels.addAll(hotels_names); hotels.addAll(hotels_prices); hotels.addAll(hotels_ratings); hotels.addAll(hotels_reviews); hotels.addAll(hotels_links);
		//General.displayList(hotels);
		//System.out.printf("%d hotels in %s crawled\n",hotels_names.size(), city);
		//Excel.output_hotels_to_excel(hotels,);
		return hotels;
	}

}
