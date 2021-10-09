

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.transform.TransformerException;

import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.Font;
import org.geotools.styling.PointPlacement;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.TextSymbolizer;
import org.geotools.swing.JMapFrame;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

import javafx.scene.shape.Line;

public class MapDisplay {
	
	MapContent map = new MapContent();
	General general = new General();
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void addLayers() throws SchemaException, IOException, TransformerException {
		addBaseLayer();
		addDestinationPointsLayer();
		addRoutePointsLayer();
		//addNationalParksInfoCenterPointsLayer();
		addDurationsInfoLayer();
		addDistancesInfoLayer();
		addRouteLinesLayer();
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void display() {
		JMapFrame.showMap(map);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void addBaseLayer() throws IOException {
        
        File file = new File("/Users/apple/Desktop/Maps(shp)/world.shp");

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = store.getFeatureSource();
        map.setTitle("map");
        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        Layer baseLayer = new FeatureLayer(featureSource, style);
        map.addLayer(baseLayer);
    }
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void addDistancesInfoLayer() throws IOException, SchemaException {
    	
    	Coordinate[] coordinates = getMiddlePointsbetweenDestinations();
    	//String[] distancesInfo = getDistancesbetweenDestinations("runtime_files/final_destinations_list_directions");
    	ArrayList<String> distances = get_distance_info_from_txt("runtime_files/route_driving_info");
    	
    	for(int d=0;d<coordinates.length;d++) {
    		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    		builder.setName("feature5");
    		builder.setCRS(DefaultGeographicCRS.WGS84);
    		builder.add("MiddlePoints", Point.class);
    		final SimpleFeatureType TYPE = DataUtilities.createType("MiddlePoints", "the_geom:Point");
    		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
    		GeometryFactory geometryFactory = (GeometryFactory) JTSFactoryFinder.getGeometryFactory();
    		Point point = geometryFactory.createPoint(coordinates[d]); 
    		featureBuilder.add(point);
    		SimpleFeature feature = featureBuilder.buildFeature(null);
    		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("internal", TYPE);
    		featureCollection.add(feature);
			Style style = SLD.createPointStyle("circle", Color.BLUE, Color.BLUE, 1.0f, 1.0f);
			////////////////////////////////////////////////////////////////////////////////
			StyleBuilder styleBuilder = new StyleBuilder();
			Font font = styleBuilder.createFont("Times New Roman", 15);
			PointPlacement pointPlacement = styleBuilder.createPointPlacement(0, 1.5, 0);
			TextSymbolizer textSymbolizer = styleBuilder.createStaticTextSymbolizer(Color.BLUE, font, distances.get(d));
			textSymbolizer.setLabelPlacement(pointPlacement);
			Rule rule = styleBuilder.createRule(textSymbolizer);
			style.featureTypeStyles().get(0).rules().add(rule);
		    ////////////////////////////////////////////////////////////////////////////////
    	    Layer layer = new FeatureLayer(featureCollection, style);
    		map.layers().add(layer);
    	}
    	
    }
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void addDurationsInfoLayer() throws SchemaException, IOException {
    	
    	Coordinate[] coordinates = getMiddlePointsbetweenDestinations();
    	//String[] durationsInfo = getDurationsbetweenDestinations("runtime_files/final_destinations_list_directions");
    	ArrayList<String> durations = get_duration_info_from_txt("runtime_files/route_driving_info");
    	
    	for(int d=0;d<coordinates.length;d++) {
    		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    		builder.setName("feature4");
    		builder.setCRS(DefaultGeographicCRS.WGS84);
    		builder.add("MiddlePoints", Point.class);
    		final SimpleFeatureType TYPE = DataUtilities.createType("MiddlePoints", "the_geom:Point");
    		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
    		GeometryFactory geometryFactory = (GeometryFactory) JTSFactoryFinder.getGeometryFactory();
    		Point point = geometryFactory.createPoint(coordinates[d]); 
    		featureBuilder.add(point);
    		SimpleFeature feature = featureBuilder.buildFeature(null);
    		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("internal", TYPE);
    		featureCollection.add(feature);
			Style style = SLD.createPointStyle("circle", Color.BLUE, Color.BLUE, 1.0f, 1.0f);
			////////////////////////////////////////////////////////////////////////////////
			StyleBuilder styleBuilder = new StyleBuilder();
			Font font = styleBuilder.createFont("Times New Roman", 15);
			PointPlacement pointPlacement = styleBuilder.createPointPlacement(0, 0, 0);
			TextSymbolizer textSymbolizer = styleBuilder.createStaticTextSymbolizer(Color.BLUE, font, durations.get(d));
			textSymbolizer.setLabelPlacement(pointPlacement);
			Rule rule = styleBuilder.createRule(textSymbolizer);
			style.featureTypeStyles().get(0).rules().add(rule);
		    ////////////////////////////////////////////////////////////////////////////////
    	    Layer layer = new FeatureLayer(featureCollection, style);
    		map.layers().add(layer);
    	}
    	
    }
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void addDestinationPointsLayer() throws IOException, SchemaException, TransformerException {
    	
    	Coordinate[] coordinates = getCoordinatesSequence("runtime_files/final_destinations_coordinates_list");
    	String[] destinations = getDestinationsNames("runtime_files/final_destinations_list");
    	
    	for(int c=0;c<coordinates.length;c++) {
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			builder.setName("feature1");
			builder.setCRS(DefaultGeographicCRS.WGS84);
			builder.add("DestinationPoints", Point.class);
			final SimpleFeatureType TYPE = DataUtilities.createType("DestinationPoints", "the_geom:Point");
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
			GeometryFactory geometryFactory = (GeometryFactory) JTSFactoryFinder.getGeometryFactory();
			Point point = geometryFactory.createPoint(coordinates[c]); 
			featureBuilder.add(point);
			SimpleFeature feature = featureBuilder.buildFeature(null);
			DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("internal", TYPE);
			featureCollection.add(feature);
			Style style = SLD.createPointStyle("square", Color.red, Color.red, 1.0f, 5.0f);
			////////////////////////////////////////////////////////////////////////////////
			StyleBuilder styleBuilder = new StyleBuilder();
			Font font = styleBuilder.createFont("Arial", 16);
			PointPlacement pointPlacement = styleBuilder.createPointPlacement(0.8, 0.8, 0);
			TextSymbolizer textSymbolizer = styleBuilder.createStaticTextSymbolizer(Color.RED, font, destinations[c]);
			textSymbolizer.setLabelPlacement(pointPlacement);
			Rule rule = styleBuilder.createRule(textSymbolizer);
			style.featureTypeStyles().get(0).rules().add(rule);
		    ////////////////////////////////////////////////////////////////////////////////
		    Layer layer = new FeatureLayer(featureCollection, style);
			map.layers().add(layer);
    	}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void addNationalParksInfoCenterPointsLayer()throws IOException, SchemaException, TransformerException {
    	
    	Coordinate[] coordinates = getCoordinatesSequence("runtime_files/national_parks_coordinates_list");
    	String[] destinations = getDestinationsNames("runtime_files/national_parks_list");
    	
    	for(int c=0;c<coordinates.length;c++) {
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			builder.setName("feature6");
			builder.setCRS(DefaultGeographicCRS.WGS84);
			builder.add("DestinationPoints", Point.class);
			final SimpleFeatureType TYPE = DataUtilities.createType("DestinationPoints", "the_geom:Point");
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
			GeometryFactory geometryFactory = (GeometryFactory) JTSFactoryFinder.getGeometryFactory();
			Point point = geometryFactory.createPoint(coordinates[c]); 
			featureBuilder.add(point);
			SimpleFeature feature = featureBuilder.buildFeature(null);
			DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("internal", TYPE);
			featureCollection.add(feature);
			Style style = SLD.createPointStyle("circle", Color.GREEN, Color.GREEN, 1.0f, 6.0f);
			////////////////////////////////////////////////////////////////////////////////
			StyleBuilder styleBuilder = new StyleBuilder();
			Font font = styleBuilder.createFont("Arial", 13);
			PointPlacement pointPlacement = styleBuilder.createPointPlacement(-0.08, -0.08, 0);
			TextSymbolizer textSymbolizer = styleBuilder.createStaticTextSymbolizer(Color.GREEN, font, destinations[c]);
			textSymbolizer.setLabelPlacement(pointPlacement);
			Rule rule = styleBuilder.createRule(textSymbolizer);
			style.featureTypeStyles().get(0).rules().add(rule);
		    ////////////////////////////////////////////////////////////////////////////////
		    Layer layer = new FeatureLayer(featureCollection, style);
			map.layers().add(layer);
    	}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void addRoutePointsLayer() throws IOException, SchemaException {
		
    	Coordinate[] coordinates = getCoordinatesSequence("runtime_files/final_route_coordinates_list");
    	
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("feature2");
		builder.setCRS(DefaultGeographicCRS.WGS84);
		builder.add("RoutePoints", Point.class);
		final SimpleFeatureType TYPE = DataUtilities.createType("RoutePoints", "the_geom:MultiPoint");
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
		GeometryFactory geometryFactory = (GeometryFactory) JTSFactoryFinder.getGeometryFactory();
		MultiPoint points = geometryFactory.createMultiPoint(coordinates); 
		featureBuilder.add(points);
		SimpleFeature feature = featureBuilder.buildFeature(null);
		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("internal", TYPE);
		featureCollection.add(feature);
	    Style style = SLD.createSimpleStyle(TYPE,Color.BLUE);
	    Layer layer = new FeatureLayer(featureCollection, style);
		map.layers().add(layer);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void addRouteLinesLayer() throws SchemaException, IOException {
		
		Coordinate[] coordinates = getCoordinatesSequence("runtime_files/final_route_coordinates_list");
		
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("feature3");
		builder.setCRS(DefaultGeographicCRS.WGS84);
		builder.add("route", Line.class);
		SimpleFeatureType TYPE = DataUtilities.createType("RouteLines", "line", "the_geom:LineString");
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
		GeometryFactory geometryFactory = (GeometryFactory) JTSFactoryFinder.getGeometryFactory();
		LineString lines = geometryFactory.createLineString(coordinates);
		featureBuilder.add(lines);
		SimpleFeature feature = featureBuilder.buildFeature(null);
		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("internal", TYPE);
		featureCollection.add(feature);
	    Style style = SLD.createLineStyle(Color.cyan, 1);
	    Layer layer = new FeatureLayer(featureCollection, style);
		map.layers().add(layer);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	static int getFileLines(String fn) throws IOException {
		String fileLocation = fn;
		int line_num = 0;
		File file = new File(fileLocation);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while( reader.readLine() != null) {
			line_num++;
		}
		reader.close();
		return line_num;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	static Coordinate[] getCoordinatesSequence(String fn) throws IOException {
		String fileLocation = fn;
		int line_num = getFileLines(fn);
		Coordinate[] coordinates = new Coordinate[line_num];
		//System.out.println(line_num);
		File file = new File(fileLocation);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null, lat_str, long_str;
		Double lat_double = null, long_double = null;
		for(int l=0;l<line_num;l++) {
			line = reader.readLine();
			String split[] = line.split(",");
			lat_str = split[0].substring(1);
			long_str = split[1].substring(0, split[1].length()-1);
			lat_double = Double.parseDouble(lat_str);
			long_double = Double.parseDouble(long_str);
			coordinates[l] = new Coordinate(long_double, lat_double); //be careful of the order
			//System.out.println(lat_double+","+long_double);
		}
		reader.close();
		return coordinates;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	static String[] getDestinationsNames(String fileName) throws IOException {
		String fileLocation = fileName;
		int line_num = getFileLines(fileName);
		String[] destinations = new String[line_num];
		File file = new File(fileLocation);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String destination = null;
		for(int l=0;l<line_num;l++) {
			destination = reader.readLine();
			destinations[l] = destination;
		}
		reader.close();
		return destinations;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	static Coordinate[] getMiddlePointsbetweenDestinations() throws IOException {
		
		Coordinate[] coordinates = getCoordinatesSequence("runtime_files/final_destinations_coordinates_list");
		Coordinate[] midPoints = new Coordinate[coordinates.length-1];
		Double lat_double = null, lat1_double = null, lat2_double = null, long_double = null, long1_double = null, long2_double = null;
		for(int l=0;l<coordinates.length-1;l++) {
			long1_double = coordinates[l].x;
			lat1_double = coordinates[l].y;
			long2_double = coordinates[l+1].x;
			lat2_double = coordinates[l+1].y;
			long_double = long1_double + 0.5*(long2_double - long1_double);
			lat_double = lat1_double + 0.5*(lat2_double - lat1_double);
			//System.out.println(lat_double + "," + long_double);
			midPoints[l] = new Coordinate(long_double, lat_double);
		}
		return midPoints;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	ArrayList<String> get_duration_info_from_txt(String fileName) throws IOException{
		ArrayList<String> route_info = general.read_line_from_file("runtime_files/route_driving_info");
		ArrayList<String> durations = new ArrayList<String>();
		String line; String duration;
		for(int i=0;i<route_info.size();i++) {
			line = route_info.get(i);
			String[] split = line.split(":");
			duration = split[1];
			durations.add(duration);
		}
		//System.out.println(durations);
		return durations;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	ArrayList<String> get_distance_info_from_txt(String fileName) throws IOException{
		ArrayList<String> route_info = general.read_line_from_file("runtime_files/route_driving_info");
		ArrayList<String> distances = new ArrayList<String>();
		String line; String distance;
		for(int i=0;i<route_info.size();i++) {
			line = route_info.get(i);
			String[] split = line.split(":");
			distance = split[0];
			distances.add(distance);
		}
		//System.out.println(distances);
		return distances;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	static String[] getDurationsbetweenDestinations(String fileName) throws IOException {
		GoogleMapsPlatform GoogleMaps = new GoogleMapsPlatform();
		String[] destinations = getDestinationsNames(fileName);
		String[] durationsInfo = new String [destinations.length-1];
		for(int d=0;d<destinations.length-1;d++) {
			String duration = GoogleMaps.getDrivingDuration(destinations[d], destinations[d+1]);
			durationsInfo[d] = duration;
			//System.out.println(duration);
		}
		return durationsInfo;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	static String[] getDistancesbetweenDestinations(String fileName) throws IOException {
		GoogleMapsPlatform GoogleMaps = new GoogleMapsPlatform();
		String[] destinations = getDestinationsNames(fileName);
		String[] distancesInfo = new String [destinations.length-1];
		for(int d=0;d<destinations.length-1;d++) {
			String duration = GoogleMaps.getDrivingDistance(destinations[d], destinations[d+1]);
			distancesInfo[d] = duration;
			//System.out.println(duration);
		}
		return distancesInfo;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
}