package org.coody.framework.jdbc.entity;

import java.util.List;

import org.coody.framework.core.model.BaseModel;

@SuppressWarnings("serial")
public class Pager extends BaseModel {

	private Integer count;
	private Integer size = 10;
	private Integer current;
	private Integer total=1;
	private List<?> data;

	
	

	public Pager(Integer size) {
		super();
		this.current = 1;
		this.size = size;
	}


	public Pager(Integer size, Integer current) {
		super();
		if(current==null||current<1){
			current=1;
		}
		if(size==null||size>100){
			size=20;
		}
		this.size = size;
		this.current = current;
	}


	public Pager() {
		this.current = 1;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getData() {
		return (List<T>) data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	public Integer getCurrent() {
		return current;
	}

	public void setCurrent(Integer currPage) {
		if(currPage==null||currPage<1){
			currPage=1;
		}
		this.current = currPage;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		if(size==null){
			size=20;
		}
		if(size>100){
			size=100;
		}
		this.size = size;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
		try {
			this.total = count / size;
			Integer mod = count % size;
			if (mod > 0) {
				this.total++;
			}
			if (this.total == 0) {
				this.total = 1;
			}
			if (this.current > total) {
				this.current = total;
			}
			if (this.current == 0 || this.current < 0) {
				current = 1;
			}
		} catch (Exception e) {
		}
	}

}
