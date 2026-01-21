package com.letswork.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.CategoryWithSubCategoriesDto;
import com.letswork.crm.entities.Category;
import com.letswork.crm.entities.SubCategory;
import com.letswork.crm.enums.CategoryType;
import com.letswork.crm.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<String> createCategory(
            @RequestBody Category category,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                categoryService.saveOrUpdateCategory(
                        category
                )
        );
    }

    @PostMapping("/sub")
    public ResponseEntity<String> createSubCategories(
            @RequestBody SubCategory subCategory,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                categoryService.saveOrUpdateSubCategories(
                        subCategory
                )
        );
    }

    @GetMapping("/get-parent-and-sub")
    public ResponseEntity<List<CategoryWithSubCategoriesDto>> getCategories(
            @RequestParam String companyId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) CategoryType categoryType,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                categoryService.getCategoriesWithSubCategories(
                        companyId, category, categoryType
                )
        );
    }
}
