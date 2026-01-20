package com.letswork.crm.service;

import java.util.List;
import java.util.Map;

import com.letswork.crm.dtos.CategoryWithSubCategoriesDto;

public interface CategoryService {
	
	String saveOrUpdateCategory(
            String companyId,
            String categoryName
    );

	String saveOrUpdateSubCategories(
	        String companyId,
	        String parentCategory,
	        String subCategoryNames
	);

    List<CategoryWithSubCategoriesDto> getCategoriesWithSubCategories(
            String companyId,
            String category
    );

}
