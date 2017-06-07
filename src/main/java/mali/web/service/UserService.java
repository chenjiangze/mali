package mali.web.service;

import mali.core.generic.GenericService;
import mali.web.model.User;

/**
 * 用户 业务 接口
 * 
 * @author Vincent
 * @since 2017年7月5日 上午11:53:33
 **/
public interface UserService extends GenericService<User, Long> {

    /**
     * 用户验证
     * 
     * @param user
     * @return
     */
    User authentication(User user);

    /**
     * 根据用户名查询用户
     * 
     * @param username
     * @return
     */
    User selectByUsername(String username);
}
