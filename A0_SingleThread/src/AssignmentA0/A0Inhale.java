package AssignmentA0;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;

public class A0Inhale {
	static Map<String, LinkedList<Double>> airlinecosts = new HashMap<String, LinkedList<Double>>();
	static Map<Double, String> finalairlinecosts = new TreeMap<Double, String>();

	static int TOTAL = 0;
	static int F = 0;
	static int K = 0;
	static int timezone = 0;

	public static void main(String[] args) {
		long starttime=System.currentTimeMillis();
		A0Inhale.readfromcsv();
		long stoptime=System.currentTimeMillis();
		long elaspedtime=stoptime-starttime;
		System.out.println();
		System.out.println("time taken= "+(elaspedtime)+" miliseconds");
	}

	public static void readfromcsv() {
		String csvfile = "323.csv";
		// BufferedReader br = null;
		// String line = "";
		// System.out.println("hello++");
		try {
			// System.out.println("move");
			CSVReader reader = new CSVReader(new FileReader(csvfile));
			String[] nextLine;
			// br = new BufferedReader(new FileReader(csvfile));
			while ((nextLine = reader.readNext()) != null) {
				// System.out.println("hello+++++");
				TOTAL++;
				// if(TOTAL<=2){
				// String[] airlinesdata = line.split(CHARACTER);
				if (nextLine[0].startsWith("YEAR")) {
					// System.out.println(nextLine);
					K++;
				} else {
					// if (nextLine.length >= 110) {

					if (nextLine[15].contains(",")) {

						String crsarrtime = nextLine[40];
						String crsdeptime = nextLine[29];
						String crselapsedtime = nextLine[50];

						if (crsarrtime.length() == 3) {
							crsarrtime = "0".concat(crsarrtime);
						}
						if (crsdeptime.length() == 3) {
							crsdeptime = "0".concat(crsdeptime);
						}

						boolean condition1 = crschecktime(crsarrtime, crsdeptime, crselapsedtime);

						String orgcityname = nextLine[15];
						String orgstate = nextLine[16];
						String orgstatename = nextLine[18];
						String destcityname = nextLine[24];
						String deststate = nextLine[25];
						String deststatename = nextLine[27];

						boolean condition2 = statecheckfields(orgcityname, orgstate, orgstatename, destcityname,
								deststate, deststatename);

						String orgairide = nextLine[11];
						String orgairseqid = nextLine[12];
						String orgcitymktid = nextLine[13];
						String orgstatefips = nextLine[17];
						String orgwac = nextLine[19];

						String destairide = nextLine[20];
						String destairseqid = nextLine[21];
						String destcitymktid = nextLine[22];
						String deststatefips = nextLine[26];
						String destwac = nextLine[28];
						boolean condition3 = airportcheckfields(orgairide, orgairseqid, orgcitymktid, orgstatefips,
								orgwac, destairide, destairseqid, destcitymktid, deststatefips, destwac);

						String cancelled = nextLine[47];
						String arrdelay = nextLine[42];
						String arrdelaymins = nextLine[43];
						String arrdelay15 = nextLine[44];

						String arrtime = nextLine[41];
						String deptime = nextLine[30];
						String actelapsedtime = nextLine[51];
						if (arrtime.length() == 3) {
							arrtime = "0".concat(arrtime);
						}
						if (deptime.length() == 3) {
							deptime = "0".concat(deptime);
						}

						String airlinecode = nextLine[6];
						Double price = Double.parseDouble(nextLine[109]);

						boolean condition4 = cancellation(cancelled, arrdelay, arrdelaymins, arrdelay15, arrtime,
								deptime, actelapsedtime);

						if (condition1 && condition2 && condition3 && condition4) {

							F++;

							LinkedList<Double> list;
							if (!airlinecosts.containsKey(airlinecode)) {
								list = new LinkedList<Double>();
								list.add(price);
								airlinecosts.put(airlinecode, list);
							} else {
								list = airlinecosts.get(airlinecode);
								list.add(price);
								airlinecosts.put(airlinecode, list);
							}

						} else {
							// System.out.println("condition1"+ condition1);
							// System.out.println("condition2"+ condition2);
							// System.out.println("condition3"+ condition3);
							// System.out.println("condition4"+ condition4);
						}
					} else {
						K++;
					}
					// }
				} // }else{System.out.println(K + " " + F + " " +
					// TOTAL);System.exit(0);}
			}
			reader.close();
			System.out.println(K + " " + F);
			getairlinescost();

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static boolean crschecktime(String crsarrtime, String crsdeptime, String crselapsedtime) {

		try {

			if ((!crsarrtime.equals(null) && !crsarrtime.isEmpty())
					&& (!crsdeptime.equals(null) && !crsdeptime.isEmpty())
					&& (!crselapsedtime.equals(null) && !crselapsedtime.isEmpty())) {

				int arrtime = Integer.parseInt(crsarrtime);
				int deptime = Integer.parseInt(crsdeptime);
				timezone = converttominutes(crsarrtime) - converttominutes(crsdeptime)
						- Integer.parseInt(crselapsedtime);

				return (arrtime > 0 && deptime > 0 && (timezone % 60) == 0);

			} else {
				return false;
			}

		} catch (NumberFormatException e) {
			return false;
		}

	}

	private static int converttominutes(String string1) {
		// TODO Auto-generated method stub
		int giventime = 0;

		if (string1.length() > 3) {
			String hrs = string1.substring(0, 2);
			String mins = string1.substring(2);
			giventime = Integer.parseInt(hrs) * 60 + Integer.parseInt(mins);
		} else {
			String hrs = "0";
			String mins = string1;
			giventime = Integer.parseInt(hrs) * 60 + Integer.parseInt(mins);
		}
		return giventime;
	}

	private static boolean statecheckfields(String orgcityname, String orgstate, String orgstatename,
			String destcityname, String deststate, String deststatename) {

		// System.out.println(string1+" "+string2+" "+string3+" "+string4+"
		// "+string5+" "+string6);
		return (!orgcityname.isEmpty() && !orgstate.isEmpty() && !orgstatename.isEmpty() && !destcityname.isEmpty()
				&& !deststate.isEmpty() && !deststatename.isEmpty());

	}

	private static boolean airportcheckfields(String string1, String string2, String string3, String string4,
			String string5, String string6, String string7, String string8, String string9, String string10) {

		try {
			if ((!string1.equals(null) && !string1.isEmpty()) && (!string2.equals(null) && !string2.isEmpty())
					&& (!string3.equals(null) && !string3.isEmpty()) && (!string4.equals(null) && !string4.isEmpty())
					&& (!string5.equals(null) && !string5.isEmpty()) && (!string6.equals(null) && !string6.isEmpty())
					&& (!string7.equals(null) && !string7.isEmpty()) && (!string8.equals(null) && !string8.isEmpty())
					&& (!string9.equals(null) && !string9.isEmpty())
					&& (!string10.equals(null) && !string10.isEmpty())) {

				// System.out.println(string1+" "+string2+" "+string3+"
				// "+string4+" "+string5+" "+
				// string6+" "+string7+" "+string8+" "+string9+" "+string10);

				int orgid = Integer.parseInt(string1);
				int orgseqid = Integer.parseInt(string2);
				int orgmktid = Integer.parseInt(string3);
				int orgstatefips = Integer.parseInt(string4.replace("\"", ""));
				int orgwac = Integer.parseInt(string5);

				int depid = Integer.parseInt(string6);
				int depseqid = Integer.parseInt(string7);
				int depmktid = Integer.parseInt(string8);
				int depstatefips = Integer.parseInt(string9.replace("\"", ""));
				int depwac = Integer.parseInt(string10);

				return (orgid > 0 && orgseqid > 0 && orgmktid > 0 && orgstatefips > 0 && orgwac > 0 && depid > 0
						&& depseqid > 0 && depmktid > 0 && depstatefips > 0 && depwac > 0);

			} else {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static boolean cancellation(String cancelled, String arrdelay, String arrdelaymins, String arrdelay15,
			String arrtime, String deptime, String actelapsedtime) {

		// System.out.println(cancelled+" "+arrdelay+" "+arrdelaymins+"
		// "+arrdelay15+" "+arrtime+" "+deptime+" "+actelapsedtime);

		// boolean arenumbers=false;
		// if(isnum(arrtime) && isnum(deptime) && isnum(actelapsedtime)){
		//
		// arenumbers=true;
		// }
		if (StringUtils.isNotEmpty(arrtime)) {
			if (!cancelled.equals("1") && ((converttominutes(arrtime) - converttominutes(deptime)
					- Integer.parseInt(actelapsedtime) - timezone) % 24 == 0)
					&& checkarrdelay(arrdelay, arrdelaymins, arrdelay15)) {
				return true;

			} else {
				return false;
			}
		}

		return true;

	}

	private static boolean checkarrdelay(String string1, String string2, String string3) {

		double arrdelay = Double.parseDouble(string1);
		double arrdelaymins = Double.parseDouble(string2);
		double arrdelay15 = Double.parseDouble(string3);

		// System.out.println(arrdelay+" "+arrdelaymins+" "+arrdelay15);

		if (arrdelay > 0 && arrdelay != arrdelaymins)
			return false;
		if (arrdelay < 0 && arrdelaymins != 0)
			return false;
		if (arrdelaymins >= 15 && arrdelay15 == 0)
			return false;

		return true;
	}

	public static boolean isnum(String field) {
		return StringUtils.isNumeric(field);
	}

	private static void getairlinescost() {
		// static Map<String, LinkedList<Double>> airlinecosts = new
		// HashMap<String, LinkedList<Double>>();
		// static Map<Double, String> finalairlinecosts = new TreeMap<Double,
		// String>();

		for (Entry<String, LinkedList<Double>> e : airlinecosts.entrySet()) {
			String code = e.getKey();
			LinkedList<Double> costs = e.getValue();
			double totalcost = 0;

			for (double c : costs) {
				totalcost = totalcost + c;
			}
			double avg = 0;
			avg = totalcost / costs.size();
			finalairlinecosts.put(avg, code);
		}

		for (Entry<Double, String> e : finalairlinecosts.entrySet()) {
			System.out.println(e.getValue() + " " + e.getKey());
		}

	}
}
