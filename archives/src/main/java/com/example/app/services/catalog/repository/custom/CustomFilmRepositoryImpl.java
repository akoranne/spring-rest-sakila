package com.example.app.services.catalog.repository.custom;

import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.blazebit.persistence.querydsl.JPQLNextExpressions;
import com.example.app.common.domain.dto.FullName;
import com.example.app.services.catalog.domain.dto.ActorDto;
import com.example.app.services.catalog.domain.dto.FilmDetailsDto;
import com.example.app.services.catalog.domain.dto.FilmDto;
import com.example.app.services.catalog.domain.entity.QActorEntity;
import com.example.app.services.catalog.domain.entity.QFilmActorEntity;
import com.example.app.services.catalog.domain.entity.QFilmCategoryEntity;
import com.example.app.services.catalog.domain.entity.QFilmEntity;
import com.example.app.services.catalog.domain.mapper.FilmMapper;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.app.common.util.ExpressionUtils.filterEquals;

@Repository
@RequiredArgsConstructor
public class CustomFilmRepositoryImpl implements CustomFilmRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final BlazeJPAQueryFactory blazeJPAQueryFactory;
    private final FilmMapper filmMapper;

    @Override
    public List<FilmDto.Film> findAllFilmList(Pageable pageable) {
        final var film = QFilmEntity.filmEntity;
        final var query = jpaQueryFactory
                .selectFrom(film)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        return query.fetch().stream().map(filmMapper::mapToDto).toList();
    }

    @Override
    public List<FilmDto.Film> findAllFilmListWithFilter(FilmDto.FilterOption condition, Pageable pageable) {
        final var film = QFilmEntity.filmEntity;
        final var category = QFilmCategoryEntity.filmCategoryEntity;
        final var query = jpaQueryFactory
                .selectFrom(film)
                .innerJoin(category).on(category.filmId.eq(film.filmId))
                .where(filterEquals(category.categoryId, condition.getCategory()))
                .where(filterEquals(film.releaseYear.year(), condition.getReleaseYear().getValue()))
                .where(filterEquals(film.rating, condition.getRating()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        return query.fetch().stream().map(filmMapper::mapToDto).toList();
    }

    @Override
    public List<ActorDto.Actor> findAllFilmActorListById(Integer filmId, Pageable pageable) {
        final var query = findFilmActor(filmId, null, pageable);
        return query.fetch();
    }

    @Override
    public Optional<ActorDto.Actor> findFilmActorById(Integer filmId, Integer actorId) {
        final var query = findFilmActor(filmId, actorId, Pageable.unpaged());
        return Optional.of(query.fetchFirst());
    }

    private JPAQuery<ActorDto.Actor> findFilmActor(Integer filmId, Integer actorId, Pageable pageable) {
        final var actor = QActorEntity.actorEntity;
        final var filmActor = QFilmActorEntity.filmActorEntity;
        final var film = QFilmEntity.filmEntity;

        return jpaQueryFactory
                .select(Projections.constructor(ActorDto.Actor.class,
                        actor.actorId.as(ActorDto.Actor.Fields.actorId),
                        actor.fullName.firstName.as(FullName.Fields.firstName),
                        actor.fullName.lastName.as(FullName.Fields.lastName)))
                .from(film)
                .leftJoin(filmActor).on(filmActor.filmId.eq(film.filmId))
                .leftJoin(actor).on(actor.actorId.eq(filmActor.actorId))
                .where(film.filmId.eq(filmId))
                .where(filterEquals(actor.actorId, actorId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
    }

    @Override
    public List<FilmDetailsDto.FilmDetails> findAllFilmListDetail() {
        final var query = findFilmDetail(null);
        return query.fetch();
    }

    @Override
    public Optional<FilmDetailsDto.FilmDetails> findFilmDetailsById(Integer filmId) {
        final var query = findFilmDetail(filmId);
        return Optional.of(query.fetchFirst());
    }

    private BlazeJPAQuery<FilmDetailsDto.FilmDetails> findFilmDetail(Integer id) {
        final var actor = QActorEntity.actorEntity;
        final var film = QFilmEntity.filmEntity;
        final var filmActor = QFilmActorEntity.filmActorEntity;
        final var filmCategory = QFilmCategoryEntity.filmCategoryEntity;

        return blazeJPAQueryFactory
                .select(Projections.constructor(FilmDetailsDto.FilmDetails.class,
                        film.filmId.as(FilmDetailsDto.FilmDetails.Fields.filmId),
                        film.title.as(FilmDetailsDto.FilmDetails.Fields.title),
                        film.description.as(FilmDetailsDto.FilmDetails.Fields.description),
                        filmCategory.categoryId.as(FilmDetailsDto.FilmDetails.Fields.category),
                        film.rentalRate.as(FilmDetailsDto.FilmDetails.Fields.price),
                        film.length.as(FilmDetailsDto.FilmDetails.Fields.length),
                        film.rating.as(FilmDetailsDto.FilmDetails.Fields.rating),
                        JPQLNextExpressions.groupConcat(actor.fullName.firstName.concat(" ")
                                .concat(actor.fullName.lastName), ", ").as(FilmDetailsDto.FilmDetails.Fields.actors)))
                .from(film)
                .leftJoin(filmCategory).on(filmCategory.filmId.eq(film.filmId))
                .leftJoin(filmActor).on(filmActor.filmId.eq(film.filmId))
                .leftJoin(actor).on(actor.actorId.eq(filmActor.actorId))
                .where(filterEquals(film.filmId, id))
                .groupBy(film.filmId, filmCategory.categoryId);
    }
}
