package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.IdentityRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface IdentityRegistryRepository extends JpaRepository<IdentityRegistry, Long> {

    Optional<IdentityRegistry> findByIdentityNumberAndNameAndIdCardLast6(
            String identityNumber, String name, String idCardLast6);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT i.className FROM IdentityRegistry i WHERE i.className IS NOT NULL")
    List<String> findDistinctClassNames();
}
