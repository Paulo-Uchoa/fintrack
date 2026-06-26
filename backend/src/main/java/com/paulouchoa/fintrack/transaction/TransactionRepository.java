package com.paulouchoa.fintrack.transaction;

import com.paulouchoa.fintrack.common.TransactionType;
import com.paulouchoa.fintrack.report.CategorySummaryProjection;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    @Override
    @EntityGraph(attributePaths = {"account", "category"})
    Page<Transaction> findAll(Specification<Transaction> spec, Pageable pageable);

    boolean existsByAccountId(Long accountId);

    boolean existsByCategoryId(Long categoryId);

    @Query("""
            select coalesce(sum(t.amount), 0)
            from Transaction t
            where t.user.id = :userId
              and t.type = :type
              and t.date between :start and :end
            """)
    BigDecimal sumByType(@Param("userId") Long userId,
                         @Param("type") TransactionType type,
                         @Param("start") LocalDate start,
                         @Param("end") LocalDate end);

    @Query("""
            select coalesce(sum(t.amount), 0)
            from Transaction t
            where t.user.id = :userId
              and t.category.id = :categoryId
              and t.type = :type
              and t.date between :start and :end
            """)
    BigDecimal sumByCategoryAndType(@Param("userId") Long userId,
                                    @Param("categoryId") Long categoryId,
                                    @Param("type") TransactionType type,
                                    @Param("start") LocalDate start,
                                    @Param("end") LocalDate end);

    @Query("""
            select t.category.id as categoryId,
                   t.category.name as categoryName,
                   t.category.color as color,
                   sum(t.amount) as total
            from Transaction t
            where t.user.id = :userId
              and t.type = :type
              and t.date between :start and :end
            group by t.category.id, t.category.name, t.category.color
            order by sum(t.amount) desc
            """)
    List<CategorySummaryProjection> sumByCategory(@Param("userId") Long userId,
                                                  @Param("type") TransactionType type,
                                                  @Param("start") LocalDate start,
                                                  @Param("end") LocalDate end);
}
