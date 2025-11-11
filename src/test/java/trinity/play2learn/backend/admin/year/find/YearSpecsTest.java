package trinity.play2learn.backend.admin.year.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.specs.YearSpecs;

@ExtendWith(MockitoExtension.class)
class YearSpecsTest {

    @Mock
    private Root<Year> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> objectPath;

    @Mock
    private Path<String> namePath;

    @Mock
    private Expression<String> stringExpression;

    @Mock
    private Predicate predicate;

    @Nested
    @DisplayName("notDeleted")
    class NotDeleted {

        @Test
        @DisplayName("Genera predicado isNull sobre deletedAt")
        void buildsIsNullPredicateForDeletedAt() {
            when(root.get("deletedAt")).thenReturn(objectPath);
            when(criteriaBuilder.isNull(objectPath)).thenReturn(predicate);

            Predicate result = YearSpecs.notDeleted().toPredicate(root, query, criteriaBuilder);

            assertThat(result).isSameAs(predicate);
            verify(criteriaBuilder).isNull(objectPath);
            verify(root).get("deletedAt");
        }
    }

    @Nested
    @DisplayName("nameContains")
    class NameContains {

        @Test
        @DisplayName("Genera predicado LIKE case-insensitive")
        @SuppressWarnings({ "unchecked", "rawtypes" })
        void buildsLikePredicateWithLowerCaseName() {
            when(root.get("name")).thenReturn((Path) namePath);
            when(criteriaBuilder.lower((Expression<String>) namePath)).thenReturn(stringExpression);
            when(criteriaBuilder.like(stringExpression, "%básico%")).thenReturn(predicate);

            Predicate result = YearSpecs.nameContains("BÁsico").toPredicate(root, query, criteriaBuilder);

            assertThat(result).isSameAs(predicate);
            verify(criteriaBuilder).lower((Expression<String>) namePath);
            verify(criteriaBuilder).like(stringExpression, "%básico%");
        }
    }

    @Nested
    @DisplayName("genericFilter")
    class GenericFilter {

        @Test
        @DisplayName("Genera predicado equal cuando el campo existe")
        @SuppressWarnings({ "unchecked", "rawtypes" })
        void buildsEqualPredicateWhenFieldExists() {
            when(root.get("name")).thenReturn((Path) namePath);
            when(criteriaBuilder.equal(namePath, "Primero Básico")).thenReturn(predicate);

            Predicate result = YearSpecs.genericFilter("name", "Primero Básico").toPredicate(root, query, criteriaBuilder);

            assertThat(result).isSameAs(predicate);
            verify(criteriaBuilder).equal(namePath, "Primero Básico");
        }

        @Test
        @DisplayName("Devuelve conjunción cuando el campo no existe")
        void returnsConjunctionWhenFieldIsInvalid() {
            when(root.get("invalid")).thenThrow(new IllegalArgumentException("invalid field"));
            Predicate conjunction = Mockito.mock(Predicate.class);
            when(criteriaBuilder.conjunction()).thenReturn(conjunction);

            Predicate result = YearSpecs.genericFilter("invalid", "value").toPredicate(root, query, criteriaBuilder);

            assertThat(result).isSameAs(conjunction);
            verify(criteriaBuilder).conjunction();
        }
    }
}

