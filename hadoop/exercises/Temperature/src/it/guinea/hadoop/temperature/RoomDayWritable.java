package it.guinea.hadoop.temperature;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class RoomDayWritable implements WritableComparable<RoomDayWritable> {
	private Text date;
	private IntWritable mote;

	public RoomDayWritable() {
		this.date = new Text();
		this.mote = new IntWritable();
	}

	public RoomDayWritable(String date, int mote) {
		this.date = new Text(date);
		this.mote = new IntWritable(mote);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		this.mote.write(out);
		this.date.write(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.mote.readFields(in);
		this.date.readFields(in);
	}

	public Text getDate() {
		return this.date;
	}

	public void setDate(Text date) {
		this.date = date;
	}

	public IntWritable getMote() {
		return this.mote;
	}

	public void setMote(IntWritable mote) {
		this.mote = mote;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.date == null) ? 0 : this.date.hashCode());
		result = prime * result + ((this.mote == null) ? 0 : this.mote.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RoomDayWritable)) {
			return false;
		}
		RoomDayWritable other = (RoomDayWritable) obj;
		if (this.date == null) {
			if (other.date != null) {
				return false;
			}
		}
		else if (!this.date.equals(other.date)) {
			return false;
		}
		if (this.mote == null) {
			if (other.mote != null) {
				return false;
			}
		}
		else if (!this.mote.equals(other.mote)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.date + "\t" + this.mote;
	}

	@Override
	public int compareTo(RoomDayWritable o) {
		if (o == null) {
			throw new NullPointerException();
		}
		if (o == this) {
			return 0;
		}
		if (this.mote.compareTo(o.mote) > 0) {
			return 1;
		}
		else if (this.mote.compareTo(o.mote) == 0) {
			return this.date.compareTo(o.date);
		}
		else {
			return -1;
		}
	}

}
