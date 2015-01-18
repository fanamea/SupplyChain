package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.essentials.RepastEssentials;

public class ForecastAgent{
	
	private Business biz;
	private ArrayList<Link> linkList;
	private HashMap<Integer, Double> forecastTotal;
	private int movAvgTimeSpanPast;
	private int movAvgTimeSpanFuture;
	
	public ForecastAgent(){
		
	}
	
	public ForecastAgent(Business biz){
		this.biz = biz;
		this.linkList = biz.getDownstrLinks();
	}
	
	
	/**
	 * Berechnet auf Grundlage der Parameter (Objektvariablen) und den OrderAmountHistories in den Links den forecastTotal (Objektvariable)
	 */
	public void calcForecastTotal(int timeSpanFuture){
		HashMap<Integer, Double> totalForecast = new HashMap<Integer, Double>();
		int currentTick = (int)RepastEssentials.GetTickCount();
		for(Link link : linkList){
			ArrayList<Double> amountHistory = link.getOrderAmountHistory();
			//Eventuell seit der letzten Order fehlende Ticks ergänzen
			for(int i = amountHistory.size(); i<=currentTick; i++){
				amountHistory.add(i, 0.0);
			}
			HashMap<Integer, Double> linkForecast = getMovingAverageFC(amountHistory, this.movAvgTimeSpanPast, timeSpanFuture);
			//Für den ersten Schleifendurchlauf
			if(totalForecast.isEmpty()) totalForecast = linkForecast;
			//Linkforecast zum totalForecast addieren
			for(Integer i : totalForecast.keySet()){
				double sum = totalForecast.get(i) + linkForecast.get(i);
				totalForecast.put(i, sum);
			}			
		}
		forecastTotal = totalForecast;
		
	}
	
	public HashMap<Integer, Double> getOrderForecast(){
		return this.forecastTotal;
	}
	
	
	
	
	
	/*
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< FORECASTING ALGORITHMEN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	
	public HashMap<Integer, Double> getMovingAverageFC(ArrayList<Double> history, int timeSpanPast, int timeSpanFuture){
		ArrayList<Double> historyCopy = (ArrayList<Double>)history.clone();
		for(int i = 0; i<timeSpanFuture; i++){
			double sum = 0;
			for(int j = 0; j < timeSpanPast; j++){
				sum += historyCopy.get(historyCopy.size()-j-1);
			}
			double avg = sum/timeSpanPast;
			historyCopy.add(avg);
		}
		HashMap<Integer, Double> forecast = new HashMap<Integer, Double>();
		for(int i = history.size(); i<history.size() + timeSpanFuture; i++){
			forecast.put(i, historyCopy.get(i));
		}
		//System.out.println(history);
		//System.out.println(historyCopy);
		//System.out.println(forecast);
		return forecast;
	}

}
