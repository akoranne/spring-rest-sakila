package com.example.app.services.rental.repository.custom;

import com.example.app.services.rental.domain.entity.QRentalEntity;
import com.example.app.services.rental.domain.entity.RentalEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomRentalRepositoryImpl implements CustomRentalRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public RentalEntity findRentedDvdRentalId(Integer customerId, Integer inventoryId) {
        final var rental = QRentalEntity.rentalEntity;
        final var query = jpaQueryFactory
                .select(rental)
                .from(rental)
                .where(rental.customerId.eq(customerId))
                .where(rental.inventoryId.eq(inventoryId))
                .where(rental.returnDate.isNull());
        return query.fetchFirst();
    }
}
