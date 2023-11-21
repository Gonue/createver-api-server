package com.template.server.domain.image.repository.view;

import com.template.server.domain.image.entity.ImageView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImageViewRepository extends JpaRepository<ImageView, Long>, CustomViewRepository {
}
