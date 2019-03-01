package loader.filter.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import loader.filter.CommitFilter;
import model.Commit;

public class DateFilter implements CommitFilter {

	private Date date;
	
	public DateFilter(String dateString) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss Z");
		this.date = dateFormat.parse(dateString);
	}
	public boolean filter(Commit commit) {
		
		if(commit.getDate()!=null && commit.getDate().before(this.date)) {
			return false;
		}
		return true;

	}
}