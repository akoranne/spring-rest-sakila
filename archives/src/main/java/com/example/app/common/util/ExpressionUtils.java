package com.example.app.common.util;

import com.querydsl.core.types.dsl.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExpressionUtils {
    public static BooleanExpression filterExpression(boolean condition, BooleanExpression expression) {
        return condition ? expression : null;
    }

    private static <T extends Comparable<?>, U extends ComparableExpressionBase<T>> BooleanExpression equalExpression(U path, T value) {
        return value != null ? path.eq(value) : null;
    }

    public static <T extends Number & Comparable<?>> BooleanExpression filterEquals(NumberExpression<T> path, T value) {
        return equalExpression(path, value);
    }

    public static <T extends Number & Comparable<?>> BooleanExpression filterEquals(NumberPath<T> path, T value) {
        return equalExpression(path, value);
    }

    public static <T extends Enum<T>> BooleanExpression filterEquals(EnumExpression<T> path, T value) {
        return equalExpression(path, value);
    }

    public static <T extends Enum<T>> BooleanExpression filterEquals(EnumPath<T> path, T value) {
        return equalExpression(path, value);
    }

    private static <T extends Comparable<?>> BooleanExpression betweenExpression(ComparableExpression<T> path, T start, T end) {
        if (start != null && end != null) {
            return path.between(start, end);
        } else if (start != null) {
            return path.goe(start);
        } else if (end != null) {
            return path.loe(end);
        } else {
            return null;
        }
    }

    public static <T extends Comparable<?>> BooleanExpression filterBetween(DateTimeExpression<T> path, T start, T end) {
        return betweenExpression(path, start, end);
    }

    public static <T extends Comparable<?>> BooleanExpression filterBetween(DateTimePath<T> path, T start, T end) {
        return betweenExpression(path, start, end);
    }
}
