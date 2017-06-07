package mali.web.dao;

import java.util.List;

import mali.core.feature.orm.mybatis.Page;
import mali.core.generic.GenericDao;
import mali.web.model.User;
import mali.web.model.UserExample;

import org.apache.ibatis.annotations.Param;

/**
 * 用户Dao接口
 * 
 * @author Vincent
 * @since 2017年7月5日 上午11:49:57
 **/
public interface UserMapper extends GenericDao<User, Long> {
    int countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 用户登录验证查询
     * 
     * @param record
     * @return
     */
    User authentication(@Param("record") User record);

    /**
     * 分页条件查询
     * 
     * @param page
     * @param example
     * @return
     */
    List<User> selectByExampleAndPage(Page<User> page, UserExample example);
}