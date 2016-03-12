package mapreduce2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class A1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();

		System.out.println("Program is executing...");
		
		List<Processor> listofprocessors=new ArrayList<Processor>();
		
		//String target_dir = "C:\\Users\\kmaha\\OneDrive\\Documents\\NEU Masters 2014-16\\MS @ NEU\\Semester4\\Mapreduce\\Assignment2\\csvfiles";
		String target_dir = args[1].split("=")[1];
		System.out.println(target_dir);
		File dir = new File(target_dir);
		File[] files = dir.listFiles();
		//System.out.println(files.length);
		
		/* ExecutorService manages the threads I have assigned value of threads as 8
		 * because for threads between 4-10 code was performing relatively faster
		 * */
		ExecutorService executor=Executors.newFixedThreadPool(8);
		
		/*Submission of task to the executor service, these task should be runnable
		 * */
		Processor p;
		for (File f : files) {
			//System.out.println(f);
			p=new Processor(f);
			listofprocessors.add(p);
			executor.submit(p);
			
		}
		//Shutting down the executor service so that it accepts no more tasks
		executor.shutdown();
		
		
		try {
			/*
			 * After all threads have returned next step of processing begins
			 * Iterating for all 25 task for which K, F and Map values are ready*/
			if(executor.awaitTermination(5, TimeUnit.MINUTES)){
				int badlines=0;
				int goodlines=0;
				int totallines=0;
				HashMap<String,calculatedvalues> results=new HashMap<String,calculatedvalues>();
				//System.out.println(listofprocessors.size());
				
				/*Iterating over all the objects we received from the tasks*/
				for(Processor eachproc : listofprocessors){
					badlines=badlines+eachproc.K;
					goodlines=goodlines+eachproc.F;
					totallines=totallines+eachproc.TOTAL;
					
					
					for (Map.Entry<String, calculatedvalues> entry : eachproc.processormap.entrySet()) {
						
					    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
					    String code=entry.getKey();
					    calculatedvalues val=entry.getValue();
			    
					    
					    calculatedvalues finalval;
						if (!results.containsKey(code)) {
							results.put(code, val);
						} else {
							finalval = results.get(code);
							finalval.setAvg((finalval.getAvg()*finalval.getCount()+val.getAvg()*val.getCount())
									/(finalval.getCount()+val.getCount()));
							finalval.setCount(finalval.getCount()+val.getCount());
							
							List<Double> newList = new ArrayList<Double>(finalval.getlistofprice());
							newList.addAll(val.getlistofprice());
							
							java.util.Collections.sort(newList);
							finalval.setlistofprice(newList);
							results.put(code, finalval);
						}

					    
					}   
					}
					System.out.println("K= "+badlines);
					System.out.println("F= "+goodlines);
					TreeMap<String, String> toprinttreemap=new TreeMap<String, String>();
					
					/*to calculate median*/
					for (Map.Entry<String, calculatedvalues> entry : results.entrySet()) {
						
						double median=0;
						
						int arraysize=entry.getValue().getlistofprice().size();
						int middle=entry.getValue().getlistofprice().size()/2;
						
						if((arraysize%2)==1){
							median=entry.getValue().getlistofprice().get(middle);
						}else{
							median=(entry.getValue().getlistofprice().get(middle-1)+
									entry.getValue().getlistofprice().get(middle))/2.0;
									
						}
						
						toprinttreemap.put(entry.getValue().getAvg()+" "+median, entry.getKey());
						
						//System.out.println(entry.getKey()+" "+entry.getValue().getAvg()+" "+median);
						
					}
					
					for(Map.Entry<String, String> entry:toprinttreemap.entrySet()){
						System.out.println(entry.getValue()+" "+entry.getKey());
					}
					
				
				
				
			}else{
				System.out.println("Timeout has occured due towhich task are incomplete");
			}
			
			//System.out.println(proc.size());
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		long stop = System.currentTimeMillis();
		System.out.println("Time taken: " + (stop - start) / 1000);

	}



}
