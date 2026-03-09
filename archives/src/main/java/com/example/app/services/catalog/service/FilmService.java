package com.example.app.services.catalog.service;

import com.example.app.services.catalog.domain.dto.ActorDto;
import com.example.app.services.catalog.domain.dto.FilmDetailsDto;
import com.example.app.services.catalog.domain.dto.FilmDto;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FilmService {
    List<FilmDto.Film> getFilmList();

    List<FilmDto.Film> getFilmList(FilmDto.FilterOption condition, Pageable pageable);

    Optional<FilmDto.Film> getFilm(Integer filmId);

    List<ActorDto.Actor> getFilmActorList(Integer filmId, Pageable pageable);

    Optional<ActorDto.Actor> getFilmActor(Integer filmId, Integer actorId);

    List<FilmDetailsDto.FilmDetails> getFilmDetailsList();

    Optional<FilmDetailsDto.FilmDetails> getFilmDetails(Integer filmId);

    BigDecimal getFilmRentalPrice(Integer filmId);

    FilmDto.Film addFilm(FilmDto.FilmRequest model);

    FilmDto.Film updateFilm(Integer filmId, FilmDto.FilmRequest model);

    void deleteFilm(Integer filmId);
}
