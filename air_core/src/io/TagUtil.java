package io;

import java.net.DatagramPacket;

public class TagUtil {
	/**
	 * 
	 */
	private String strLatitude;
	
	private String strLongitude;
	/**
	 * @param payload
	 * @return
	 */
	public float extractedBattery(byte[] payload,int startIndex) {
		return Float.parseFloat(payload[startIndex] + "." + payload[startIndex+1]);
	}

	/**
	 * 도 표기법
	 * @return
	 */
	private double doNotation(byte a, byte b, byte c)
	{
		double tempLatitude = (Double.parseDouble(String.valueOf(a))
				+ (Double.parseDouble(String.valueOf(b)) * 0.01)
				+ (Double.parseDouble(String.valueOf(c)) * 0.0001)) / 60;
		return tempLatitude;
	}
	
	
	/**위치 정보 추출
	 * @param payload
	 * @return
	 */
	public double[] extractedLatLng(byte[] payload) {
		//logger.debug("extracted latlng start...");
		//logger.debug("extracted lat data");
		/*logger.debug("palyload data=>palyload[7]:"+payload[7]+
									" palyload[8]:"+payload[8]+
									" palyload[9]:"+payload[9]+
									" palyload[10]:"+payload[10]);*/

		double[] temp_latlng = new double[2];

		/*
		 * 위도 좌표 계산
		 */
		strLatitude = payload[7] + "." + payload[8] + payload[9] + payload[10];
		//logger.debug("도분 표기법(lat):"+ payload[7] + "." + payload[8] + payload[9] + payload[10]);				
		// 도분 표기법 ---------> 도 표기법으로 변환		

		temp_latlng[0] = Double.parseDouble(String.format("%.6f", (Double.valueOf(payload[7]) + doNotation(payload[8], payload[9], payload[10]))));
		//logger.debug("<lat result:"+temp_latlng[0]+">");


		/*
		 * 경위 자표 계산
		 */
		strLongitude = (payload[12] & 0xFF) + "." + payload[13] + payload[14] + payload[15];
		/*logger.debug("도분 표기법(lng):"+(payload[12] & 0xFF) + "." + payload[13] + payload[14] + payload[15]);
		logger.debug("extracted lng data");
		logger.debug("palyload data=>palyload[12]:"+payload[12]+
									" palyload[13]:"+payload[13]+
									" palyload[14]:"+payload[14]+
									" palyload[15]:"+payload[15]);*/

		// 도분 표기법 ---------> 도 표기법으로 변환	
		temp_latlng[1] = Double.parseDouble(String.format("%.6f", (Double.valueOf((payload[12]) & 0xFF) +  doNotation(payload[13], payload[14], payload[15]))));
		/*		logger.debug("<lng result:"+temp_latlng[1]+">");

		logger.debug("extracted latlng end...");*/

		//temp_latlng[0] = Double.valueOf(strLatitude); 
		//temp_latlng[1] = Double.valueOf(strLongitude);
		return temp_latlng;
	}
	
	/**위치 정보 추출
	 * 
	 * @구조 위도(4byte), 위도 방향(1byte), 경도(4byte), 경도 방향(1byte)
	 * 위도 방향('N','S')
	 * 경도 방향('E','W')
	 * @param payload
	 * @return
	 */
	public double[] extractedLatLng(byte[] payload, int startIndex) {

		int latStartIndex=startIndex;

		double[] temp_latlng = new double[2];

		/*
		 * 위도 좌표 계산
		 * 4byte: 위도
		 * 1byt : 위도 방향
		 * 
		 */
		strLatitude = (payload[11] & 0xFF) + "." + 
		             String.format("%02d",payload[12]) + 
		             String.format("%02d",payload[13]) + 
		             String.format("%02d",payload[14]);
		
		

		// 도분 표기법 ---------> 도 표기법으로 변환		

		temp_latlng[0] = Double.parseDouble(String.format("%.6f", (Double.valueOf(payload[latStartIndex]) 
				+ doNotation(payload[latStartIndex+1], payload[latStartIndex+2], payload[latStartIndex+3]))));

		/*
		 * 경도 좌표 계산
		 * 4byte: 경도
		 * 1byt : 경도 방향
		 */
		int lngStartIndex = startIndex+5;// latidue-d:4+latidue:1
		strLongitude = (payload[15] & 0xFF) + "." + 
		              String.format("%02d",payload[16]) + 
				      String.format("%02d",payload[17]) + String.format("%02d",payload[18]);
		//logger.debug("도분 표기법(lng):"+(payload[12] & 0xFF) + "." + payload[13] + payload[14] + payload[15]);


		System.out.println(strLatitude+","+strLongitude);


		// 도분 표기법 ---------> 도 표기법으로 변환	
		temp_latlng[1] = Double.parseDouble(String.format("%.6f", (Double.valueOf((payload[lngStartIndex]) & 0xFF) +  doNotation(payload[lngStartIndex+1], payload[lngStartIndex+2], payload[lngStartIndex+3]))));
		
		
		byte lat[] = new byte[4];
		byte lng[] = new byte[4];
		lat[0] = payload[startIndex++];
		lat[1] = payload[startIndex++];
		lat[2] = payload[startIndex++];
		lat[3] = payload[startIndex++];
		
		lng[0] = payload[startIndex++];
		lng[1] = payload[startIndex++];
		lng[2] = payload[startIndex++];
		lng[3] = payload[startIndex++];

		temp_latlng[0] =TagUtil.convertByteToDouble(lat);
		temp_latlng[1] =TagUtil.convertByteToDouble(lng);

		return temp_latlng;
	}

	public String extractedTagIP(DatagramPacket inPacket) {
		return inPacket.getAddress().getHostAddress().toString() + ":" + inPacket.getPort();
	}
	/**태그 아이디 추출-
	 * 전체길이: 8byte
	 * 1. country : 1byte
	 * 2. company : 2byte
	 * 3. type 	  : 1byte
	 * 4. number  : 4byte 
	 * @param payload
	 * @return
	 */
	public String extractedTID(byte[] payload, int startIndex) {

		StringBuffer buffer = new StringBuffer();

		for(int i=0;i<8;i++,startIndex++)
		{
			buffer.append(String.format("%02X",payload[startIndex]));
			
		}
		return buffer.toString();
	}



	/**
	 * 바이트 16진수 표현
	 * 바이트는 8비트니까  16진수면 2자리로 표현
	 * @param data
	 * @return
	 */
	private String toHex(byte data)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.toString((data&0xF0)>>4,16));

		sb.append(Integer.toString((data&0x0F),16));
		return sb.toString();

	}

	/**충격 조건 추출
	 * @param payload
	 * @return
	 */
	public String[] extractHit(byte[] payload, int startIndex) {

		String[] tempHit = new String[3];
		try{
			tempHit[0] = String.valueOf(payload[startIndex] + "." + payload[startIndex+1]);
			tempHit[1] = String.valueOf((payload[startIndex+2] & 0xFF) + "." + (payload[startIndex+3] & 0xFF));
			tempHit[2] =String.valueOf((payload[startIndex+4] & 0xFF) + "." + (payload[startIndex+5] & 0xFF));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return tempHit;
	}

	public String extractStrHit(byte[] payload) {

		String[] tempHit = new String[3];
		try{
			tempHit[0] = (payload[19] + "." + payload[20]);
			tempHit[1] = (payload[21] & 0xFF) + "." + (payload[22] & 0xFF);
			tempHit[2] = (payload[23] & 0xFF) + "." + (payload[24] & 0xFF);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return payload[19] + "." + payload[20]+"."+(payload[21] & 0xFF) + "." + (payload[22] & 0xFF)+"."+(payload[23] & 0xFF) + "." + (payload[24] & 0xFF);
	}
	/**온도 정보 추출
	 * @param payload
	 * @return
	 */
	public short extractTemperature(byte[] payload,int temperature_index) {

		// temp index:17
		short temp_temperature;
		if (String.format("%08d", Integer.parseInt(Integer.toBinaryString((payload[temperature_index]) & 0xFF))).charAt(0) == '1') 
		{
			temp_temperature = Short.parseShort("-" + Integer.parseInt(String.format("%08d", Integer.parseInt(Integer.toBinaryString((payload[17]) & 0xFF).substring(1))), 2));
		}
		else
		{
			temp_temperature = (short) Integer.parseInt(Integer.toBinaryString((payload[temperature_index]) & 0xFF), 2);
		}
		return temp_temperature;
	}
	/**
	 *소수점 6자리 변환 
	 */
	public static final byte[] convertDoubleToByte(double value)
	{
		byte[] data = new byte[4];

		String strValue =String.format("%.6f",value);
		
		String lat1 = strValue.substring(0, strValue.indexOf("."));

		String lat2 = strValue.substring(strValue.indexOf(".")+1, strValue.length());
		
		data[0] =(byte) Integer.parseInt(lat1);
		data[1] =(byte) Integer.parseInt(lat2.substring(0, 2));
		data[2] =(byte) Integer.parseInt(lat2.substring(2, 4));
		data[3] =(byte) Integer.parseInt(lat2.substring(4, 6));
		
		return data;
	}
	public static final double convertByteToDouble(byte[] value)
	{
		
		String strValue = (value[0] & 0xFF) + "." + 
				String.format("%02d",value[1] & 0xFF)+ 
				String.format("%02d",value[2] & 0xFF)+
				String.format("%02d",value[3] & 0xFF);
		return Double.valueOf(strValue);

	}
}
