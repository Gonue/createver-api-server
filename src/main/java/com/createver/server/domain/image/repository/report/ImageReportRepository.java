package com.createver.server.domain.image.repository.report;

import com.createver.server.domain.image.entity.ImageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageReportRepository extends JpaRepository<ImageReport, Long> , CustomReportRepository{
}
