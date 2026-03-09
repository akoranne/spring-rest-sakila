package com.example.app.services.customer.repository.custom;

import com.example.app.common.util.ExpressionUtils;
import com.example.app.services.customer.domain.dto.CustomerDetailsDto;
import com.example.app.services.customer.domain.entity.QCustomerEntity;
import com.example.app.services.location.domain.entity.QAddressEntity;
import com.example.app.services.location.domain.entity.QCityEntity;
import com.example.app.services.payment.domain.dto.PaymentDto;
import com.example.app.services.payment.domain.entity.QPaymentEntity;
import com.example.app.services.payment.domain.mapper.PaymentMapper;
import com.example.app.services.rental.domain.dto.RentalDto;
import com.example.app.services.rental.domain.entity.QRentalEntity;
import com.example.app.services.rental.domain.mapper.RentalMapper;
import com.example.app.services.rental.domain.vo.RentalStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.app.common.util.ExpressionUtils.filterBetween;
import static com.example.app.common.util.ExpressionUtils.filterEquals;

@Repository
@RequiredArgsConstructor
public class CustomCustomerRepositoryImpl implements CustomCustomerRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final PaymentMapper paymentMapper;
    private final RentalMapper rentalMapper;

    @Override
    public List<CustomerDetailsDto.CustomerDetails> findAllCustomerDetailsList() {
        final var query = findCustomerDetail(null);
        return query.fetch();
    }

    @Override
    public Optional<CustomerDetailsDto.CustomerDetails> findCustomerDetailsById(Integer customerId) {
        final var query = findCustomerDetail(customerId);
        return Optional.of(query.fetchFirst());
    }

    private JPAQuery<CustomerDetailsDto.CustomerDetails> findCustomerDetail(Integer id) {
        final var customer = QCustomerEntity.customerEntity;
        final var address = QAddressEntity.addressEntity;
        final var city = QCityEntity.cityEntity;
        return jpaQueryFactory
                .select(Projections.constructor(CustomerDetailsDto.CustomerDetails.class,
                        customer.customerId.as(CustomerDetailsDto.CustomerDetails.Fields.id),
                        Expressions.asString(customer.fullName.firstName).concat(" ")
                                .concat(customer.fullName.lastName).as(CustomerDetailsDto.CustomerDetails.Fields.name),
                        address.address.as(CustomerDetailsDto.CustomerDetails.Fields.address),
                        address.postalCode.as(CustomerDetailsDto.CustomerDetails.Fields.zipCode),
                        address.phone.as(CustomerDetailsDto.CustomerDetails.Fields.phone),
                        city.city.as(CustomerDetailsDto.CustomerDetails.Fields.city),
                        city.countryId.as(CustomerDetailsDto.CustomerDetails.Fields.country),
                        Expressions.stringTemplate("IF({0}, 'active', '')", customer.active)
                                .as(CustomerDetailsDto.CustomerDetails.Fields.notes),
                        customer.storeId.as(CustomerDetailsDto.CustomerDetails.Fields.sid)))
                .from(customer)
                .join(address).on(customer.addressId.eq(address.addressId))
                .join(city).on(address.cityId.eq(city.cityId))
                .where(filterEquals(customer.customerId, id));
    }

    @Override
    public List<PaymentDto.Payment> findAllCustomerPaymentListByIdWithCondition(
            Integer customerId, LocalDate startDate, LocalDate endDate) {
        final var customer = QCustomerEntity.customerEntity;
        final var payment = QPaymentEntity.paymentEntity;
        final var query = jpaQueryFactory
                .selectFrom(payment)
                .join(customer).on(payment.customerId.eq(customer.customerId))
                .where(customer.customerId.eq(customerId))
                .where(filterBetween(payment.paymentDate,
                        Optional.ofNullable(startDate).map(LocalDate::atStartOfDay).orElse(null),
                        Optional.ofNullable(endDate).map(LocalDate::atStartOfDay).orElse(null)));
        return query.fetch().stream().map(paymentMapper::mapToDto).toList();
    }

    @Override
    public List<RentalDto.Rental> findAllCustomerRentalListByIdWithCondition(
            Integer customerId, RentalStatus status, LocalDate startDate, LocalDate endDate) {
        final var customer = QCustomerEntity.customerEntity;
        final var rental = QRentalEntity.rentalEntity;
        final var query = jpaQueryFactory
                .selectFrom(rental)
                .join(customer).on(rental.customerId.eq(customer.customerId))
                .where(customer.customerId.eq(customerId))
                .where(filterBetween(rental.rentalDate,
                        Optional.ofNullable(startDate).map(LocalDate::atStartOfDay).orElse(null),
                        Optional.ofNullable(endDate).map(LocalDate::atStartOfDay).orElse(null)))
                .where(ExpressionUtils.filterExpression(status == RentalStatus.RENTED, rental.returnDate.isNotNull()));
        return query.fetch().stream().map(rentalMapper::mapToDto).toList();
    }
}
