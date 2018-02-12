package com.raffa.microsegmentationcontroller;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JodaDateAdapter extends TypeAdapter<DateTime> {

	// private static final String FORMAT = DateFormat.FULL;
	// private SimpleDateFormat format = new SimpleDateFormat();
	private static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

	@Override
	public DateTime read(JsonReader jsonReader) throws IOException {
		// format.applyPattern(FORMAT);
		String value = jsonReader.nextString();
		return DateTime.parse(value, dtf);
	}

	@Override
	public void write(JsonWriter jsonWriter, DateTime date) throws IOException {
		if (date == null) {
			jsonWriter.nullValue();
			return;
			//date=new DateTime();
		}
		StringBuffer sb = new StringBuffer();
		dtf.printTo(sb, date.toInstant());
		jsonWriter.value(sb.toString());
	}

}
