package com.sunzy.reggie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Set;

@SpringBootTest
class ReggieApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {

        String city11 = stringRedisTemplate.opsForValue().get("city11");

    }


    @Test
    void testHash(){
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();


        opsForHash.put("002","name","sunny");
        opsForHash.put("002","age","20");
        opsForHash.put("002","add","beijing");
        String name = (String) opsForHash.get("002", "name");
        System.out.println(name);

        List<Object> values = opsForHash.values("002");
        for (Object value : values) {
            System.out.println(value);
        }
    }


    @Test
    void testSet(){
        SetOperations<String, String> opsForSet = stringRedisTemplate.opsForSet();

        opsForSet.add("myset1","a","b","c","a");

        Set<String> myset1 = opsForSet.members("myset1");

        for (String s : myset1) {
            System.out.println(s);
        }

        opsForSet.remove("myset1","a","b");

        myset1 = opsForSet.members("myset1");

        for (String s : myset1) {
            System.out.println(s);
        }
    }

    @Test
    void testCommon(){
        // 获取所有key
        Set<String> keys = stringRedisTemplate.keys("*");
        for (String key : keys) {
            System.out.println(key);
        }
        // 判断指定key是否存在

        Boolean aBoolean = stringRedisTemplate.hasKey("002");
        System.out.println(aBoolean);
        // 删除指定key
        stringRedisTemplate.delete("002");
        aBoolean = stringRedisTemplate.hasKey("002");
        System.out.println(aBoolean);
        // 获取指定key的数据类型
        DataType type = stringRedisTemplate.type("001");
        System.out.println(type);

    }


    @Test
    void testKeys(){
        Set<String> keys = stringRedisTemplate.keys("*dish_*");
        stringRedisTemplate.delete(keys);
        redisTemplate.delete(keys);
        keys = stringRedisTemplate.keys("*dish_*");
        for (String key : keys) {
            System.out.println(key);
            Object o = redisTemplate.opsForValue().get(key);
            System.out.println(o);
        }
    }
}
