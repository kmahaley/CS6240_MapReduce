package mapreduce2;
/*Earlier A0 assignment I had hardcoded the column index
 * this time I have dynamically handled column names these name of columns are 
 * collabrated with Pankaj Tripathi
 * */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;

public class Processor implements Runnable {

	int TOTAL = 0;
	int F = 0;
	int K = 0;
	HashMap<String,calculatedvalues> processormap;
	
	File filename = null;

	public Processor(File f) {

		this.filename = f;
		// System.out.println("File name= "+this.filename);
	}

	@Override
    public void run() {
        if (filename.isFile()) {
 
            //String csvFile = filename;
            CSVReader reader=null;
            String data[];

            String Origin, Destination,OriginCityName,DestCityName,OriginStateAbr,DestStateAbr,OriginStateName,DestStateName,Cancelled,Carrier,Price;
            String OriginAirportID=null,DestAirportID= null,OriginAirportSeqID = null,DestAirportSeqID= null,OriginCityMarketID = null,DestCityMarketID= null,OriginStateFips= null,DestStateFips= null,
                    OriginWac= null,DestWac= null,FlDate=null;

            int timeZone = 0;
            String  CRSArrTime,CRSDepTime,CRSElapsedTime,ArrTime,DepTime,ActualElapsedTime,ArrDelay,ArrDelayMinutes = null,ArrDel15;
            int    CRSArrTimeIdx = 0,CRSDepTimeIdx = 0,OriginAirportIDIdx= 0,DestAirportIDIdx= 0,OriginAirportSeqIDIdx= 0,DestAirportSeqIDIdx= 0,OriginCityMarketIDIdx= 0,DestCityMarketIDIdx= 0,
                    OriginStateFipsIdx= 0,DestStateFipsIdx= 0,OriginWacIdx= 0,DestWacIdx= 0,OriginIdx= 0, DestinationIdx= 0,OriginCityNameIdx= 0,DestCityNameIdx= 0,
                    OriginStateAbrIdx= 0,DestStateAbrIdx= 0,OriginStateNameIdx= 0,DestStateNameIdx= 0,ArrTimeIdx= 0,DepTimeIdx= 0,ActualElapsedTimeIdx= 0,ArrDelayMinIdx=0,
                    CRSElapsedTimeIdx = 0,ArrDelayIdx= 0,ArrDel15Idx= 0,CancelledIdx=0,PriceIdx=0,CarrIdx=0,insanedata=0,sanedata=0,FlDateIdx=0;
            List<Double> list;
            TreeMap<String,List<Double>> map= new TreeMap<String, List<Double>>();
            TreeMap<Double, String> avgPrice=new TreeMap<Double, String>();
            processormap=new HashMap<String,calculatedvalues>();


            try {
            	/*filename is the object file given by the task which is submitted to executor service*/
                reader = new CSVReader(new FileReader(filename));
                String col[] = reader.readNext();
                insanedata++;

                for(int i=0;i<col.length;i++){

                    if(col[i].equals("CRS_ARR_TIME"))
                        CRSArrTimeIdx = i;
                    if(col[i].equals("CRS_DEP_TIME"))
                        CRSDepTimeIdx = i;
                    if(col[i].equals("ORIGIN_AIRPORT_ID"))
                        OriginAirportIDIdx = i;
                    if(col[i].equals("DEST_AIRPORT_ID"))
                        DestAirportIDIdx = i;
                    if(col[i].equals("DEST_AIRPORT_SEQ_ID"))
                        DestAirportSeqIDIdx = i;
                    if(col[i].equals("ORIGIN_AIRPORT_SEQ_ID"))
                        OriginAirportSeqIDIdx = i;
                    if(col[i].equals("ORIGIN_CITY_MARKET_ID"))
                        OriginCityMarketIDIdx = i;
                    if(col[i].equals("DEST_CITY_MARKET_ID"))
                        DestCityMarketIDIdx = i;
                    if(col[i].equals("ORIGIN_WAC"))
                        OriginWacIdx = i;
                    if(col[i].equals("DEST_WAC"))
                        DestWacIdx = i;
                    if(col[i].equals("ORIGIN"))
                        OriginIdx = i;
                    if(col[i].equals("DEST"))
                        DestinationIdx = i;
                    if(col[i].equals("ORIGIN_CITY_NAME"))
                        OriginCityNameIdx = i;
                    if(col[i].equals("DEST_CITY_NAME"))
                        DestCityNameIdx = i;
                    if(col[i].equals("ORIGIN_STATE_ABR"))
                        OriginStateAbrIdx = i;
                    if(col[i].equals("DEST_STATE_ABR"))
                        DestStateAbrIdx = i;
                    if(col[i].equals("ORIGIN_STATE_NM"))
                        OriginStateNameIdx = i;
                    if(col[i].equals("DEST_STATE_NM"))
                        DestStateNameIdx = i;
                    if(col[i].equals("ARR_TIME"))
                        ArrTimeIdx = i;
                    if(col[i].equals("DEP_TIME"))
                        DepTimeIdx = i;
                    if(col[i].equals("ACTUAL_ELAPSED_TIME"))
                        ActualElapsedTimeIdx = i;
                    if(col[i].equals("CRS_ELAPSED_TIME"))
                        CRSElapsedTimeIdx = i;
                    if(col[i].equals("ARR_DELAY"))
                        ArrDelayIdx = i;
                    if(col[i].equals("ARR_DELAY_NEW"))
                        ArrDelayMinIdx = i;
                    if(col[i].equals("ARR_DEL15"))
                        ArrDel15Idx = i;
                    if(col[i].equals("CANCELLED"))
                        CancelledIdx = i;
                    if(col[i].equals("AVG_TICKET_PRICE"))
                        PriceIdx = i;
                    if(col[i].equals("CARRIER"))
                        CarrIdx = i;
                    if(col[i].equals("FL_DATE"))
                        FlDateIdx = i;

                }

                while((data=reader.readNext())!=null){
                	TOTAL++;
                	/*
					 * These contains sane data lines, After careful checking of files some lines with
					 * 109 columns contain correct data.Not sure if that is sane data or not because
					 * which columns is missing data ?
					 * */
                    if(data.length<110)
                    {
                        insanedata++;
                        continue;
                    }

                    CRSArrTime=data[CRSArrTimeIdx];
                    CRSDepTime=data[CRSDepTimeIdx];
                    CRSElapsedTime=data[CRSElapsedTimeIdx];

                    OriginAirportID=data[OriginAirportIDIdx];
                    DestAirportID=data[DestAirportIDIdx];
                    OriginAirportSeqID=data[OriginAirportSeqIDIdx];
                    DestAirportSeqID=data[DestAirportSeqIDIdx];
                    OriginCityMarketID=data[OriginCityMarketIDIdx];
                    DestCityMarketID=data[DestCityMarketIDIdx];
                    OriginStateFips=data[OriginStateFipsIdx];
                    DestStateFips=data[DestStateFipsIdx];
                    OriginWac=data[OriginWacIdx];
                    DestWac=data[DestWacIdx];


                    /*
					 * CRSArrTime, CRSDepTime containis data in HHmm format hence converting them to
					 * mins
					 * */

                    if(istime(CRSArrTime)&&istime(CRSDepTime)&&istime(CRSElapsedTime)){
                        timeZone=converttomins(CRSArrTime)-converttomins(CRSDepTime)-Integer.parseInt(CRSElapsedTime);

                    }else{
                        insanedata++;
                        continue;
                    }

                    /*
					 * Sanity test for timeZone as per professor's condition
					 * */
                    if((timeZone%60)!=0){
                        insanedata++;
                        continue;
                    }

                    /*
					 * to check if all IDs are not null or empty and are numeric
					 * convert to integer to check validity of the data
					 * */
                    if(isidvalid(OriginAirportID) && isidvalid(DestAirportID) && isidvalid(OriginAirportSeqID) && isidvalid(DestAirportSeqID) && isidvalid(OriginCityMarketID) && isidvalid(DestCityMarketID)
                            && isidvalid(OriginStateFips) && isidvalid(DestStateFips) && isidvalid(OriginWac) && isidvalid(DestWac)) {
                        if(Integer.parseInt(OriginAirportID)<0||Integer.parseInt(DestAirportID)<0||Integer.parseInt(OriginAirportSeqID)<0||Integer.parseInt(DestAirportSeqID)<0||
                                Integer.parseInt(OriginCityMarketID)<0||Integer.parseInt(DestCityMarketID)<0||Integer.parseInt(OriginStateFips)<0||Integer.parseInt(DestStateFips)<0||
                                Integer.parseInt(OriginWac)<0||Integer.parseInt(DestWac)<0){
                            insanedata++;
                            continue;
                        }
                    }else{
                        insanedata++;
                        continue;
                    }

                    Cancelled=data[CancelledIdx];
                    Origin=data[OriginIdx];
                    Destination=data[DestinationIdx];
                    OriginCityName=data[OriginCityNameIdx];
                    DestCityName=data[DestCityNameIdx];
                    OriginStateAbr=data[OriginStateAbrIdx];
                    DestStateAbr=data[DestStateAbrIdx];
                    OriginStateName=data[OriginStateNameIdx];
                    DestStateName=data[DestStateNameIdx];

                    ArrTime=data[ArrTimeIdx];
                    DepTime=data[DepTimeIdx];
                    ActualElapsedTime=data[ActualElapsedTimeIdx];
                    ArrDelay=data[ArrDelayIdx];
                    ArrDelayMinutes=data[ArrDelayMinIdx];
                    ArrDel15=data[ArrDel15Idx];

                    /*
					 * airport fields should not be empty.
					 * */
                    if(Origin.equals("") || Destination.equals("") || OriginCityName.equals("") || DestCityName.equals("") || OriginStateAbr.equals("") || DestStateAbr.equals("") ||
                            OriginStateName.equals("") || DestStateName.equals("") )
                    {
                        insanedata++;
                        continue;
                    }

                    /*
					 * Sanity test for flights not cancelled.
					 * */
                    if(Cancelled.equals("1")){

                        if(isNum(ArrTime) && isNum(DepTime) && isNum(ActualElapsedTime) && isNum(ArrDelay) && isNum(ArrDelayMinutes) && isNum(ArrDel15))
                            if((converttomins(ArrTime)-converttomins(DepTime)-Integer.parseInt(ActualElapsedTime)-timeZone==0))
                            {
                                insanedata++;
                                continue;
                            }
                        if(isNum(ArrDelay) && isNum(ArrDelayMinutes) && isNum(ArrDel15))
                            if(!checkarrdelay(ArrDelay, ArrDelayMinutes, ArrDel15)){
                                insanedata++;
                                continue;
                            }
                    }

                  //if the flights passes every sanity tests given then they are sane flights.
                    // consider them for further analysis
                    sanedata++;


                    Carrier=data[CarrIdx];
                    Price=data[PriceIdx];
                    FlDate=data[FlDateIdx];

                    /*
					 *Add to carrier code and price as list if airline is active during
					 *January 2015
					 * */
                    if(isdatevalid(FlDate)){
                        if(map.containsKey(Carrier)){
                            List<Double> val=map.get(Carrier);
                            val.add(Double.parseDouble(Price));
                            map.put(Carrier, val);
                        }
                        else{
                            list=new ArrayList<Double>();
                            list.add(Double.parseDouble(Price));
                            map.put(Carrier, list);
                        }
                    }
                }

                double avg=0;
                List<Double> value=new ArrayList<Double>();
                Set set = map.entrySet();
                Iterator i = set.iterator();

                /*
				 * Retrieve the carrier and list of prices map.
				 * calculate avg of the prices
				 * */
                while(i.hasNext()) {
                    Map.Entry me = (Map.Entry)i.next();
                    value=(List<Double>)me.getValue();
                    double size=value.size();

                    for(Double d:value)
                        avg=avg+d;

                    avg=avg/size;
                    //avgPrice.put(avg,(String)me.getKey());

                    calculatedvalues req=new calculatedvalues();
                        req.setAvg(avg);
                        req.setCount((int)size);
                        java.util.Collections.sort(value);
                        req.setlistofprice(value);
                        processormap.put((String)me.getKey(),req);
                }


                K=insanedata;
                F=sanedata;

                Set priceSet=avgPrice.entrySet();
                Iterator it=priceSet.iterator();

                                reader.close();
            }
            catch (FileNotFoundException fe) {

                fe.printStackTrace();
            }
            catch (IOException ie) {

                ie.printStackTrace();
            }


        }

    }

	
		public boolean isNum(String field){
			return StringUtils.isNumeric(field);
		}

		
		public boolean istime(String field){

			if(!field.isEmpty()&&StringUtils.isNumeric(field)&&!field.equals("0")&&!field.equals(null)){
				return true;
			}else{
				return false;
			}
		}

		
		public boolean isidvalid(String field){	

			if( StringUtils.isNotBlank(field)&&field!=null && StringUtils.isNumeric(field))
				return true;
			else
				return false;
		}

		// Helper to convert ArrTime DeptTime and other such fields in format hhmm in minutes.
		public int converttomins(String field){
			int giventime = 0;

			if(field.length()==3){
				field="0".concat(field);
			}

			if (field.length() > 3) {
				String hrs = field.substring(0, 2);
				String mins = field.substring(2);

				giventime = Integer.parseInt(hrs) * 60 + Integer.parseInt(mins);
			} else {
				String hrs = "0";
				String mins = field;

				giventime = Integer.parseInt(hrs) * 60 + Integer.parseInt(mins);
			}
			return giventime;
		}

		//Helper to sanity check of arrdelay arrdelaymins arrdelay15
		private static boolean checkarrdelay(String field1, String field2, String field3) {

			double arrdelay=Double.parseDouble(field1);
			double arrdelaymins=Double.parseDouble(field2);
			double arrdelay15=Double.parseDouble(field3);

			if(arrdelay>0 && arrdelay==arrdelaymins)
				return true;
			if(arrdelay<0 && arrdelaymins ==0)
				return true;
			if(arrdelaymins>=15 && arrdelay15==1 )
				return true;

			return false;
		}
		
		public static boolean isdatevalid(String inputdate){
	        try {
	        	
	            SimpleDateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy");
	            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
	            Date date;
	        	Date fromdate=formatter1.parse("12/31/2014");;
	        	Date todate=formatter1.parse("2/1/2015");
	            
	            if(inputdate.contains("/")){
	            	date = formatter1.parse(inputdate);
		             

	            }else{
	            	date = formatter2.parse(inputdate);
	            }
	            	
	            	
	            if(date.compareTo(fromdate) > 0 && date.compareTo(todate) < 0)
	                return true;
	            
	        } catch (ParseException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return false;
	    }

	

	// ///////////////////////////
}
