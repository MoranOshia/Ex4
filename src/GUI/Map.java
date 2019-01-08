package GUI;




import Geom.Point3D;

public class Map {
	//1033.7639174532708
	//4327.375764
	/**
	 * The function get the size of the map and x ,y in pixel, and convert them to coords.
	 * @param mapWidth
	 * @param mapHeight
	 * @param x
	 * @param y
	 * @return
	 */
	public static  Point3D PixelToCoords(int mapWidth, int mapHeight, double x, double y) {
		
		////////////////////////////////////////////////////
		final double rw = 6371*1000;
		final double pi = Math.PI;
		double ln = Math.cos(((32.10571) * pi)/180);
//		//32.10571,35.20238
//		//32.10186,35.21237
		//double xn = Math.asin(local_vector_in_meter.x()/rw)*(180/pi);
		double ratio_x = Math.sin((35.21237 - 35.20238)*pi/180)*rw*ln/mapWidth;
		double ratio_y = Math.sin((32.10186 - 32.10571)*pi/180)*rw/mapHeight;
		double cSize = 1033.7639174532708;
		double pSize = Math.sqrt(Math.pow((mapWidth), 2)+Math.pow(mapHeight, 2));
		double ratio = (cSize / pSize);
		MyCoords m = new MyCoords();
		Point3D p = new Point3D(32.10571,35.20238);
		
		Point3D v = new Point3D((y) * (ratio_y), (x) * ratio_x);
		Point3D r= new Point3D(m.add(p, v));
		//System.out.println("convert"+r.toString());
		return r;
	}
	/**
	 * The function get the size of the map and x ,y in coords and convert them to pixel
	 * @param mapWidth
	 * @param mapHeight
	 * @param y
	 * @param x
	 * @return
	 */
	public static  Point3D coordsToPixel(int mapWidth, int mapHeight, double y, double x) {
	
		final double rw = 6371*1000;
		final double pi = Math.PI;
		double ln = Math.cos(((32.10571) * pi)/180);
//		//32.10571,35.20238
//		//32.10186,35.21237
		//double xn = Math.asin(local_vector_in_meter.x()/rw)*(180/pi);
		double ratio_x = Math.sin((35.21237 - 35.20238)*pi/180)*rw*ln/mapWidth;
		double ratio_y = Math.sin((32.10186 - 32.10571)*pi/180)*rw/mapHeight;
//		
//		double cSize = 1033.7639174532708;
//		double pSize = Math.sqrt(Math.pow((mapWidth), 2)+Math.pow(mapHeight, 2));
//		double ratio = cSize / pSize;
//		System.out.println(ratio);
		double xn = x - 35.20238;
		double yn = y - 32.10571;
		
		xn = xn * pi/180;
		yn = yn * (pi/180);
		
		xn = Math.sin(xn) * rw *ln;
		yn = Math.sin(yn) * rw;
		
		xn =  (xn) /ratio_x;
		yn =  (yn) /ratio_y;
		int xn1 = (int)xn+1;
		int yn1 = (int)yn+1;
		
		//System.out.println("pixel: ("+xn1+","+yn1+")");
		
		return new Point3D(xn1,yn1);
	
	}
	/**
	 * The fuction calculate the distance between 2 pixel's points
	 * @param mapWidth
	 * @param mapHeight
	 * @param pack
	 * @param fruit
	 * @return m.distance3d(p1, p2)
	 */
	public double distance(int mapWidth, int mapHeight,Point3D pack, Point3D fruit) {
		 MyCoords m = new MyCoords();
		Point3D p1 = PixelToCoords(mapWidth, mapHeight, pack.x(), pack.y());
		Point3D p2 = PixelToCoords(mapWidth, mapHeight, fruit.x(), fruit.y());
		return m.distance3d(p1, p2);
	}
	public static void main(String[] args) {
	//	Point3D p =new Point3D(32.10241771028104,35.2075179134491)
	}

}
