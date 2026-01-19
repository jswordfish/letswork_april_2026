package com.letswork.crm.service;

import java.util.List;
import java.util.Map;

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

    Map<String, List<String>> getCategoriesWithSubCategories(
            String companyId,
            String category
    );

}
