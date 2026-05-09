package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.entities.SolutionType;

public interface SolutionTypeService {

    SolutionType saveOrUpdate(SolutionType solutionType);

    public List<SolutionType> getSolutionTypes(
            String companyId,
            String search
    );
}
