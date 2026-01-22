package com.letswork.crm.serviceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.CategoryWithSubCategoriesDto;
import com.letswork.crm.entities.Category;
import com.letswork.crm.entities.SubCategory;
import com.letswork.crm.enums.CategoryType;
import com.letswork.crm.repo.CategoryRepository;
import com.letswork.crm.repo.SubCategoryRepository;
import com.letswork.crm.service.CategoryService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
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

    @Override
    public String saveOrUpdateCategory(
            Category category
    ) {
        validateCompany(category.getCompanyId());

        Category existing =
                categoryRepo.findByNameAndCompanyIdAndCategoryType(
                        category.getName(), category.getCompanyId(), category.getCategoryType()
                );

        if (existing != null) {
            existing.setUpdateDate(new Date());
            categoryRepo.save(existing);
            return "Category updated";
        }

        Category category1 = new Category();
        category1.setName(category.getName());
        category1.setCompanyId(category.getCompanyId());
        category1.setCategoryType(category.getCategoryType());
        category1.setCreateDate(new Date());
        category1.setUpdateDate(new Date());

        categoryRepo.save(category1);
        return "Category created";
    }

    @Override
    public String saveOrUpdateSubCategories(
            SubCategory subCategory
    ) {
        validateCompany(subCategory.getCompanyId());

        Category parent =
                categoryRepo.findByNameAndCompanyIdAndCategoryType(
                        subCategory.getParentCategory(), subCategory.getCompanyId(), subCategory.getCategoryType()
                );

        if (parent == null) {
            throw new RuntimeException("Parent category does not exist");
        }

        // 1️⃣ Parse incoming names
        Set<String> incomingNames = Arrays.stream(subCategory.getName().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        if (incomingNames.isEmpty()) {
            throw new RuntimeException("No valid sub-categories provided");
        }

        // 2️⃣ Fetch existing sub-categories for this parent
        List<SubCategory> existingSubs =
                subCategoryRepo.findByCompanyIdAndParentCategoryAndCategoryType(
                		subCategory.getCompanyId(), subCategory.getParentCategory(), subCategory.getCategoryType()
                );

        Map<String, SubCategory> existingMap =
                existingSubs.stream()
                        .collect(Collectors.toMap(
                                SubCategory::getName,
                                s -> s
                        ));

        int created = 0;
        int updated = 0;
        int deleted = 0;

        // 3️⃣ Create or update incoming ones
        for (String name : incomingNames) {
            SubCategory existing = existingMap.get(name);

            if (existing != null) {
                existing.setUpdateDate(new Date());
                subCategoryRepo.save(existing);
                updated++;
            } else {
            	SubCategory sub = new SubCategory();
            	sub.setCompanyId(subCategory.getCompanyId());
            	sub.setParentCategory(subCategory.getParentCategory());
            	sub.setName(name);
            	sub.setCategoryType(subCategory.getCategoryType()); 
            	sub.setCreateDate(new Date());
            	sub.setUpdateDate(new Date());

            	subCategoryRepo.save(sub);
                created++;
            }
        }

        // 4️⃣ Delete removed sub-categories
        List<Long> toDeleteIds = existingSubs.stream()
                .filter(sc -> !incomingNames.contains(sc.getName()))
                .map(SubCategory::getId)
                .collect(Collectors.toList());

        if (!toDeleteIds.isEmpty()) {
            subCategoryRepo.deleteAllByIdIn(toDeleteIds);
            deleted = toDeleteIds.size();
        }

        return String.format(
                "Sub-categories synced successfully. Created: %d, Updated: %d, Deleted: %d",
                created, updated, deleted
        );
    }

    @Override
    public List<CategoryWithSubCategoriesDto> getCategoriesWithSubCategories(
            String companyId,
            String category,
            CategoryType categoryType
    ) {
        validateCompany(companyId);

        List<CategoryWithSubCategoriesDto> response = new ArrayList<>();

        List<Category> categories =
                (category != null)
                        ? List.of(
                            categoryRepo.findByNameAndCompanyIdAndCategoryType(
                                    category, companyId, categoryType
                            )
                          )
                        : categoryRepo.findByCompanyIdAndCategoryType(
                                companyId, categoryType
                        );

        for (Category cat : categories) {
            if (cat == null) continue;

            List<SubCategory> subs =
                    subCategoryRepo.findByCompanyIdAndParentCategoryAndCategoryType(
                            companyId,
                            cat.getName(),
                            cat.getCategoryType()
                    );

            List<String> subCategoryNames = subs.stream()
                    .map(SubCategory::getName)
                    .collect(Collectors.toList());

            response.add(
                    new CategoryWithSubCategoriesDto(
                            cat.getName(),
                            cat.getCategoryType(),
                            subCategoryNames
                    )
            );
        }

        return response;
    }
}
