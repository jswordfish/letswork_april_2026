package com.letswork.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.CategoryWithSubCategoriesDto;
import com.letswork.crm.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<String> createCategory(
            @RequestParam String companyId,
            @RequestParam String name,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                categoryService.saveOrUpdateCategory(
                        companyId, name
                )
        );
    }

    @PostMapping("/sub")
    public ResponseEntity<String> createSubCategory(
            @RequestParam String companyId,
            @RequestParam String parentCategory,
            @RequestParam String name,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                categoryService.saveOrUpdateSubCategory(
                        companyId, parentCategory, name
                )
        );
    }

    @GetMapping("/get-parent-and-sub")
    public ResponseEntity<List<CategoryWithSubCategoriesDto>> getCategories(
            @RequestParam String companyId,
            @RequestParam(required = false) String category,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                categoryService.getCategoriesWithSubCategories(
                        companyId, category
                )
        );
    }
}
