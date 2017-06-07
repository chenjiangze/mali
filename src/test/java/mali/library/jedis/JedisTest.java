package mali.library.jedis;

import mali.core.feature.cache.redis.RedisCache;
import mali.core.feature.test.TestSupport;

import org.junit.Test;

import javax.annotation.Resource;

/**
 * JedisTest : 测试 jedis 功能
 *
 * @author Vincent
 * @since 2015-03-20 10:32
 */
public class JedisTest extends TestSupport {


    @Resource
    private RedisCache redisCache;


    @Test
    public void testSet() {
        redisCache.cache("anchor", "Vincent", 1 * 60 * 24);
    }

    @Test
    public void testGet() {
        System.out.printf(redisCache.get("anchor"));
    }
}
