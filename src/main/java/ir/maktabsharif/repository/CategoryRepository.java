package ir.maktabsharif.repository;


import ir.maktabsharif.model.Category;
import ir.maktabsharif.model.TradesMan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(Long categoryId);

    boolean existsById(Long categoryId);

    @Query("select count(t)>0 from TradesMan t where t.id=:tId")
    boolean tradesmanExistsById(@Param("tId") Long tradesmanId);
    @Query("select t from TradesMan t where t.id=:tId")
    Optional<TradesMan> findTradesManByTradesManId(@Param("tId") Long tradesmanId);

    boolean existsByCategoryName(String categoryName);

    Category findByCategoryName(String categoryName);
    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);
    @Query("select count(c)>0 from Category c where c.id=:id and c.parentCategory is null")
    boolean isParent(@Param("id") Long categoryId);

    @Query("select c from Category c where c.parentCategory.id=:parentCategoryId")
    List<Category> findSubCategories(@Param("parentCategoryId") Long categoryId);

    @Query("select c from Category c where c.parentCategory is null ")
    List<Category> findAllParentCategories();

    @Query("select c from Category c join c.tradesMen t where t.id=:tId")
///important!
    List<Category> findByTradesManId(@Param("tId") Long tradesManId);

    @Modifying
    void deleteById(Long categoryId);
}
