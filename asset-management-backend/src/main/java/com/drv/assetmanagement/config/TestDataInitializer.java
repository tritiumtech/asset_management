package com.drv.assetmanagement.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 测试数据初始化配置
 * 仅在 dev 和 h2 环境下加载
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile({"dev", "h2"})
public class TestDataInitializer {

    private final DataSource dataSource;

    @Bean
    public CommandLineRunner initTestData() {
        return args -> {
            log.info("开始加载测试数据...");
            try (Connection connection = dataSource.getConnection()) {
                // 检查是否已有足够测试数据（>30条视为已初始化）
                boolean hasData = false;
                int assetCount = 0;
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM asset")) {
                    if (rs.next()) {
                        assetCount = rs.getInt(1);
                        if (assetCount > 30) {
                            hasData = true;
                            log.info("数据库已有 {} 条资产数据，跳过初始化", assetCount);
                        }
                    }
                } catch (Exception e) {
                    // 表可能不存在，继续执行
                    log.info("检查数据失败，可能是表不存在: {}", e.getMessage());
                }
                
                if (!hasData) {
                    log.info("当前资产数据 {} 条，需要初始化测试数据...", assetCount);
                    // 只加载测试数据（schema已由JPA创建）
                    ScriptUtils.executeSqlScript(connection, 
                        new ClassPathResource("db/test-data.sql"));
                    log.info("测试数据加载完成");
                }
            } catch (Exception e) {
                log.error("加载测试数据失败: {}", e.getMessage(), e);
            }
        };
    }
}