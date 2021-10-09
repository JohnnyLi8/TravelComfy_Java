
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Excel {
	
	General general = new General();
	final int HOTEL_START_ROW = 2;
	final int ITINERARY_START_ROW = 1;
	final int HOTEL_LOCATION_START_ROW = 1;
	final int ROUTE_INFO_START_ROW = 1;
	final int ROUTE_CELL = 2;
	final int ROUTE_INFO_CELL = 3;
	final int HOTEL_LOCATION_CELL = 5;
	final int TRANSPORTATION_CELL = 1;
	final int TOP_ACTIVITIES_ROW_OFFSET = 2;
	//Traveler traveler = new Traveler();
	
	
	void init() throws IOException {
		if (new File("trip.xls").exists()==true && new File("hotels.xls").exists()==true) {
			System.out.println("trip.xls and hotels.xls initialized");
			return;
		}
		if(new File("trip.xls").exists()==false) {
			init_tripxls_sheets();
			add_dates_to_tripxls();
			fillin_hotel_dates_in("trip.xls");
			fillin_travel_routes();
			fillin_hotel_locations();
			fillin_route_info();
		}
		if(new File("hotels.xls").exists()==false) {
			init_hotelxls_sheets();
			fillin_hotel_dates_in("hotels.xls");
		}
		//System.out.println("trip.xls and hotels.xls initialized");
	}
	
	
	void fillin_route_info() {
		try {
			ArrayList<String> final_travel_route = general.read_line_from_file("runtime_files/final_travel_route");
			//System.out.println(route_info);
			FileInputStream fileIn = new FileInputStream("trip.xls");
			Workbook wb = WorkbookFactory.create(fileIn);
			Sheet sheet = wb.getSheetAt(0);
			String driving_info;
			CellStyle cs = wb.createCellStyle(); cs.setWrapText(true);
			//////
			for(int i=0; i<final_travel_route.size();i++) {
				ArrayList<String> info = new ArrayList<String>();
				String line = final_travel_route.get(i);
				if(line.contains("airport") || line.contains("explore")) continue;
				String[] split = line.split(" -> ");
				if(split.length==2) {
					driving_info = general.get_distance_duration_of_route(split[0], split[1]);
					info.add(driving_info);
				}else if(split.length>2) {
					for(int j=0; j<split.length-1;j++) {
						driving_info = general.get_distance_duration_of_route(split[j], split[j+1]);
						info.add(driving_info);
					}
				}
				//System.out.println(info);
				int row_index = ROUTE_INFO_START_ROW + i;
				Row row = sheet.getRow(row_index);
				if(info.size()==1) {
					Cell route_info_cell = row.createCell(ROUTE_INFO_CELL);
					route_info_cell.setCellType(CellType.STRING);
					route_info_cell.setCellValue(info.get(0));
				}else {
					String str = general.convert_ArrayList_to_Array_separated_by(info, ";");
					Cell route_info_cell = row.createCell(ROUTE_INFO_CELL);
					route_info_cell.setCellType(CellType.STRING);
					route_info_cell.setCellValue(str);
					route_info_cell.setCellStyle(cs);
				}
				Cell transportation_cell = row.createCell(TRANSPORTATION_CELL);
				transportation_cell.setCellType(CellType.STRING);
				transportation_cell.setCellValue("driving");
			}
			//////
			sheet.autoSizeColumn(ROUTE_INFO_CELL);
			FileOutputStream output = new FileOutputStream("trip.xls");
			wb.write(output);
			wb.close();
			output.close();
			System.out.println("route info filled in trip.xls");
		}catch(Exception e) {
			System.out.println("fail to fill in route info in trip.xls");
			e.printStackTrace();
		}
	}
	
	void fillin_hotel_locations() {
		try {
			ArrayList<String> daily_hotel_plan = general.read_line_from_file("runtime_files/daily_hotel_plan");
			FileInputStream fileIn = new FileInputStream("trip.xls");
			Workbook wb = WorkbookFactory.create(fileIn);
			Sheet sheet = wb.getSheetAt(0);
			for(int i=0; i<daily_hotel_plan.size();i++) {
				int row_index = HOTEL_LOCATION_START_ROW + i;
				Row row = sheet.getRow(row_index);
				Cell cell = row.createCell(HOTEL_LOCATION_CELL);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(daily_hotel_plan.get(i));
			}
			sheet.autoSizeColumn(HOTEL_LOCATION_CELL);
			FileOutputStream output = new FileOutputStream("trip.xls");
			wb.write(output);
			wb.close();
			output.close();
			System.out.println("hotel locations filled in trip.xls");
		}catch(Exception e) {
			System.out.println("fail to fill in hotel locations in trip.xls");
			e.printStackTrace();
		}
	}
	
	void fillin_travel_routes() {
		try {
			ArrayList<String> travel_routes = general.read_line_from_file("runtime_files/final_travel_route");
			FileInputStream fileIn = new FileInputStream("trip.xls");
			Workbook wb = WorkbookFactory.create(fileIn);
			Sheet sheet = wb.getSheetAt(0);
			for(int i=0; i<travel_routes.size();i++) {
				int row_index = ITINERARY_START_ROW + i;
				Row row = sheet.getRow(row_index);
				Cell cell = row.createCell(ROUTE_CELL);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(travel_routes.get(i));
			}
			sheet.autoSizeColumn(ROUTE_CELL);
			FileOutputStream output = new FileOutputStream("trip.xls");
			wb.write(output);
			wb.close();
			output.close();
			System.out.println("travel routes filled in trip.xls");
		}catch(Exception e) {
			System.out.println("fail to fill in travel routes in trip.xls");
			e.printStackTrace();
		}
	}
	
	
	int getTotalNightstoStay() {
		int total_days=0; int total_nights =0;
		try {
			FileInputStream fileIn = new FileInputStream("trip.xls");
			Workbook wb = WorkbookFactory.create(fileIn);
			Sheet sheet = wb.getSheetAt(0);
			int row_index=1;
			Row row = sheet.getRow(row_index);
			while(row!=null) {
				Cell cell = row.getCell(0);
				String cell_content = cell.getStringCellValue();
				if(cell_content.contains("-")) {
					total_days++;
				}
				row_index++;
				row = sheet.getRow(row_index);
			}
			total_nights = total_days-1; //leaving the last day, so subtract last day
			System.out.printf("total trip duration: %d days %d nights \n",total_days,total_nights);
			wb.close();
		}catch(Exception e) {
			System.out.println("fail to get total nights to stay from trip.xls");
			e.printStackTrace();
		}
		return total_nights;
	}
	
	void fillin_hotel_dates_in(String excel_name) throws IOException {
		ArrayList<String> hotel_dates_list = general.read_line_from_file("runtime_files/formatted_hotel_dates_list");
		ArrayList<String> destinations = new ArrayList<String>(); ArrayList<String> dates_list = new ArrayList<String>();
		for(int i=0;i<hotel_dates_list.size();i++) {
			String line = hotel_dates_list.get(i); String[] split = line.split(":");
			String desti = split[0]; String d1 = split[1]; String d2 = split[2];
			destinations.add(desti);
			dates_list.add(d1); dates_list.add(d2);
		}
		try {
			FileInputStream fileIn = new FileInputStream(excel_name);
			Workbook wb = WorkbookFactory.create(fileIn);
			for(int i=0;i<destinations.size();i++) {
				String sheet_name = destinations.get(i);
				Sheet sheet = wb.getSheet(sheet_name);
				//Sheet sheet = wb.getSheetAt(i);
				Row r0 = sheet.getRow(0);
				Cell c1 = r0.getCell(1); Cell c2 = r0.getCell(2);
				if(c1==null || c2==null) {
					c1 = r0.createCell(1); c2 = r0.createCell(2);
					c1.setCellType(CellType.STRING); c2.setCellType(CellType.STRING);
					c1.setCellValue(dates_list.get(2*i));
					c2.setCellValue(dates_list.get(2*i+1));
				}else {
					Cell c5 = r0.createCell(5); Cell c6 = r0.createCell(6);
					c5.setCellType(CellType.STRING); c6.setCellType(CellType.STRING);
					c5.setCellValue(dates_list.get(2*i));
					c6.setCellValue(dates_list.get(2*i+1));
				}
				sheet.autoSizeColumn(1); sheet.autoSizeColumn(2);
			}
			FileOutputStream output = new FileOutputStream(excel_name);
			wb.write(output);
			wb.close();
			output.close();
			System.out.printf("hotel dates filled in %s \n", excel_name);
		}catch(Exception e) {
			System.out.println("fail to fill in dates in hotels.xls");
			e.printStackTrace();
		}
	}
	
	void add_dates_to_tripxls() throws IOException {
		ArrayList<String> dates = general.read_line_from_file("runtime_files/dates");
		String departure_date = dates.get(0);
		String return_date= dates.get(1);
		try {
			FileInputStream fileIn = new FileInputStream("trip.xls");
			Integer[] m_31 = new Integer[] {1,3,5,7,8,10,12};
			Integer[] m_30 = new Integer[] {4,6,9,11};
			Workbook wb = WorkbookFactory.create (fileIn);
			Sheet sheet = wb.getSheetAt(0);
			int row_index = 1;
			int year = Integer.parseInt(departure_date.split("-")[0]);
			int month = Integer.parseInt(departure_date.split("-")[1]); 
			int day = Integer.parseInt(departure_date.split("-")[2]);
			String date = year + "-" + month + "-" + day;
			while(!(date.equals(return_date))) {
				if(Arrays.asList(m_31).contains(month)) {
					if(day==32) {
						day = 1;
						month ++;
						if(month==13) {
							year = year + 1;
							month=1;
						}
					}
				}else if(Arrays.asList(m_30).contains(month)) {
					if(day==31) {
						day = 1;
						month ++;
						if(month==13) {
							year = year + 1;
							month=1;
						}
					}
				}else if(month==2) {
					if(year%4==0 && day==30) {
						day = 1;
						month ++;
						if(month==13) {
							year = year + 1;
							month=1;
						}
					}else if(year%4 !=0 && day==29) {
						day = 1;
						month++;
						if(month==13) {
							year = year + 1;
							month=1;
						}
					}
				}
				date = year + "-" + month + "-" + day;
				Row row = sheet.createRow(row_index);
				Cell cell = row.createCell(0);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(date);
				day++;
				row_index ++;
			}
			sheet.autoSizeColumn(0);
			FileOutputStream output = new FileOutputStream("trip.xls");
			wb.write(output);
			wb.close();
			output.close();
		}catch(Exception e) {
			System.out.println("fail to add dates to tripxls");
			e.printStackTrace();
		}
	}
	
	void write_to_tripxls(int row_index, int cell_index, String content) {
		try {
			FileInputStream fileIn = new FileInputStream("trip.xls");
			Workbook wb = WorkbookFactory.create (fileIn);
			Sheet sheet = wb.getSheetAt(0);
			Row row = sheet.getRow(row_index);
			if(row==null) row = sheet.createRow(row_index);
			Cell cell = row.getCell(cell_index);
			if(cell==null) cell = row.createCell(cell_index);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(content);
		}catch(Exception e) {
			System.out.println("fail to write to tripxls");
			e.printStackTrace();
		}
	}
	
	void init_hotelxls_sheets() throws IOException {
		if (new File("hotels.xls").exists()==true) return;
		ArrayList<String> destinations = general.read_line_from_file("runtime_files/formatted_hotel_dates_list");
		for(int i=0; i<destinations.size();i++) {
			String destination = destinations.get(i).split(":")[0];
			destinations.set(i, destination);
		}
		try {
			Workbook wb = new HSSFWorkbook();
			for(int i=0;i<destinations.size();i++) {
				String sheet_name = destinations.get(i);
				Sheet sheet = wb.createSheet(sheet_name);
				Row r0 = sheet.createRow(0);
				Cell r0c0 = r0.createCell(0); r0c0.setCellType(CellType.STRING); r0c0.setCellValue("date:");
				Cell r0c4 = r0.createCell(4); r0c4.setCellType(CellType.STRING); r0c4.setCellValue("date:");
				Row r1 = sheet.createRow(1);
				Cell r1c0 = r1.createCell(0); r1c0.setCellType(CellType.STRING); r1c0.setCellValue("hotel");
				Cell r1c1 = r1.createCell(1); r1c1.setCellType(CellType.STRING); r1c1.setCellValue("price");
				Cell r1c2 = r1.createCell(2); r1c2.setCellType(CellType.STRING); r1c2.setCellValue("rating");
				Cell r1c3 = r1.createCell(3); r1c3.setCellType(CellType.STRING); r1c3.setCellValue("reviews");
				Cell r1c4 = r1.createCell(4); r1c4.setCellType(CellType.STRING); r1c4.setCellValue("link");
			}
			FileOutputStream output = new FileOutputStream("hotels.xls");
			wb.write(output);
			wb.close();
			output.close();
			System.out.println("hotels.xls initialized");
		}catch(Exception e) {
			System.out.println("fail to init hotel sheet");
			e.printStackTrace();
		}
	}
	
	void init_tripxls_sheets() throws IOException {
		if (new File("trip.xls").exists()==true) return;
		ArrayList<String> hotel_destinations = general.read_line_from_file("runtime_files/formatted_hotel_dates_list");
		for(int i=0; i<hotel_destinations.size();i++) {
			String destination = hotel_destinations.get(i).split(":")[0];
			hotel_destinations.set(i, destination);
		}
		try {
			Workbook wb = new HSSFWorkbook();
			//////////////////////////////////////////////////////////////////////////////////////////////////////////
			Sheet trip_sheet = wb.createSheet("trip");
			String[] general_contents = new String[] {"date","transportation","routes","routes info", "places of interests","hotel location", "finalized hotel"};
			Row row0 = trip_sheet.createRow(0);
			for(int c=0;c<general_contents.length;c++) {
				Cell cc = row0.createCell(c);
				cc.setCellType(CellType.STRING);
				cc.setCellValue(general_contents[c]);
				trip_sheet.autoSizeColumn(c);
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////
			wb.createSheet("Top Activities");
			//////////////////////////////////////////////////////////////////////////////////////////////////////////
			for(int i=0;i<hotel_destinations.size();i++) {
				String sheet_name = hotel_destinations.get(i);
				Sheet sheet = wb.createSheet(sheet_name);
				Row r0 = sheet.createRow(0);
				Cell r0c0 = r0.createCell(0); r0c0.setCellType(CellType.STRING); r0c0.setCellValue("date:");
				Row r1 = sheet.createRow(1);
				Cell r1c0 = r1.createCell(0); r1c0.setCellType(CellType.STRING); r1c0.setCellValue("hotel");
				Cell r1c1 = r1.createCell(1); r1c1.setCellType(CellType.STRING); r1c1.setCellValue("price");
				Cell r1c2 = r1.createCell(2); r1c2.setCellType(CellType.STRING); r1c2.setCellValue("rating");
				Cell r1c3 = r1.createCell(3); r1c3.setCellType(CellType.STRING); r1c3.setCellValue("reviews");
				Cell r1c4 = r1.createCell(4); r1c4.setCellType(CellType.STRING); r1c4.setCellValue("link");
			}
			FileOutputStream output = new FileOutputStream("trip.xls");
			wb.write(output);
			wb.close();
			output.close();
			System.out.println("trip.xls initialized");
		}catch(Exception e) {
			System.out.println("fail to init trip sheet");
			e.printStackTrace();
		}
	}
	
	void output_sorted_hotels_to_excel(String sheet_name,ArrayList<String>names,ArrayList<Integer>prices,ArrayList<Float>ratings,ArrayList<Integer>reviews, ArrayList<String>links) {
		try {
			FileInputStream fileIn = new FileInputStream("trip.xls");
			Workbook wb = WorkbookFactory.create (fileIn);
			Sheet sheet = wb.getSheet(sheet_name);
			//int row_index = getFirstAvailableRow("trip.xls",sheet_name);
			int row_index = 2;
			for(int i=0;i<names.size();i++) {
				Row row = sheet.createRow(row_index);
				Cell c0 = row.createCell(0); c0.setCellType(CellType.STRING); c0.setCellValue(names.get(i));
				Cell c1 = row.createCell(1); c1.setCellType(CellType.STRING); Integer price_val = prices.get(i);
				if(price_val==99999) c1.setCellValue("sold out/not available");else c1.setCellValue(Integer.toString(price_val));
				Cell c2 = row.createCell(2); c2.setCellType(CellType.STRING); Float rating_val = ratings.get(i); 
				if(rating_val==-1.0f) c2.setCellValue("not available"); else c2.setCellValue(Float.toString(ratings.get(i)));
				Cell c3 = row.createCell(3); c3.setCellType(CellType.STRING); Integer reviews_val = reviews.get(i);
				if(reviews_val==-1) c3.setCellValue("not available"); else c3.setCellValue(Integer.toString(reviews.get(i)));
				Cell c4 = row.createCell(4); c4.setCellType(CellType.STRING); c4.setCellValue(links.get(i));
				row_index++;
			}
			for(int col=0;col<5;col++) {
				sheet.autoSizeColumn(col);
			}
			FileOutputStream output = new FileOutputStream("trip.xls");
			wb.write(output);
			wb.close();
			output.close();
			System.out.printf("sorted %s output to trip.xls\n",sheet_name);
		}catch(Exception e) {
			System.out.println("fail to output sorted hotels info to trip.xls");
			e.printStackTrace();
		}
	}
	
	void output_activities_to_excel(ArrayList<String> activities) {
		if (new File("trip.xls").exists()==false) {
			System.out.println("cannot output to trip.xls: excel file does not exist");
			return;
		}
		int total_size = activities.size();
		int activities_num = total_size/5;
		int inc = total_size/5;
		String title = activities.get(0);
		try {
			FileInputStream fileIn = new FileInputStream("trip.xls");
			Workbook wb = WorkbookFactory.create(fileIn);
			Sheet sheet = wb.getSheet("Top Activities");
			int start_row = get_end_of_sheet("trip.xls", "Top Activities");
			Row rs = sheet.createRow(start_row);
			Cell rsc0 = rs.createCell(0); rsc0.setCellType(CellType.STRING); rsc0.setCellValue(title);
			start_row++;
			Row rm = sheet.createRow(start_row); 
			Cell rmc0 = rm.createCell(0); rmc0.setCellType(CellType.STRING); rmc0.setCellValue("activity");
			Cell rmc1 = rm.createCell(1); rmc1.setCellType(CellType.STRING); rmc1.setCellValue("category");
			Cell rmc2 = rm.createCell(2); rmc2.setCellType(CellType.STRING); rmc2.setCellValue("price");
			Cell rmc3 = rm.createCell(3); rmc3.setCellType(CellType.STRING); rmc3.setCellValue("reviews");
			Cell rmc4 = rm.createCell(4); rmc4.setCellType(CellType.STRING); rmc4.setCellValue("link");
			start_row++;
			for(int i=0; i<activities_num;i++) {
				Row row = sheet.createRow(start_row + i);
				Cell c0 = row.createCell(0); c0.setCellType(CellType.STRING); c0.setCellValue(activities.get(1+i));
				Cell c1 = row.createCell(1); c1.setCellType(CellType.STRING); c1.setCellValue(activities.get(1+i+inc));
				Cell c2 = row.createCell(2); c2.setCellType(CellType.STRING); c2.setCellValue(activities.get(1+i+2*inc));
				Cell c3 = row.createCell(3); c3.setCellType(CellType.STRING); c3.setCellValue(activities.get(1+i+3*inc));
				Cell c4 = row.createCell(4); c4.setCellType(CellType.STRING); c4.setCellValue(activities.get(1+i+4*inc));
			}
			for(int i=0; i<activities_num;i++) {
				sheet.autoSizeColumn(i);
			}
			FileOutputStream output = new FileOutputStream("trip.xls");
			wb.write(output);
			wb.close();
			output.close();
			System.out.printf("%s output to trip.xls\n", title);
		}catch(Exception e) {
			System.out.println("fail to output top activities info to trip.xls");
			e.printStackTrace();
		}
	}
	

	void output_hotels_to_excel(List<String> list, int sheet_index) {
		if (new File("hotels.xls").exists()==false) {
			System.out.println("cannot output to hotels.xls: excel file does not exist");
			return;
		}
		int total_size = list.size();
		int hotels_num = total_size/5;
		int inc = total_size/5;
		String desti_name = list.get(0);
		try (FileInputStream fileIn = new FileInputStream("hotels.xls")){
			////////////////////////preparing the sheet/////////////////////////
			Workbook wb = WorkbookFactory.create (fileIn);
			//Sheet sheet = wb.getSheet(sheet_name);
			Sheet sheet = wb.getSheetAt(sheet_index);
			if(!(sheet.getSheetName().contains(desti_name))) {
				System.out.println("hotels output to wrong sheet in trip.xls");
				return;
			}
			String sheet_name = sheet.getSheetName();
			int start_row = getFirstAvailableRow("hotels.xls",sheet_name);
			///////////////////////output each hotel info to excel///////////////////////////////
			for(int i=0;i<hotels_num;i++) {
				Row row = sheet.createRow(start_row+i);
				Cell c0 = row.createCell(0); c0.setCellType(CellType.STRING); c0.setCellValue(list.get(1+i));
				Cell c1 = row.createCell(1); c1.setCellType(CellType.STRING); c1.setCellValue(list.get(1+i+inc));
				Cell c2 = row.createCell(2); c2.setCellType(CellType.STRING); c2.setCellValue(list.get(1+i+2*inc));
				Cell c3 = row.createCell(3); c3.setCellType(CellType.STRING); c3.setCellValue(list.get(1+i+3*inc));
				Cell c4 = row.createCell(4); c4.setCellType(CellType.STRING); c4.setCellValue(list.get(1+i+4*inc));
			}
			for(int col=0;col<5;col++) {
				sheet.autoSizeColumn(col);
			}
			////////////////////////output to excel file/////////////////////////
			try {
				FileOutputStream output = new FileOutputStream("hotels.xls");
				wb.write(output);
				wb.close();
				output.close();
				System.out.printf("%d hotels info in %s added to excel sheet %s in hotels.xls\n",inc,sheet_name.split(" ")[0],sheet_name);
			} catch(Exception e){
				e.printStackTrace();
				System.out.println("fail to output hotels to hotels.xls");
			}
			////////////////////////////////////////////////////////////////////
		}catch (Exception e) {
			e.printStackTrace();
	    }
	}
	
	
	
	ArrayList<Float> read_hotel_float_info_from_excel(int sheet_index, String info){
		int col_index;
		if(info=="rating") col_index = 2;
		else{
			System.out.printf("no info to read from excel on %s\n",info);
			return null;
		}
		ArrayList<Float> info_list = new ArrayList<Float>();
		try {
			int row_index = HOTEL_START_ROW;
			FileInputStream fileIn = new FileInputStream("hotels.xls");
			Workbook wb = WorkbookFactory.create (fileIn);
			Sheet sheet = wb.getSheetAt(sheet_index);
			Row row = sheet.getRow(row_index);
			while(row!=null) {
				Cell c = row.getCell(col_index);
				String cell_content = c.getStringCellValue();
				Float cell_value;
				if(cell_content.equals("not available")) {
					cell_value = -1.0f;
				}else{ 
					cell_value = Float.parseFloat(cell_content);
				}
				info_list.add(cell_value);
				row_index++;
				row = sheet.getRow(row_index);
			}
			wb.close();
			//general.display_list(info_list);
			return info_list;
		}catch(Exception e) {
			System.out.println("fail to read hotels info from excel");
			e.printStackTrace();
		}
		return info_list;
	}
	
	ArrayList<Integer> read_hotel_int_info_from_excel(int sheet_index, String info){
		int col_index;
		if(info=="price") col_index = 1;
		else if(info=="reviews") col_index = 3;
		else{
			System.out.printf("no info to read from excel on %s\n",info);
			return null;
		}
		ArrayList<Integer> info_list = new ArrayList<Integer>();
		try {
			int row_index = HOTEL_START_ROW;
			FileInputStream fileIn = new FileInputStream("hotels.xls");
			Workbook wb = WorkbookFactory.create (fileIn);
			Sheet sheet = wb.getSheetAt(sheet_index);
			Row row = sheet.getRow(row_index);
			while(row!=null) {
				Integer cell_value;
				Cell c = row.getCell(col_index);
				String cell_content = c.getStringCellValue();
				if(cell_content.equals("sold out/not available")) {
					cell_value = 99999;
				}else if(cell_content.contains("not available")) {
					cell_value = -1;
				}else if(cell_content.contains(",")) {
					cell_content = cell_content.replaceAll(",", "");
					cell_value = Integer.parseInt(cell_content);
				}else {
					cell_value = Integer.parseInt(cell_content);
				}
				info_list.add(cell_value);
				row_index++;
				row = sheet.getRow(row_index);
			}
			wb.close();
			//general.display_list(info_list);
			return info_list;
		}catch(Exception e) {
			System.out.println("fail to read hotels info from excel");
			e.printStackTrace();
		}
		return info_list;
	}
	
	ArrayList<String> read_hotel_string_info_from_excel(int sheet_index, String info){
		int col_index;
		if(info=="hotel") col_index = 0;
		else if(info=="price") col_index = 1;
		else if(info=="rating") col_index = 2;
		else if(info=="reviews") col_index = 3;
		else if(info=="link") col_index = 4;
		else{
			System.out.printf("no info to read from excel on %s\n",info);
			return null;
		}
		ArrayList<String> info_list = new ArrayList<String>();
		try {
			int row_index = HOTEL_START_ROW;
			FileInputStream fileIn = new FileInputStream("hotels.xls");
			Workbook wb = WorkbookFactory.create (fileIn);
			Sheet sheet = wb.getSheetAt(sheet_index);
			Row row = sheet.getRow(row_index);
			info_list = new ArrayList<String>();
			while(row!=null) {
				Cell c = row.getCell(col_index);
				String cell_content = c.getStringCellValue();
				info_list.add(cell_content);
				row_index++;
				row = sheet.getRow(row_index);
			}
			wb.close();
			//general.display_list(info_list);
			return info_list;
		}catch(Exception e) {
			System.out.println("fail to read hotels info from excel");
			e.printStackTrace();
		}
		return info_list;
	}
	
	int get_end_of_sheet(String excel_file, String sheet_name) {
		if (new File(excel_file).exists()==false) {
			System.out.println("cannot locate the end of excel file: excel file does not exist");
			return -1;
		}
		try {
			FileInputStream fileIn = new FileInputStream(excel_file);
			Workbook wb = WorkbookFactory.create (fileIn);
			Sheet sheet = wb.getSheet(sheet_name);
			int row_index = 0;
			Row row1 = sheet.getRow(row_index);
			Row row2 = sheet.getRow(row_index+1);
			Row row3 = sheet.getRow(row_index+3);
			if(row1==null && row2==null && row3==null) return 0;
			while( ! (row1==null && row2==null && row3==null) ) {
				row_index++;
				row1 = sheet.getRow(row_index);
				row2 = sheet.getRow(row_index+1);
				row3 = sheet.getRow(row_index+2);
			}
			wb.close();
			int desired_start_row = row_index + 2;
			//System.out.println(desired_start_row);
			return desired_start_row;
		}catch(Exception e) {
			System.out.printf("fail to get to the end of excel file\n", excel_file);
			e.printStackTrace();
		}
		return -1;
	}

	int getFirstAvailableRow(String excel_file, String sheet_name) {
		if (new File(excel_file).exists()==false) {
			System.out.println("cannot locate first available line: excel file does not exist");
			return -1;
		}
		try {
			FileInputStream fileIn = new FileInputStream(excel_file);
			Workbook wb = WorkbookFactory.create (fileIn);
			Sheet sheet = wb.getSheet(sheet_name);
			int row_index = 0;
			Row row = sheet.getRow(row_index);
			if(row==null) return 0;
			while(row!=null) {
				row_index++;
				row = sheet.getRow(row_index);
			}
			wb.close();
			//System.out.println("next available row: " + row_index);
			return row_index;
		}catch(Exception e){
			System.out.println("fail to locate first available line");
			e.printStackTrace();
			return -1;
		}
	}
	
	boolean sheet_not_exists(String file, String sheet_name) {
		try
		{
			FileInputStream fileIn = new FileInputStream(file);
			Workbook wb = WorkbookFactory.create (fileIn);
			int sheet_num = wb.getNumberOfSheets();
			for(int i=0;i<sheet_num;i++) {
				if(wb.getSheetName(i).equals(sheet_name)) {
					return false;
				}
			}
			return true;
		}catch(Exception e) {
			System.out.println("fail to check existence of a sheet");
		}
		return false;
	}
	
}
