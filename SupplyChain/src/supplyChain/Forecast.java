package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

public class Forecast{
	
	public int movingAverage(ArrayList<Integer> history, int timeSpan){
		double avg = 0;
		for(int i = 0; i < timeSpan; i++){
			avg = avg + history.get(history.size()-i);
		}
		return (int)(avg/timeSpan);
	}

}
