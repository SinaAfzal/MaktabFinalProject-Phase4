package ir.maktabsharif.repository;

import ir.maktabsharif.model.Category;
import ir.maktabsharif.model.Task;
import ir.maktabsharif.model.enumeration.TaskStatus;
import ir.maktabsharif.service.dto.request.AdvancedTaskSearchDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AdvancedTaskSearchDAO {

    private final EntityManager entityManager;


    public AdvancedTaskSearchDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Task> findTasks(AdvancedTaskSearchDTO advancedTaskSearchDTO) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = criteriaBuilder.createQuery(Task.class);
        Root<Task> root = query.from(Task.class);
        query.select(root);

        List<Predicate> predicates = new ArrayList<>();

        //add predicates
        addParentCategoryIdPredicate(predicates, root, criteriaBuilder, query, advancedTaskSearchDTO.getParentCategoryId(), advancedTaskSearchDTO.getSubCategoryId());
        addSubCategoryIdPredicate(predicates, root, criteriaBuilder, advancedTaskSearchDTO.getSubCategoryId());
        addCustomerIdPredicate(predicates, root, criteriaBuilder, advancedTaskSearchDTO.getCustomerId());
        addTradesmanIdPredicate(predicates, root, criteriaBuilder, advancedTaskSearchDTO.getTradesManId());
        addRequestDateFromPredicate(predicates, root, criteriaBuilder, advancedTaskSearchDTO.getRequestDateFrom());
        addRequestDateToPredicate(predicates, root, criteriaBuilder, advancedTaskSearchDTO.getRequestDateTo());
        addTaskStartDateFromPredicate(predicates, root, criteriaBuilder, advancedTaskSearchDTO.getTaskStartDateFrom());
        addTaskStartDateToPredicate(predicates, root, criteriaBuilder, advancedTaskSearchDTO.getTaskStartDateTo());
        addTaskStatusPredicate(predicates, root, criteriaBuilder, advancedTaskSearchDTO.getStatus());
        addWinnerPriceMinPredicate(predicates,root,criteriaBuilder,advancedTaskSearchDTO.getWinnerPriceMin());
        addWinnerPriceMaxPredicate(predicates,root,criteriaBuilder,advancedTaskSearchDTO.getWinnerPriceMax());

        if (predicates.size() > 0) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        TypedQuery<Task> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    /**
     * SELECT *
     * FROM tasks t
     * WHERE t.subCategoryId IN(
     * SELECT c.id
     * FROM category c
     * JOIN category p ON c.parent_category_id=p.id
     * WHERE p.id=?)
     */
    private void addParentCategoryIdPredicate(List<Predicate> predicates, Root<Task> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<Task> query, Long parentCatId, Long subCatId) {
        if (parentCatId != null && subCatId == null) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Category> subqueryRoot = subquery.from(Category.class);
            Join<Category, Category> parentCategoryJoin = subqueryRoot.join("parentCategory", JoinType.INNER);
            subquery.select(subqueryRoot.get("id")).where(criteriaBuilder.equal(parentCategoryJoin.get("id"), parentCatId));
            Predicate parentCategoryIdPredicate = criteriaBuilder.in(root.<Long>get("subCategoryId")).value(subquery);
            predicates.add(parentCategoryIdPredicate);
        }
    }

    private void addSubCategoryIdPredicate(List<Predicate> predicates, Root<Task> root, CriteriaBuilder criteriaBuilder, Long subCatId) {
        if (subCatId != null) {
            Predicate subCategoryIdPredicate = criteriaBuilder.equal(
                    root.get("subCategoryId"), subCatId
            );
            predicates.add(subCategoryIdPredicate);
        }
    }

    private void addCustomerIdPredicate(List<Predicate> predicates, Root<Task> root, CriteriaBuilder criteriaBuilder, Long customerId) {
        if (customerId != null) {
            Predicate customerIdPredicate = criteriaBuilder.equal(
                    root.get("customer").get("id"), customerId
            );
            predicates.add(customerIdPredicate);
        }
    }

    private void addTradesmanIdPredicate(List<Predicate> predicates, Root<Task> root, CriteriaBuilder criteriaBuilder, Long tradesmanId) {
        if (tradesmanId != null) {
            Predicate tradesmanIdPredicate = criteriaBuilder.equal(
                    root.get("tradesManWhoGotTheJob").get("id"), tradesmanId
            );
            predicates.add(tradesmanIdPredicate);
        }
    }

    private void addRequestDateFromPredicate(List<Predicate> predicates, Root<Task> root, CriteriaBuilder criteriaBuilder, LocalDate requestDateFrom) {
        if (requestDateFrom != null) {
            Predicate requestDateFromPredicate = criteriaBuilder.greaterThanOrEqualTo(
                    root.get("requestDateTime"), requestDateFrom
            );
            predicates.add(requestDateFromPredicate);
        }
    }

    private void addRequestDateToPredicate(List<Predicate> predicates, Root<Task> root, CriteriaBuilder criteriaBuilder, LocalDate requestDateTo) {
        if (requestDateTo != null) {
            Predicate requestDateToPredicate = criteriaBuilder.lessThanOrEqualTo(
                    root.get("requestDateTime"), requestDateTo
            );
            predicates.add(requestDateToPredicate);
        }
    }

    private void addTaskStartDateFromPredicate(List<Predicate> predicates, Root<Task> root, CriteriaBuilder criteriaBuilder, LocalDate taskStartDateFrom) {
        if (taskStartDateFrom != null) {
            Predicate taskStartDateFromPredicate = criteriaBuilder.greaterThanOrEqualTo(
                    root.get("taskDateTimeByCustomer"), taskStartDateFrom
            );
            predicates.add(taskStartDateFromPredicate);
        }
    }

    private void addTaskStartDateToPredicate(List<Predicate> predicates, Root<Task> root, CriteriaBuilder criteriaBuilder, LocalDate taskStartDateTo) {
        if (taskStartDateTo != null) {
            Predicate taskStartDateToPredicate = criteriaBuilder.lessThanOrEqualTo(
                    root.get("taskDateTimeByCustomer"), taskStartDateTo
            );
            predicates.add(taskStartDateToPredicate);
        }
    }

    private void addTaskStatusPredicate(List<Predicate> predicates, Root<Task> root, CriteriaBuilder criteriaBuilder, TaskStatus status) {
        if (status != null) {
            Predicate taskStatusPredicate = criteriaBuilder.equal(
                    root.get("status"), status
            );
            predicates.add(taskStatusPredicate);
        }
    }

    private void addWinnerPriceMinPredicate(List<Predicate> predicates, Root<Task> root, CriteriaBuilder criteriaBuilder, Double winnerPriceMin) {
        if (winnerPriceMin != null) {
            Predicate priceMinPredicate = criteriaBuilder.greaterThanOrEqualTo(
                    root.get("selectedProposal").get("proposedPrice"), winnerPriceMin
            );
            predicates.add(priceMinPredicate);
        }
    }

    private void addWinnerPriceMaxPredicate(List<Predicate> predicates, Root<Task> root, CriteriaBuilder criteriaBuilder, Double winnerPriceMax) {
        if (winnerPriceMax != null) {
            Predicate priceMaxPredicate = criteriaBuilder.lessThanOrEqualTo(
                    root.get("selectedProposal").get("proposedPrice"), winnerPriceMax
            );
            predicates.add(priceMaxPredicate);
        }
    }
}
