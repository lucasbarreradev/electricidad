package com.sistema.repository;

import com.sistema.model.RemitoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemitoItemRepository extends JpaRepository<RemitoItem, Long> {
}
