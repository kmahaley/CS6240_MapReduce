package com.trialprogram;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;

public class CompositeGroupKey implements WritableComparable<CompositeGroupKey> {
	String name;
	String city;
	public CompositeGroupKey(){
		
	}
	public CompositeGroupKey(String name, String city) {
	    this.name = name;
	    this.city = city;
	}
	public void write(DataOutput out) throws IOException {
		out.writeUTF(name);
		out.writeUTF(city);
	}

	public void readFields(DataInput in) throws IOException {
		name = in.readUTF();
		city = in.readUTF();
	}

	@Override
	public int compareTo(CompositeGroupKey t) {
	    int cmp = this.name.compareTo(t.name);
	    if (cmp != 0) {
	        return cmp;
	    }
	    return this.city.compareTo(t.city);
	}    
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }
	    if (getClass() != obj.getClass()) {
	        return false;
	    }
	    final CompositeGroupKey other = (CompositeGroupKey) obj;
	    if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
	        return false;
	    }
	    if (this.city != other.city && (this.city == null || !this.city.equals(other.city))) {
	        return false;
	    }
	    return true;
	}
	@Override
	public int hashCode() {
	    return this.name.hashCode() * 163 + this.city.hashCode();
	}

	@Override
	public String toString() {
		return name+ ":" + city;
	}
}
