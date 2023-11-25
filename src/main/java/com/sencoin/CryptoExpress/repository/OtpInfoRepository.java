package com.sencoin.CryptoExpress.repository;

import com.sencoin.CryptoExpress.Entities.OtpInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface OtpInfoRepository extends JpaRepository<OtpInfo, Long> {
    Optional<OtpInfo> findByEmail(String email);
}
