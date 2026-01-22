package com.letswork.crm.dtos;

import java.util.List;

import com.letswork.crm.enums.CategoryType;

public class CategoryWithSubCategoriesDto {
	
	private String category;
	
    private List<String> subCategories;
    
    private CategoryType categoryType;

    public CategoryWithSubCategoriesDto(String category, CategoryType categoryType, List<String> subCategories) {
        this.category = category;
        this.categoryType = categoryType;
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

	public CategoryType getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(CategoryType categoryType) {
		this.categoryType = categoryType;
	}
    
    

}
