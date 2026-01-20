package com.letswork.crm.dtos;

import java.util.List;

public class CategoryWithSubCategoriesDto {
	
	private String category;
    private List<String> subCategories;

    public CategoryWithSubCategoriesDto(String category, List<String> subCategories) {
        this.category = category;
        this.subCategories = subCategories;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<String> subCategories) {
        this.subCategories = subCategories;
    }

}
