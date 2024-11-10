package org.zir.dragonieze.dragon.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zir.dragonieze.dragon.DragonHead;

import java.util.List;

public interface DragonHeadRepository extends JpaRepository<DragonHead, Long> {
    List<DragonHead> findByDragonId(Long dragonId);

    List<DragonHead> findByUserId(Long userId);


}