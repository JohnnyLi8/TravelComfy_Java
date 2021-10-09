import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class StayComfy {

	General general = new General();
	Excel Excel = new Excel();
	
	void rank_hotels_with_price() {
		try {
			FileInputStream fileIn = new FileInputStream("hotels.xls");
			Workbook wb = WorkbookFactory.create (fileIn);
			int sheet_num = wb.getNumberOfSheets();
			for (int sheet_index=0;sheet_index<sheet_num;sheet_index++) {
				Sheet sheet = wb.getSheetAt(sheet_index);
				String sheet_name = sheet.getSheetName();
				ArrayList<String> hotels_names = Excel.read_hotel_string_info_from_excel(sheet_index, "hotel"); ArrayList<Integer> hotels_prices = Excel.read_hotel_int_info_from_excel(sheet_index, "price");
				ArrayList<Float> hotels_ratings = Excel.read_hotel_float_info_from_excel(sheet_index, "rating"); ArrayList<Integer> hotels_reviews = Excel.read_hotel_int_info_from_excel(sheet_index, "reviews");
				ArrayList<String> hotels_links = Excel.read_hotel_string_info_from_excel(sheet_index, "link");
				ArrayList<Integer> sorted_hotels_prices = (ArrayList<Integer>) hotels_prices.clone();
				Collections.sort(sorted_hotels_prices); //ascending order
				//general.display_string_list(hotels_names); general.display_integer_list(hotels_prices); general.display_float_list(hotels_ratings); general.display_integer_list(hotels_reviews);
				//general.display_integer_list(hotels_prices); System.out.println("///"); general.display_integer_list(sorted_hotels_prices);
				ArrayList<Integer> permutation = new ArrayList<Integer>();
				for(int i=0;i<sorted_hotels_prices.size();i++) {
					Integer price = sorted_hotels_prices.get(i);
					int p_index = hotels_prices.indexOf(price);
					permutation.add(p_index);
					hotels_prices.set(p_index, -2);
				}
				//general.display_integer_list(hotels_prices);System.out.println(); general.display_integer_list(sorted_hotels_prices); System.out.println(); general.display_integer_list(permutation); System.out.println("\n");
				ArrayList<String> sorted_hotels_names = new ArrayList<String>(); ArrayList<Float> sorted_hotels_ratings= new ArrayList<Float>(); 
				ArrayList<Integer> sorted_hotels_reviews = new ArrayList<Integer>(); ArrayList<String> sorted_hotels_links = new ArrayList<String>();
				for(int i=0;i<permutation.size();i++) {
					int p_index = permutation.get(i);
					sorted_hotels_names.add(hotels_names.get(p_index));
					sorted_hotels_ratings.add(hotels_ratings.get(p_index));
					sorted_hotels_reviews.add(hotels_reviews.get(p_index));
					sorted_hotels_links.add(hotels_links.get(p_index));
				}
				//general.display_string_list(sorted_hotels_names); general.display_integer_list(sorted_hotels_prices); general.display_float_list(sorted_hotels_ratings); general.display_integer_list(sorted_hotels_reviews);
				//output sorted hotels info to trip.xls
				Excel.output_sorted_hotels_to_excel(sheet_name, sorted_hotels_names, sorted_hotels_prices, sorted_hotels_ratings, sorted_hotels_reviews, sorted_hotels_links);
			}
			wb.close();	
		}catch(Exception e){
			System.out.println("fail to rank hotels with price");
			e.printStackTrace();
		}
	}
	
	void rank_hotels_with_rating() {
		try {
			FileInputStream fileIn = new FileInputStream("hotels.xls");
			Workbook wb = WorkbookFactory.create (fileIn);
			int sheet_num = wb.getNumberOfSheets();
			for (int sheet_index=0;sheet_index<sheet_num;sheet_index++) {
				Sheet sheet = wb.getSheetAt(sheet_index);
				String sheet_name = sheet.getSheetName();
				ArrayList<String> hotels_names = Excel.read_hotel_string_info_from_excel(sheet_index, "hotel"); ArrayList<Integer> hotels_prices = Excel.read_hotel_int_info_from_excel(sheet_index, "price");
				ArrayList<Float> hotels_ratings = Excel.read_hotel_float_info_from_excel(sheet_index, "rating"); ArrayList<Integer> hotels_reviews = Excel.read_hotel_int_info_from_excel(sheet_index, "reviews");
				ArrayList<String> hotels_links = Excel.read_hotel_string_info_from_excel(sheet_index, "link");
				ArrayList<Float> sorted_hotels_ratings = (ArrayList<Float>) hotels_ratings.clone();
				Collections.sort(sorted_hotels_ratings, Collections.reverseOrder()); //descending order
				ArrayList<Integer> permutation = new ArrayList<Integer>();
				for(int i=0;i<sorted_hotels_ratings.size();i++) {
					Float rating = sorted_hotels_ratings.get(i);
					int p_index = hotels_ratings.indexOf(rating);
					permutation.add(p_index);
					hotels_ratings.set(p_index, -2.0f);
				}
				ArrayList<String> sorted_hotels_names = new ArrayList<String>(); ArrayList<Integer> sorted_hotels_prices= new ArrayList<Integer>(); 
				ArrayList<Integer> sorted_hotels_reviews = new ArrayList<Integer>(); ArrayList<String> sorted_hotels_links = new ArrayList<String>();
				for(int i=0;i<permutation.size();i++) {
					int p_index = permutation.get(i);
					sorted_hotels_names.add(hotels_names.get(p_index));
					sorted_hotels_prices.add(hotels_prices.get(p_index));
					sorted_hotels_reviews.add(hotels_reviews.get(p_index));
					sorted_hotels_links.add(hotels_links.get(p_index));
				}
				//System.out.println(sheet_name);
				Excel.output_sorted_hotels_to_excel(sheet_name, sorted_hotels_names, sorted_hotels_prices, sorted_hotels_ratings, sorted_hotels_reviews, sorted_hotels_links);
			}
			wb.close();	
		}catch(Exception e){
			System.out.println("fail to rank hotels with rating");
			e.printStackTrace();
		}
	}
	
	void rank_hotels_with_review() {
		try {
			FileInputStream fileIn = new FileInputStream("hotels.xls");
			Workbook wb = WorkbookFactory.create (fileIn);
			int sheet_num = wb.getNumberOfSheets();
			for (int sheet_index=0;sheet_index<sheet_num;sheet_index++) {
				Sheet sheet = wb.getSheetAt(sheet_index);
				String sheet_name = sheet.getSheetName();
				ArrayList<String> hotels_names = Excel.read_hotel_string_info_from_excel(sheet_index, "hotel"); ArrayList<Integer> hotels_prices = Excel.read_hotel_int_info_from_excel(sheet_index, "price");
				ArrayList<Float> hotels_ratings = Excel.read_hotel_float_info_from_excel(sheet_index, "rating"); ArrayList<Integer> hotels_reviews = Excel.read_hotel_int_info_from_excel(sheet_index, "reviews");
				ArrayList<String> hotels_links = Excel.read_hotel_string_info_from_excel(sheet_index, "link");
				ArrayList<Integer> sorted_hotels_reviews = (ArrayList<Integer>) hotels_reviews.clone(); 
				Collections.sort(sorted_hotels_reviews, Collections.reverseOrder()); //descending order
				ArrayList<Integer> permutation = new ArrayList<Integer>();
				for(int i=0;i<sorted_hotels_reviews.size();i++) {
					Integer reviews = sorted_hotels_reviews.get(i);
					int p_index = hotels_reviews.indexOf(reviews);
					permutation.add(p_index);
					hotels_reviews.set(p_index, -2);
				}
				ArrayList<String> sorted_hotels_names = new ArrayList<String>(); ArrayList<Integer> sorted_hotels_prices= new ArrayList<Integer>(); 
				ArrayList<Float> sorted_hotels_ratings = new ArrayList<Float>(); ArrayList<String> sorted_hotels_links = new ArrayList<String>();
				for(int i=0;i<permutation.size();i++) {
					int p_index = permutation.get(i);
					sorted_hotels_names.add(hotels_names.get(p_index));
					sorted_hotels_prices.add(hotels_prices.get(p_index));
					sorted_hotels_ratings.add(hotels_ratings.get(p_index));
					sorted_hotels_links.add(hotels_links.get(p_index));
				}
				Excel.output_sorted_hotels_to_excel(sheet_name, sorted_hotels_names, sorted_hotels_prices, sorted_hotels_ratings, sorted_hotels_reviews, sorted_hotels_links);
			}
			wb.close();	
		}catch(Exception e){
			System.out.println("fail to rank hotels with reviews");
			e.printStackTrace();
		}
	}
		
}
