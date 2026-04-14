package com.letswork.crm.enums;

public enum SortingOrder {

	ASC("Asc"), DESC("Desc");

	private String sortingName;

	SortingOrder(String sortingName) {
		this.sortingName = sortingName;
	}

	public String getSortingName() {
		return sortingName;
	}
}
