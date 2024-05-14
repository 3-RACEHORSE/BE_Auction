package com.skyhorsemanpower.auction.repository;

import com.skyhorsemanpower.auction.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
