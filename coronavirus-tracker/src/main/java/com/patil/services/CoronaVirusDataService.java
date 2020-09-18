package com.patil.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.patil.models.LocationStats;

@Service
public class CoronaVirusDataService {
	private static  String csvFile="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private List<LocationStats> allStats=new ArrayList<>();
	
	@PostConstruct
	@Scheduled(cron = "* * * 1 * *")
	public  void read() {
		try {
			List<LocationStats> newStats=new ArrayList<>();
			URL url = new URL(csvFile); 
			HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
			con.setRequestMethod("GET");
			con.connect();
			 int code = con.getResponseCode();
		        System.out.println("Response code of the object is "+code);
		        if (code==200)
		        {
		            System.out.println("OK");
		        }
		        BufferedReader csvReader = new BufferedReader(new InputStreamReader(
		                con.getInputStream()));
		        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(csvReader);
		        for (CSVRecord record : records) {
		        	LocationStats locationStats=new LocationStats();
		        	locationStats.setCountry(record.get("Country/Region"));
		        	
		        	locationStats.setState(record.get("Province/State"));
		        	
		        	Integer latestCases=Integer.parseInt(record.get(record.size()-1));
		        	Integer  previousDayCases=Integer.parseInt(record.get(record.size()-2));
		        	
		        	locationStats.setLatestTotalCases(latestCases);
		        	locationStats.setDiffFromThePreDay(latestCases-previousDayCases);
		           
		        	System.out.println(locationStats);
		        	
		        	newStats.add(locationStats);
		        }
		      
		       this.allStats=newStats;


		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}

	public List<LocationStats> getAllStats() {
		return allStats;
	}

	public void setAllStats(List<LocationStats> allStats) {
		this.allStats = allStats;
	}


}
