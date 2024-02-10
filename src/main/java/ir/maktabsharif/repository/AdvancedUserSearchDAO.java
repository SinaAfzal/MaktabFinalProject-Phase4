package ir.maktabsharif.repository;

import io.micrometer.common.util.StringUtils;
import ir.maktabsharif.model.BaseUser;

import ir.maktabsharif.model.Category;
import ir.maktabsharif.model.TradesMan;
import ir.maktabsharif.model.enumeration.TradesManStatus;
import ir.maktabsharif.model.enumeration.UserRole;
import ir.maktabsharif.service.dto.request.AdvancedUserSearchDTO;
import jakarta.persistence.EntityManager;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AdvancedUserSearchDAO { //todo use Specification to develop this API in springboot data-jpa
    private final EntityManager entityManager;

    public AdvancedUserSearchDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<BaseUser> findUsers(AdvancedUserSearchDTO advancedUserSearchDTO) { //todo secure method
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BaseUser> query = criteriaBuilder.createQuery(BaseUser.class);
        Root<BaseUser> root = query.from(BaseUser.class);
        query.select(root);

        List<Predicate> predicates = new ArrayList<>();

        //add generic predicates
        addFirstNamePredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getFirstName());
        addLastNamePredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getLastName());
        addRolePredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getRole());
        addEmailPredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getEmail());
        addIsActivePredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.isActive());
        addRegistrationDateFromPredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getRegistrationDateFrom());
        addRegistrationDateToPredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getRegistrationDateTo());

        //add tradesman-specific predicates
        addTradesManRatingMinPredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getTradesManRatingMin(), advancedUserSearchDTO.getRole());
        addTradesManRatingMaxPredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getTradesManRatingMax(), advancedUserSearchDTO.getRole());
        addTradesManStatusPredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getTradesManStatus(), advancedUserSearchDTO.getRole());
        addTradesManCreditMinPredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getTradesManEarnedCreditMin(), advancedUserSearchDTO.getRole());
        addTradesManCreditMaxPredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getTradesManEarnedCreditMax(), advancedUserSearchDTO.getRole());
        addTradesManCategoryPredicate(predicates, root, criteriaBuilder, query, advancedUserSearchDTO.getTradesManSubCategoryId(), advancedUserSearchDTO.getRole());


        //add customer-specific predicates
        addCustomerBalanceMinPredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getCustomerPurchasedBalanceMin(), advancedUserSearchDTO.getRole());
        addCustomerBalanceMaxPredicate(predicates, root, criteriaBuilder, advancedUserSearchDTO.getCustomerPurchasedBalanceMax(), advancedUserSearchDTO.getRole());

        if (predicates.size() > 0) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        TypedQuery<BaseUser> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    private void addFirstNamePredicate(List<Predicate> predicates, Root<BaseUser> root,
                                       CriteriaBuilder criteriaBuilder, String firstName) {
        if (StringUtils.isNotBlank(firstName)) {
//            c.firstName like '%:firstName%'
            predicates.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("firstName")),
                            "%" + firstName.toLowerCase() + "%"
                    )
            );
        }
    }

    private void addLastNamePredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, String lastName) {
        if (StringUtils.isNotBlank(lastName)) {
            predicates.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("lastName")),
                            "%" + lastName.toLowerCase() + "%"
                    )
            );
        }
    }

    private void addRolePredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, UserRole role) {
        if (role != null) {
            Predicate rolePredicate = criteriaBuilder.equal(
                    root.get("role"), role
            );
            predicates.add(rolePredicate);
        }
    }

    private void addEmailPredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, String email) {
        if (StringUtils.isNotBlank(email)) {
            Predicate emailPredicate = criteriaBuilder.equal
                    (
                            criteriaBuilder.lower(root.get("email"))
                            , email.toLowerCase()
                    );
            predicates.add(emailPredicate);
        }
    }

    private void addIsActivePredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, Boolean isActive) {
        if (isActive != null) {
            Predicate isActivePredicate = criteriaBuilder.equal(
                    root.get("isActive"), isActive
            );
            predicates.add(isActivePredicate);
        }
    }

    private void addRegistrationDateFromPredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, LocalDate fromDate) {
        if (fromDate != null) {
            Predicate fromPredicate = criteriaBuilder.greaterThanOrEqualTo(
                    root.get("registrationDateTime"), fromDate
            );
            predicates.add(fromPredicate);
        }
    }

    private void addRegistrationDateToPredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, LocalDate toDate) {
        if (toDate != null) {
            Predicate toPredicate = criteriaBuilder.lessThanOrEqualTo(
                    root.get("registrationDateTime"), toDate
            );
            predicates.add(toPredicate);
        }
    }

    private void addTradesManStatusPredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, TradesManStatus tradesManStatus, UserRole role) {
        if (
                tradesManStatus != null
                        &&
                        (role == null || role.equals(UserRole.TRADESMAN))
        ) {
            Predicate tradesmanStatusPredicate = criteriaBuilder.equal(
                    root.get("status"), tradesManStatus
            );
            predicates.add(tradesmanStatusPredicate);
        }
    }

    private void addTradesManRatingMinPredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, Float minRating, UserRole role) {
        if (
                minRating != null
                        &&
                        (role == null || role.equals(UserRole.TRADESMAN))
        ) {
            Predicate tradesmanRatingMinPredicate = criteriaBuilder.greaterThanOrEqualTo(
                    root.get("rating"), minRating
            );
            predicates.add(tradesmanRatingMinPredicate);
        }
    }

    private void addTradesManRatingMaxPredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, Float maxRating, UserRole role) {
        if (
                maxRating != null
                        &&
                        (role == null || role.equals(UserRole.TRADESMAN))
        ) {
            Predicate tradesmanRatingMaxPredicate = criteriaBuilder.lessThanOrEqualTo(
                    root.get("rating"), maxRating
            );
            predicates.add(tradesmanRatingMaxPredicate);
        }
    }

    private void addTradesManCreditMinPredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, Double minCredit, UserRole role) {
        if (
                minCredit != null
                        &&
                        (role == null || role.equals(UserRole.TRADESMAN))
        ) {
            Predicate tradesmanMinCreditPredicate = criteriaBuilder.greaterThanOrEqualTo(
                    root.get("earnedCredit"), minCredit
            );
            predicates.add(tradesmanMinCreditPredicate);
        }
    }

    private void addTradesManCreditMaxPredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, Double maxCredit, UserRole role) {
        if (
                maxCredit != null
                        &&
                        (role == null || role.equals(UserRole.TRADESMAN))
        ) {
            Predicate tradesmanMaxCreditPredicate = criteriaBuilder.lessThanOrEqualTo(
                    root.get("earnedCredit"), maxCredit
            );
            predicates.add(tradesmanMaxCreditPredicate);
        }
    }

    /**
     * the following predicate generates a criteria query somehow equivalent to this SQL query:
     * SELECT *
     * FROM base_user b
     * WHERE b.id IN (
     * SELECT t.id
     * FROM category c
     * JOIN category_tradesmen ct ON c.id = ct.category_id
     * JOIN trades_man t ON ct.trades_man_id = t.id
     * WHERE c.id = ?
     * )
     */
    private void addTradesManCategoryPredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<BaseUser> query, Long catId, UserRole role) {
        if (
                catId != null
                        &&
                        (role == null || role.equals(UserRole.TRADESMAN))
        ) {

            //creates a subquery of type Long (IDs of categories) from main query of type BaseUser and then gets a root of Categrory. this is because the relationship between tradesman and category is one way and is handled from category side by putting a list<TradesMan> filed in Category entity.

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Category> subqueryRoot = subquery.from(Category.class);

            //make a join from category root to tradesman entity-- note that name of the List<TradesMan> field in the category is 'tradesMen'
            Join<Category, TradesMan> tradesMenJoin = subqueryRoot.join("tradesMen");

            //Selects the ID of the joined TradesMan entity and adds a where clause to filter the results based on the specified category ID.
            subquery.select(tradesMenJoin.get("id").as(Long.class)).where(criteriaBuilder.equal(subqueryRoot.get("id"), catId));

            //Returns a predicate that filters the tradesmen based on their IDs. The 'in' predicate checks if the tradesman ID is in the list of IDs returned by the subquery.
            Predicate tradesManCategoryPredicate = criteriaBuilder.in(root.get("id")).value(subquery);

            predicates.add(tradesManCategoryPredicate);
        }
    }

    private void addCustomerBalanceMinPredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, Double minBalance, UserRole role) {
        if (
                minBalance != null
                        &&
                        (role == null || role.equals(UserRole.CUSTOMER))
        ) {
            Predicate customerMinBalancePredicate = criteriaBuilder.greaterThanOrEqualTo(
                    root.get("purchasedBalance"), minBalance
            );
            predicates.add(customerMinBalancePredicate);
        }
    }

    private void addCustomerBalanceMaxPredicate(List<Predicate> predicates, Root<BaseUser> root, CriteriaBuilder criteriaBuilder, Double maxBalance, UserRole role) {
        if (
                maxBalance != null
                        &&
                        (role == null || role.equals(UserRole.CUSTOMER))
        ) {
            Predicate customerMaxBalancePredicate = criteriaBuilder.lessThanOrEqualTo(
                    root.get("purchasedBalance"), maxBalance
            );
            predicates.add(customerMaxBalancePredicate);
        }
    }
}
