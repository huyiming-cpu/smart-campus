package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.SysConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SystemRepository extends JpaRepository<SysConfig, Long> {
    Optional<SysConfig> findByConfigKey(String configKey);
    List<SysConfig> findByConfigType(String configType);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE SysConfig c SET c.configValue=:value WHERE c.configKey=:key")
    int updateValueByKey(@org.springframework.data.repository.query.Param("key") String key, @org.springframework.data.repository.query.Param("value") String value);
}
