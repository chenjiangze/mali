package mali.web.service.impl;

import java.util.List;

import javax.annotation.Resource;

import mali.core.generic.GenericDao;
import mali.core.generic.GenericServiceImpl;
import mali.web.dao.RoleMapper;
import mali.web.model.Role;
import mali.web.service.RoleService;

import org.springframework.stereotype.Service;

/**
 * 角色Service实现类
 *
 * @author StarZou
 * @since 2014年6月10日 下午4:16:33
 */
@Service
public class RoleServiceImpl extends GenericServiceImpl<Role, Long> implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Override
    public GenericDao<Role, Long> getDao() {
        return roleMapper;
    }

    @Override
    public List<Role> selectRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

}
