package mali.service;

import java.util.Date;

import javax.annotation.Resource;

import mali.core.feature.test.TestSupport;
import mali.core.util.ApplicationUtils;
import mali.web.model.User;
import mali.web.service.UserService;

import org.junit.Test;

public class UserServiceTest extends TestSupport {

    @Resource
    private UserService userService;

//    @Test
    public void test_insert() {
        User model = new User();
        model.setUsername("Vincent");
        model.setPassword(ApplicationUtils.sha256Hex("123456"));
        model.setCreateTime(new Date());
        userService.insert(model);
    }

//    @Test
    public void test_10insert() {
        for (int i = 0; i < 10; i++) {
            User model = new User();
            model.setUsername("Vincent" + i);
            model.setPassword(ApplicationUtils.sha256Hex("123456"));
            model.setCreateTime(new Date());
            userService.insert(model);
        }
    }

}
