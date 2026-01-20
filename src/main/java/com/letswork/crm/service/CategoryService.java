package com.letswork.crm.service;

import java.util.List;
import java.util.Map;

import com.letswork.crm.dtos.CategoryWithSubCategoriesDto;

public interface CategoryService {
	
	String saveOrUpdateCategory(
            String companyId,
            String categoryName
    );

    String saveOrUpdateSubCategory(
            String companyId,
            String parentCategory,
            String subCategoryName
    );

    List<CategoryWithSubCategoriesDto> getCategoriesWithSubCategories(
            String companyId,
            String category
    );

}
