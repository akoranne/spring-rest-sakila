package com.example.app.services.payment.repository.custom;

import com.example.app.common.constant.Country;
import com.example.app.services.catalog.domain.entity.QFilmCategoryEntity;
import com.example.app.services.catalog.domain.entity.QFilmEntity;
import com.example.app.services.location.domain.entity.QAddressEntity;
import com.example.app.services.location.domain.entity.QCityEntity;
import com.example.app.services.payment.domain.dto.PaymentDto;
import com.example.app.services.payment.domain.entity.QPaymentEntity;
import com.example.app.services.rental.domain.entity.QRentalEntity;
import com.example.app.services.staff.domain.entity.QStaffEntity;
import com.example.app.services.store.domain.dto.CategorySalesDto;
import com.example.app.services.store.domain.dto.StoreSalesDto;
import com.example.app.services.store.domain.entity.QInventoryEntity;
import com.example.app.services.store.domain.entity.QStoreEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomPaymentRepositoryImpl implements CustomPaymentRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private static void replaceCountryIdToCountry(StoreSalesDto.StoreSales result) {
        final var replacedStore = Country.replaceCountryIdToCountryInString(result.getStore());
        result.setStore(replacedStore);
    }

    @Override
    public List<PaymentDto.Payment> findAllPaymentDetailsList() {
        return null;
    }

    @Override
    public Optional<PaymentDto.Payment> findPaymentDetailsById(Integer paymentId) {
        return Optional.empty();
    }

    @Override
    public List<CategorySalesDto.CategorySales> calculateSalesByCategory() {
        final var payment = QPaymentEntity.paymentEntity;
        final var rental = QRentalEntity.rentalEntity;
        final var inventory = QInventoryEntity.inventoryEntity;
        final var film = QFilmEntity.filmEntity;
        final var filmCategory = QFilmCategoryEntity.filmCategoryEntity;

        final var totalSales = payment.amount.sum();
        final var query = jpaQueryFactory
                .select(Projections.constructor(CategorySalesDto.CategorySales.class,
                        filmCategory.categoryId.as("category"),
                        totalSales.as("totalSales")))
                .from(payment)
                .innerJoin(rental).on(rental.rentalId.eq(payment.rentalId))
                .innerJoin(inventory).on(inventory.inventoryId.eq(rental.inventoryId))
                .innerJoin(film).on(film.filmId.eq(inventory.filmId))
                .innerJoin(filmCategory).on(filmCategory.filmId.eq(film.filmId))
                .groupBy(filmCategory.categoryId)
                .orderBy(totalSales.desc());
        return query.fetch();
    }

    @Override
    public List<StoreSalesDto.StoreSales> calculateSalesByStore() {
        final var payment = QPaymentEntity.paymentEntity;
        final var rental = QRentalEntity.rentalEntity;
        final var inventory = QInventoryEntity.inventoryEntity;
        final var store = QStoreEntity.storeEntity;
        final var address = QAddressEntity.addressEntity;
        final var city = QCityEntity.cityEntity;
        final var staff = QStaffEntity.staffEntity;

        final var query = jpaQueryFactory
                .select(Projections.constructor(StoreSalesDto.StoreSales.class,
                        Expressions.asString(city.city).concat(",")
                                .concat(city.countryId.stringValue()).as("store"),
                        Expressions.asString(staff.fullName.firstName).concat(" ")
                                .concat(staff.fullName.lastName).as("manager"),
                        payment.amount.sum().as("totalSales")))
                .from(payment)
                .innerJoin(rental).on(rental.rentalId.eq(payment.rentalId))
                .innerJoin(inventory).on(inventory.inventoryId.eq(rental.inventoryId))
                .innerJoin(store).on(store.storeId.eq(inventory.storeId))
                .innerJoin(address).on(address.addressId.eq(store.addressId))
                .innerJoin(city).on(city.cityId.eq(address.cityId))
                .innerJoin(staff).on(staff.staffId.eq(store.managerStaffId))
                .groupBy(store.storeId)
                .orderBy(store.storeId.asc());
        final var result = query.fetch();
        result.forEach(CustomPaymentRepositoryImpl::replaceCountryIdToCountry);
        return result;
    }
}
