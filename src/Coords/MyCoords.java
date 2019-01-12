package Coords;

import Geom.Point3D;
/*
 * this class is responsible for making element in coords like:add ,distance atc.
 */

public class MyCoords implements coords_converter{
	
	
	//COS(B6*PI()/180)
	final double rw = 6371*1000;
	final double pi = Math.PI;
	
	
	@Override
	public Point3D add(Point3D gps, Point3D local_vector_in_meter) {
//		// TODO Auto-generated method stub
//		double ln = Math.cos((gps.x()*pi)/180);
//		double xn = Math.asin(local_vector_in_meter.y()/rw)*(180/pi);
//		double yn = Math.asin(local_vector_in_meter.y()/(ln*rw))*(180/pi);
//		Point3D new_gps = new Point3D(gps.x()+xn, gps.y()+yn, gps.z()+local_vector_in_meter.z());
//		return new_gps;
		
		double lat=gps.x();

		double lon=gps.y();

		double dn=local_vector_in_meter.x();

		double de=local_vector_in_meter.y();

		double dLat = dn/rw;

		double dLon = de/(rw*Math.cos(Math.PI*lat/180));



	    //OffsetPosition, decimal degrees

		double latO = lat + dLat * 180/Math.PI;

		double lonO = lon + dLon * 180/Math.PI;

		double alt=gps.z()+local_vector_in_meter.z();

		Point3D gps1=new Point3D(latO,lonO,alt);

		return gps1;
	
	}

	@Override
	public double distance3d(Point3D gps0, Point3D gps1) {
		// TODO Auto-generated method stub
		double ln = Math.cos((gps0.x()*pi)/180);
		double difX = Math.sin((gps1.x() - gps0.x())*pi/180)*rw;
		double difY = Math.sin((gps1.y() - gps0.y())*pi/180)*ln*rw;
		return Math.sqrt(difX*difX+difY*difY);
	}

	@Override
	public Point3D vector3D(Point3D gps0, Point3D gps1) {
		double ln = Math.cos((gps0.x()*pi)/180);
		double difX = Math.sin((gps1.x() - gps0.x())*pi/180)*rw;
		double difY = Math.sin((gps1.y() - gps0.y())*pi/180)*ln*rw;
		double difZ = gps1.z() - gps0.z();
		Point3D vec = new Point3D(difX, difY, difZ);
		return vec;
	}

	@Override
	public double[] azimuth_elevation_dist(Point3D gps0, Point3D gps1) {
		
		double[] ans = new double[3];
		
		//calculate azimuth
		double x0 = gps0.x()*pi/180;
	    double x1 = gps1.x()*pi/180;
	    double dY=gps1.y()- gps0.y();
	    double delta = (dY*pi)/180;

	    double x = Math.sin(delta) * Math.cos(x1);
	    double y = Math.cos(x0) * Math.sin(x1) - Math.sin(x0)*Math.cos(x1)*Math.cos(delta);
	    double azimuth = Math.atan2(x,y);
	    
	    if(Math.toDegrees(azimuth)<0)
	    	azimuth = 360+Math.toDegrees(azimuth);
	    else
	    	azimuth = Math.toDegrees(azimuth);

	    //calculate distance
	    double distance =distance3d(gps0, gps1);
	    

	    //calculate elevation
	    double zDis=gps1.z()-gps0.z();
	    double elevation=Math.toDegrees(Math.atan(zDis/distance));
	    
	    ans[0]=azimuth;ans[1]=elevation;ans[2]=distance;
	    
	    return ans;
	}

	@Override
	public boolean isValid_GPS_Point(Point3D p) {
		
		double lat = p.x();
		double lon = p.y();
		double alt = p.z();
		if((lat > -180 && lat < 180) && (lon > -90 && lon < 90) && (alt > -450))
			return true;
		return false;
	}


}
