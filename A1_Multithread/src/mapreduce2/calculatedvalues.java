package mapreduce2;

import java.util.ArrayList;
import java.util.List;

public class calculatedvalues {
	
	Double avg;
    Integer count;
    List<Double> listofprice=new ArrayList<Double>();

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Double> getlistofprice() {
        return listofprice;
    }

    public void setlistofprice(List<Double> listofprice) {
        this.listofprice = listofprice;
    }

	
}
