package com.phonebook_system.contact_service.mapper;

import com.phonebook_system.contact_service.entity.PersonEntity;
import com.phonebook_system.contact_service.model.request.CreatePersonRequest;
import com.phonebook_system.contact_service.model.request.UpdatePersonRequest;
import com.phonebook_system.contact_service.model.response.PersonDetailResponse;
import com.phonebook_system.contact_service.model.response.PersonResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ContactInfoMapper.class })
public interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactInfoList", ignore = true)
    PersonEntity toEntity(CreatePersonRequest request);

    PersonResponse toResponse(PersonEntity entity);

    PersonDetailResponse toDetailResponse(PersonEntity entity);

    List<PersonResponse> toResponseList(List<PersonEntity> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactInfoList", ignore = true)
    void updateEntityFromRequest(UpdatePersonRequest request, @MappingTarget PersonEntity entity);
}
