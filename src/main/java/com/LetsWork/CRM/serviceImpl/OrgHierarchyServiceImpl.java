package com.LetsWork.CRM.serviceImpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.LetsWork.CRM.entities.OrgHierarchy;
import com.LetsWork.CRM.repo.OrgHierarchyRepo;
import com.LetsWork.CRM.service.OrgHierarchyService;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;




@Service
@Transactional
public class OrgHierarchyServiceImpl implements OrgHierarchyService{
	
	@Autowired
	OrgHierarchyRepo repo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public List<OrgHierarchy> findByLevel(String level, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByLevelAndCompanyId(level, companyId);
	}

	@Override
	public OrgHierarchy findByRoleOrDesig(String roleOrDesig, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByRoleOrDesig(roleOrDesig, companyId);
	}

	@Override
	public OrgHierarchy findByParentRoleOrDesig(String roleOrDesig, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByParentRoleOrDesig(roleOrDesig, companyId);
	}
	
	private void validate(OrgHierarchy orgHierarchy) {
		if(orgHierarchy.getLevel() == null) {
			throw new RuntimeException("Level can not be null");
		}
		String parentRoleOrDesig = orgHierarchy.getParentRoleOrDesig();
		if(parentRoleOrDesig != null) {
			OrgHierarchy parent = findByRoleOrDesig(parentRoleOrDesig, orgHierarchy.getCompanyId());
			if(parent == null) {
				throw new RuntimeException("Parent does not exist");
			}
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public synchronized OrgHierarchy saveOrUpdate(OrgHierarchy orgHierarchy) {
		// TODO Auto-generated method stub
		validate(orgHierarchy);
		OrgHierarchy orgHierarchy2 = findByRoleOrDesig(orgHierarchy.getRoleOrDesig(), orgHierarchy.getCompanyId());
			if(orgHierarchy2 == null) {
				orgHierarchy.setCreateDate(new Date());
				return repo.save(orgHierarchy);
			}
			else {
				orgHierarchy.setCreateDate(orgHierarchy2.getCreateDate());
				orgHierarchy.setUpdateDate(new Date());
				orgHierarchy.setId(orgHierarchy2.getId());
				mapper.map(orgHierarchy, orgHierarchy2);
				return repo.save(orgHierarchy2);
			}
	}

	@Override
	public List<OrgHierarchy> getAllHierarchyLevels(String companyId) {
		// TODO Auto-generated method stub
		//Iterable<OrgHierarchy> itr =  repo.findByCompanyId(companyId);
		List<OrgHierarchy> ret = repo.findByCompanyId(companyId);
		//itr.forEach(o -> ret.add(o));
		return ret;
	}

}
