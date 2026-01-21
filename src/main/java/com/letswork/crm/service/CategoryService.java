package com.letswork.crm.service;

import java.util.List;
import java.util.Map;

import com.letswork.crm.dtos.CategoryWithSubCategoriesDto;
import com.letswork.crm.entities.Category;
import com.letswork.crm.entities.SubCategory;
import com.letswork.crm.enums.CategoryType;

public interface CategoryService {
	
	public String saveOrUpdateCategory(
            Category category
    );

	public String saveOrUpdateSubCategories(
            SubCategory subCategory
    );

    List<CategoryWithSubCategoriesDto> getCategoriesWithSubCategories(
            String companyId,
            String category,
            CategoryType categoryType
    );

}
