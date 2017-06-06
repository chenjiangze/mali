package mali.dao;

import java.util.List;

import javax.annotation.Resource;

import mali.core.feature.orm.mybatis.Page;
import mali.core.feature.test.TestSupport;
import mali.web.dao.UserMapper;
import mali.web.model.User;
import mali.web.model.UserExample;

import org.junit.Test;

public class UserMapperTest extends TestSupport {
    @Resource
    private UserMapper userMapper;

    @Test
    public void test_selectByExampleAndPage() {
        start();
        Page<User> page = new Page<>(1, 3);
        UserExample example = new UserExample();
        example.createCriteria().andIdGreaterThan(0L);
        final List<User> users = userMapper.selectByExampleAndPage(page, example);
        for (User user : users) {
            System.err.println(user);
        }
        end();
    }
}
