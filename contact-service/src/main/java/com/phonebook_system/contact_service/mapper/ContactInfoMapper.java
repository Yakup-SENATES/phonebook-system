package com.phonebook_system.contact_service.mapper;

import com.phonebook_system.contact_service.entity.ContactInfoEntity;
import com.phonebook_system.contact_service.model.request.CreateContactInfoRequest;
import com.phonebook_system.contact_service.model.response.ContactInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContactInfoMapper {
    ContactInfoMapper INSTANCE = Mappers.getMapper(ContactInfoMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    ContactInfoEntity toEntity(CreateContactInfoRequest request);

    ContactInfoResponse toResponse(ContactInfoEntity entity);

    List<ContactInfoResponse> toResponseList(List<ContactInfoEntity> entities);
}
