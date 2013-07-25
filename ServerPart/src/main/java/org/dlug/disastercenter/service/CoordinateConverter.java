package org.dlug.disastercenter.service;

public class CoordinateConverter {
	private final static double LAT_DEGREE_PER_KM = 0.008983;
	private final static double LNG_DEGREE_PER_KM = 0.015060;
	
	private final static double LAT_DEGREE_PER_KMA_Y = 0.045714229;
	private final static double LNG_DEGREE_PER_KMA_X = 0.052940268;
	private final static double INCREASE_RATIO_PER_LAT = 0.136325514;
	
	public static class CoordKma{
		public int x;
		public int y;
	}
	
	public static class CoordLatLng{
		public double lat;
		public double lng;
	}
	
	public static double lat2KM(double latitude){
		return latitude / LAT_DEGREE_PER_KM;
	}
	
	public static double lng2KM(double longitude){
		return longitude / LNG_DEGREE_PER_KM;
	}
	
	public static CoordKma latlng2Kma(double latitude, double longitude){
		double defaultX = longitude - 123.3102;
		double defaultY = latitude - 31.6518;
				
		CoordKma result = new CoordKma();
		
		result.x = (int) (defaultX / (LNG_DEGREE_PER_KMA_X + (defaultY * INCREASE_RATIO_PER_LAT)));
		result.y = (int) (defaultY / LAT_DEGREE_PER_KMA_Y);
		
		return result;
	}
	
	public static CoordLatLng Kma2latlng(int kmaX, int kmaY){
		double defaultY = kmaY * LAT_DEGREE_PER_KMA_Y;
		double defaultX = (kmaX * (LNG_DEGREE_PER_KMA_X + (defaultY * INCREASE_RATIO_PER_LAT)));
		 
		
//		double defaultX = longitude - 123.3102;
//		double defaultY = latitude - 31.6518;
				
		CoordLatLng result = new CoordLatLng();
		
		result.lat = defaultY + 31.6518;
		result.lng = defaultX + 123.3102;
		
		return result;
	}
}
