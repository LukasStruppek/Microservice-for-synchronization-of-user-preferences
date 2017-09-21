package de.privacy_avare.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;


@Document
public class AllProfiles {
	@Field
	private List<Row> rows;
	@Field
	private int total_rows;

	public AllProfiles() {
		
	}

	public AllProfiles(List<Row> rows, int total_rows) {
		super();
		this.rows = rows;
		this.total_rows = total_rows;
	}

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public int getTotal_rows() {
		return total_rows;
	}

	public void setTotal_rows(int total_rows) {
		this.total_rows = total_rows;
	}
	
	public List<String> getAllIds(){
		List<String> list = new ArrayList<String>();
		for(Row row : this.rows) {
			list.add(row.getId());
		}
		return list;
	}

	
}
