package com.example.app.services.catalog.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.catalog.controller.FilmController;
import com.example.app.services.catalog.domain.dto.FilmDetailsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FilmDetailsRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        FilmDetailsDto.FilmDetails, FilmDetailsDto.FilmDetailsResponse> {
    public FilmDetailsRepresentationModelAssembler() {
        super(FilmDetailsDto.FilmDetails.class, FilmDetailsDto.FilmDetailsResponse.class);
    }

    @Override
    @NonNull
    public FilmDetailsDto.FilmDetailsResponse toModel(@NonNull FilmDetailsDto.FilmDetails entity) {
        final var model = instantiateModel(entity);
        model.setFilmDetails(entity);
        model.add(linkTo(methodOn(FilmController.class).getFilmDetails(entity.getFilmId()))
                .withSelfRel());
        model.add(linkTo(methodOn(FilmController.class).getFilm(entity.getFilmId()))
                .withRel(HalRelation.Fields.film));
        model.add(linkTo(methodOn(FilmController.class).getFilmList(null, null, null, Pageable.unpaged()))
                .withRel(HalRelation.Fields.filmList));
        return model;
    }
}
