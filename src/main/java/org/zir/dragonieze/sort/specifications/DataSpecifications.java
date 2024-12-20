package org.zir.dragonieze.sort.specifications;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class DataSpecifications {

    public static <T, V> Specification<T> hasField(String fieldName, V value) {
        return (root, query, criteriaBuilder) ->
                value == null ? null :
                        criteriaBuilder.equal(getPath(root, fieldName), value); //was equal
    }


    public static <T> Path<?> getPath(Root<T> root, String fieldName) {
        if (fieldName.contains(".")) {
            String[] parts = fieldName.split("\\.");     //ex: user.id
            Path<?> path = root.get(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                path = path.get(parts[i]);
            }
            return path;
        }
        return root.get(fieldName);
    }

    public static <T> Specification<T> hasFieldLike(String fieldName, String value) {
        return (root, query, criteriaBuilder) ->
                value == null || value.trim().isEmpty() ? null :
                        criteriaBuilder.like(criteriaBuilder.lower((Expression<String>) getPath(root, fieldName)), "%" + value.toLowerCase() + "%");
    }
}
