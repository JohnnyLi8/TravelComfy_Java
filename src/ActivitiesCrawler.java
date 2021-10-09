import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class ActivitiesCrawler {

	Excel Excel = new Excel();
	General general = new General();
	String activity_base_url = "https://www.________.com";
	
	void find_top_activities(ArrayList<String> urls) throws IOException {
		for(int i=0;i<urls.size();i++) {
			find_destination_top_activities(urls.get(i));
		}
		System.out.println("top activities done searching");
	}
	
	ArrayList<String> generate_activities_URLS_from_txt() throws IOException{
		ArrayList<String> destinations = general.read_line_from_file("runtime_files/final_destinations_list");
		destinations = general.remove_duplicates(destinations);
		ArrayList<String> countries = general.read_line_from_file("runtime_files/countries_list");
		if(!(general.check_location_id_files_all_exist("/Users/apple/Desktop/TravelComfy/________city_ids",countries))) System.exit(0);
		ArrayList<String> urls = new ArrayList<String>();
		for(int i=0;i<destinations.size();i++) {
			String[] city_id_country = general.get_destination_id_country("/Users/apple/Desktop/TravelComfy/______city_ids", destinations.get(i));
			//System.out.println(city_id_country[0] + "     " + city_id_country[1]);
			String destination_id = city_id_country[0]; String country = city_id_country[1];
			String final_url = activity_base_url + "/Attractions-" + destination_id + "-Activities-" + destinations.get(i) + ".html";
			//System.out.println(final_url);
			urls.add(final_url);
		}
		return urls;
	}
	
	void find_destination_top_activities(String url) throws IOException {

		ArrayList<String> activities_names = new ArrayList<String>(); ArrayList<String> activities_reviews_counts = new ArrayList<String>();
		ArrayList<String> activities_categories = new ArrayList<String>(); ArrayList<String> activities_links = new ArrayList<String>();
		ArrayList<String> activities_prices = new ArrayList<String>();
		//
		Document page_soup = Jsoup.connect(url).get();
		Elements title_ele = page_soup.select("h1");
		String title = title_ele.text();
		String[] title_parts = title.split(" - ")[1].split(" ");
		title = title_parts[0] + " " + title_parts[1];
		//System.out.println(title);
		Elements activities_containers = page_soup.select("li.attractions-attraction-overview-main-TopPOIs__item--e3w3i");
		if(activities_containers.size()!=0) {
			for(Element activity_container : activities_containers) {
				Elements activity_name_ele = activity_container.select("a.attractions-attraction-overview-main-TopPOIs__name--3eQ8p");
				String activity_name = activity_name_ele.text();
				Elements activity_review_count_ele = activity_container.select("span.reviewCount");
				String activity_review_count = activity_review_count_ele.text();
				if(activity_review_count.length()==0) activity_review_count = "no reviews";
				Elements activity_category_ele = activity_container.select("span.attractions-category-tag-CategoryTag__category_tag--1zCA6");
				String activity_category = activity_category_ele.text();
				Elements activity_price_ele = activity_container.select("span.attractions-attraction-overview-main-TopPOIs__amount--28xfW");
				String activity_price = activity_price_ele.text();
				if(activity_price.length()==0) activity_price = " ";
				else activity_price = activity_price.split(" ")[0];
				Elements activity_href_ele = activity_container.select("a[href]");
				String activity_href = activity_href_ele.attr("href");
				String activity_link = activity_base_url + activity_href;
				//System.out.println(activity_name + " - " + activity_category + " - " + activity_price + " - " + activity_review_count + " - " + activity_link);
				activities_names.add(activity_name); activities_categories.add(activity_category); activities_prices.add(activity_price);
				activities_reviews_counts.add(activity_review_count); activities_links.add(activity_link);
			}
		}else {
			activities_containers = page_soup.select("div.attraction_element");
			for(Element activity_container:activities_containers) {
				Elements activity_name_ele = activity_container.select("a[href]");
				String activity_name = activity_name_ele.text(); activity_name = activity_name.substring(0, activity_name.length()-12);
				String[] split = activity_name.split(" ");
				activity_name = split[0];
				for(int i=1; i<split.length-2;i++) {
					activity_name = activity_name + "" + split[i];
				}
				String activity_review_count = split[split.length-2] + " " + split[split.length-1];
				Elements activity_href_ele = activity_container.select("a[href]");
				String activity_href = activity_href_ele.attr("href");
				String activity_link = activity_base_url + activity_href;
				//System.out.println(activity_name); System.out.println(activity_review_count); System.out.println(activity_link);
				activities_names.add(activity_name); activities_categories.add(" "); activities_prices.add(" ");
				activities_reviews_counts.add(activity_review_count); activities_links.add(activity_link);
			}
		}
		//for loop ends above
		if(!( activities_names.size()==activities_categories.size() && activities_categories.size()==activities_prices.size() && activities_prices.size()==activities_reviews_counts.size()
				&& activities_reviews_counts.size()==activities_links.size())) {
			System.out.printf("activities information mismatch when visiting %s \n", url);
			return;
		}
		ArrayList<String> activities = new ArrayList<String>();
		activities.add(title);
		activities.addAll(activities_names); activities.addAll(activities_categories); activities.addAll(activities_prices);
		activities.addAll(activities_reviews_counts); activities.addAll(activities_links);
		//
		Excel.output_activities_to_excel(activities);
	}
	
}



