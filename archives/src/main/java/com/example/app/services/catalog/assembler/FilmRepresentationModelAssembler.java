package com.example.app.services.catalog.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.catalog.controller.FilmController;
import com.example.app.services.catalog.domain.dto.FilmDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FilmRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        FilmDto.Film, FilmDto.FilmResponse> {
    public FilmRepresentationModelAssembler() {
        super(FilmController.class, FilmDto.FilmResponse.class);
    }

    @Override
    @NonNull
    public FilmDto.FilmResponse toModel(@NonNull FilmDto.Film entity) {
        final var model = instantiateModel(entity);
        model.setFilm(entity);
        model.add(linkTo(methodOn(FilmController.class).getFilm(entity.getFilmId()))
                .withSelfRel());
        model.add(linkTo(methodOn(FilmController.class).getFilmList(null, null, null, Pageable.unpaged()))
                .withRel(HalRelation.Fields.filmList));
        return model;
    }

    @Override
    @NonNull
    public CollectionModel<FilmDto.FilmResponse> toCollectionModel(
            @NonNull Iterable<? extends FilmDto.Film> entities) {
        final var collectionModel = super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(FilmController.class).getFilmList(null, null, null, Pageable.unpaged()))
                .withSelfRel());
        return collectionModel;
    }
}
