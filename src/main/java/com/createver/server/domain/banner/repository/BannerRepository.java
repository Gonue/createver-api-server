package com.createver.server.domain.banner.repository;

import com.createver.server.domain.banner.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    Page<Banner> findByActiveTrue(Pageable pageable);
}
