package nz.co.fortytwo.freeboard.server;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Calculates the true wind from apparent wind and vessel speed/heading
 * @author robert
 *
 */
public class WindProcessor implements Processor {

	public void process(Exchange exchange) throws Exception {
		String bodyStr = exchange.getIn().getBody(String.class).trim();
		
			try {
				int apparentWind=0;
				int apparentDirection=0;
				float vesselSpeed=0;
				boolean valid=false;
				//int heading=0;
				String [] bodyArray=bodyStr.split(",");
				for(String s:bodyArray){
					// we need HDG, and LOG
					if(s.startsWith(Constants.SOG)){
						vesselSpeed= Float.valueOf(s.substring(4));
						valid=true;
					}
					if(s.startsWith(Constants.WSA)){
						apparentWind= Integer.valueOf(s.substring(4));
						valid=true;
					}
					if(s.startsWith(Constants.WDA)){
						apparentDirection= Integer.valueOf(s.substring(4));
						valid=true;
					}
					//if(s.startsWith(Constants.COG)){
					//	heading= Integer.valueOf(s.substring(4));
					//}
				}
				if(valid){
					//now calc and add to body
					double trueWindSpeed = calcTrueWindSpeed(apparentWind, apparentDirection, vesselSpeed);
					double trueWindDir = calcTrueWindDirection(apparentWind,apparentDirection, vesselSpeed);
					if(!Double.isNaN(trueWindDir)){
						bodyStr=bodyStr+"WDT:"+round(trueWindDir,1)+",";
					}
					if(!Double.isNaN(trueWindSpeed)){
						bodyStr=bodyStr+"WST:"+round(trueWindSpeed,2)+",";
					}
					exchange.getOut().setBody(bodyStr);
				}

			} catch (Exception e) {
				// e.printStackTrace();
			}
		

	}
	
	double round(double val, int places){
		double scale = Math.pow(10, places);
		long iVal = Math.round (val*scale);
		return iVal/scale;
	}
	
	/**
	 * Calculates the true wind direction from apparent wind on vessel
	 * Result is relative to bow
	 * 
	 * @param apparentWind
	 * @param apparentDirection 0 to 360 deg to the bow
	 * @param vesselSpeed
	 * @return trueDirection 0 to 360 deg to the bow
	 */
	double calcTrueWindDirection(int apparentWind, int apparentDirection, float vesselSpeed){
		/*
			 Y = 90 - D
			a = AW * ( cos Y )
			bb = AW * ( sin Y )
			b = bb - BS
			True-Wind Speed = (( a * a ) + ( b * b )) 1/2
			True-Wind Angle = 90-arctangent ( b / a )
		*/
                apparentDirection=apparentDirection%360;
		boolean stbd = apparentDirection<=180;
        if(!stbd){
           apparentDirection=360-apparentDirection; 
        }
		double y = 90-apparentDirection;
		double a = apparentWind * Math.cos(Math.toRadians(y));
		double b = (apparentWind * Math.sin(Math.toRadians(y)))-vesselSpeed;
		double td = 90-Math.toDegrees(Math.atan((b/a)));
		if(!stbd)return (360-td);
		return td;
				
	}
	
	/**
	 * Calculates the true wind speed from apparent wind speed on vessel
	 * 
	 * @param apparentWind
	 * @param apparentDirection 0 to 360 deg to the bow
	 * @param vesselSpeed
	 * @return
	 */
        double calcTrueWindSpeed(int apparentWind, int apparentDirection, float vesselSpeed){
                apparentDirection=apparentDirection%360;
                if(apparentDirection>180){
                   apparentDirection=360-apparentDirection; 
                }
		double y = 90-apparentDirection;
		double a = apparentWind * Math.cos(Math.toRadians(y));
		double b = (apparentWind * Math.sin(Math.toRadians(y)))-vesselSpeed;
		return (Math.sqrt((a*a)+(b*b)));
	}



}
