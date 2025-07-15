package pw.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import cn.xuyanwu.spring.file.storage.EnableFileStorage;

/**
 * Spring Boot 启动类
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication
@MapperScan("pw.pj.mapper")
@EnableCaching
@EnableScheduling
@EnableFileStorage
@EnableWebMvc
@EnableWebSocket
public class PersonJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonJavaApplication.class, args);
    }

}
