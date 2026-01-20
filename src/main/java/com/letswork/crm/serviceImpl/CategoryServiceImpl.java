package com.letswork.crm.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.CategoryWithSubCategoriesDto;
import com.letswork.crm.entities.Category;
import com.letswork.crm.entities.SubCategory;
import com.letswork.crm.repo.CategoryRepository;
import com.letswork.crm.repo.SubCategoryRepository;
import com.letswork.crm.service.CategoryService;
import com.letswork.crm.service.TenantService;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private SubCategoryRepository subCategoryRepo;

    @Autowired
    private TenantService tenantService;

    private void validateCompany(String companyId) {
        if (tenantService.findTenantByCompanyId(companyId) == null) {
            throw new RuntimeException("Invalid companyId - " + companyId);
        }
    }

    // 🔹 Create / Update Category
    @Override
    public String saveOrUpdateCategory(
            String companyId,
            String categoryName
    ) {
        validateCompany(companyId);

        Category existing =
                categoryRepo.findByNameAndCompanyId(
                        categoryName, companyId
                );

        if (existing != null) {
            existing.setUpdateDate(new Date());
            categoryRepo.save(existing);
            return "Category updated";
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setCompanyId(companyId);
        category.setCreateDate(new Date());
        category.setUpdateDate(new Date());

        categoryRepo.save(category);
        return "Category created";
    }

    // 🔹 Create / Update SubCategory
    @Override
    public String saveOrUpdateSubCategory(
            String companyId,
            String parentCategory,
            String subCategoryName
    ) {
        validateCompany(companyId);

        Category parent =
                categoryRepo.findByNameAndCompanyId(
                        parentCategory, companyId
                );

        if (parent == null) {
            throw new RuntimeException(
                    "Parent category does not exist"
            );
        }

        SubCategory existing =
                subCategoryRepo.findByNameAndCompanyId(
                        subCategoryName, companyId
                );

        if (existing != null) {
            existing.setParentCategory(parentCategory);
            existing.setUpdateDate(new Date());
            subCategoryRepo.save(existing);
            return "Sub-category updated";
        }

        SubCategory sub = new SubCategory();
        sub.setCompanyId(companyId);
        sub.setParentCategory(parentCategory);
        sub.setName(subCategoryName);
        sub.setCreateDate(new Date());
        sub.setUpdateDate(new Date());

        subCategoryRepo.save(sub);
        return "Sub-category created";
    }

    @Override
    public List<CategoryWithSubCategoriesDto> getCategoriesWithSubCategories(
            String companyId,
            String category
    ) {
        validateCompany(companyId);

        List<CategoryWithSubCategoriesDto> response = new ArrayList<>();

        List<Category> categories =
                (category != null)
                        ? List.of(
                            categoryRepo.findByNameAndCompanyId(
                                    category, companyId
                            )
                          )
                        : categoryRepo.findByCompanyId(companyId);

        for (Category cat : categories) {
            if (cat == null) continue;

            List<SubCategory> subs =
                    subCategoryRepo.findByCompanyIdAndParentCategory(
                            companyId, cat.getName()
                    );

            List<String> subCategoryNames = subs.stream()
                    .map(SubCategory::getName)
                    .collect(Collectors.toList());

            response.add(
                    new CategoryWithSubCategoriesDto(
                            cat.getName(),
                            subCategoryNames
                    )
            );
        }

        return response;
    }
}
