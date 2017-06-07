package mali.web.service.impl;

import java.util.List;

import javax.annotation.Resource;

import mali.core.generic.GenericDao;
import mali.core.generic.GenericServiceImpl;
import mali.web.dao.PermissionMapper;
import mali.web.model.Permission;
import mali.web.service.PermissionService;

import org.springframework.stereotype.Service;

/**
 * 权限Service实现类
 *
 * @author Vincent
 * @since 2017年6月10日 下午12:05:03
 */
@Service
public class PermissionServiceImpl extends GenericServiceImpl<Permission, Long> implements PermissionService {

    @Resource
    private PermissionMapper permissionMapper;


    @Override
    public GenericDao<Permission, Long> getDao() {
        return permissionMapper;
    }

    @Override
    public List<Permission> selectPermissionsByRoleId(Long roleId) {
        return permissionMapper.selectPermissionsByRoleId(roleId);
    }
}
