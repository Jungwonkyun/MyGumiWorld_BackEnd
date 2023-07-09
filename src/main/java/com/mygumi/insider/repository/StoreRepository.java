package com.mygumi.insider.repository;

import com.mygumi.insider.domain.Store;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface StoreRepository extends CrudRepository<Store, Long> {

    @Query("select distinct s from Store s join fetch s.reviews where s.id = :storeId")
    Store getStoreWithReviews(@Param("storeId") Long storeId);

    @Query(value = "SELECT avg(star) FROM review r where r.store_id = :storeId", nativeQuery = true)
    Integer getAverageStar(@Param("storeId") Long storeId);

}
