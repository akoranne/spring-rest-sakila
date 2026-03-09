package com.example.app.services.catalog.repository.custom;

import com.example.app.services.catalog.domain.dto.ActorDto;
import com.example.app.services.catalog.domain.dto.FilmDetailsDto;
import com.example.app.services.catalog.domain.dto.FilmDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomFilmRepository {
    List<FilmDto.Film> findAllFilmList(Pageable pageable);

    List<FilmDto.Film> findAllFilmListWithFilter(FilmDto.FilterOption condition, Pageable pageable);

    List<ActorDto.Actor> findAllFilmActorListById(Integer filmId, Pageable pageable);

    Optional<ActorDto.Actor> findFilmActorById(Integer filmId, Integer actorId);

    List<FilmDetailsDto.FilmDetails> findAllFilmListDetail();

    Optional<FilmDetailsDto.FilmDetails> findFilmDetailsById(Integer filmId);
}
