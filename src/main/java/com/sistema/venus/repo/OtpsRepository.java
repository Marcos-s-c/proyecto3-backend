package com.sistema.venus.repo;

import com.sistema.venus.domain.Otps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpsRepository extends JpaRepository<Otps, Long> {
    Otps getOtpsByCodigo(String userCode);
}
