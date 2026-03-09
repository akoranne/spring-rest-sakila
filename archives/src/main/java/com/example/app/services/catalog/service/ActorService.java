package com.example.app.services.catalog.service;

import com.example.app.services.catalog.domain.dto.ActorDetailsDto;
import com.example.app.services.catalog.domain.dto.ActorDto;
import com.example.app.services.catalog.domain.dto.FilmDetailsDto;
import com.example.app.services.catalog.domain.dto.FilmDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ActorService {
    List<ActorDto.Actor> getActorList(Pageable pageable);

    Optional<ActorDto.Actor> getActor(Integer actorId);

    List<ActorDetailsDto.ActorDetails> getActorDetailsList();

    Optional<ActorDetailsDto.ActorDetails> getActorDetails(Integer actorId);

    List<FilmDto.Film> getActorFilmList(Integer actorId, FilmDto.FilterOption condition, Pageable pageable);

    Optional<FilmDto.Film> getActorFilm(Integer actorId, Integer filmId);

    Optional<FilmDetailsDto.FilmDetails> getActorFilmDetails(Integer actorId, Integer filmId);

    List<ActorDto.Actor> searchActorList(String name);

    ActorDto.Actor addActor(ActorDto.ActorRequest model);

    FilmDto.Film addActorFilm(Integer actorId, Integer filmId);

    ActorDto.Actor updateActor(Integer actorId, ActorDto.ActorRequest model);

    void deleteActor(Integer actorId);

    void removeActorFilm(Integer actorId, Integer filmId);
}
