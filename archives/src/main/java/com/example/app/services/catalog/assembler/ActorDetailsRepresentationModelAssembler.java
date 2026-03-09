package com.example.app.services.catalog.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.catalog.controller.ActorController;
import com.example.app.services.catalog.domain.dto.ActorDetailsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ActorDetailsRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        ActorDetailsDto.ActorDetails, ActorDetailsDto.ActorDetailsResponse> {
    public ActorDetailsRepresentationModelAssembler() {
        super(ActorController.class, ActorDetailsDto.ActorDetailsResponse.class);
    }

    @Override
    @NonNull
    public ActorDetailsDto.ActorDetailsResponse toModel(@NonNull ActorDetailsDto.ActorDetails entity) {
        final var model = instantiateModel(entity);
        model.setActorDetailsModel(entity);
        model.add(linkTo(methodOn(ActorController.class).getActorDetails(entity.getActorId()))
                .withSelfRel());
        model.add(linkTo(methodOn(ActorController.class).getActor(entity.getActorId()))
                .withRel(HalRelation.Fields.actor));
        model.add(linkTo(methodOn(ActorController.class).getActorList(Pageable.unpaged()))
                .withRel(HalRelation.Fields.actorList));
        return model;
    }

    @Override
    @NonNull
    public CollectionModel<ActorDetailsDto.ActorDetailsResponse> toCollectionModel(
            @NonNull Iterable<? extends ActorDetailsDto.ActorDetails> entities) {
        return super.toCollectionModel(entities);
    }
}
