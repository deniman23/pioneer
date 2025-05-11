package org.example.pioneer.repository.spec;


import jakarta.persistence.criteria.JoinType;
import org.example.pioneer.model.User;
import org.springframework.data.jpa.domain.Specification;


import java.time.LocalDate;

public class UserSpecifications {

    public static Specification<User> dateOfBirthAfter(LocalDate date) {
        return (root, q, cb) ->
                cb.greaterThan(root.get("dateOfBirth"), date);
    }

    public static Specification<User> nameStartsWith(String prefix) {
        return (root, q, cb) ->
                cb.like(cb.upper(root.get("name")), prefix.toUpperCase() + "%");
    }

    public static Specification<User> hasEmail(String email) {
        return (root, q, cb) -> {
            var join = root.join("emails", JoinType.LEFT);
            return cb.equal(cb.lower(join.get("email")), email.toLowerCase());
        };
    }

    public static Specification<User> hasPhone(String phone) {
        return (root, q, cb) -> {
            var join = root.join("phones", JoinType.LEFT);
            return cb.equal(join.get("phone"), phone);
        };
    }
}
