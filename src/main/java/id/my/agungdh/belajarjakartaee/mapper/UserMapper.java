package id.my.agungdh.belajarjakartaee.mapper;

import id.my.agungdh.belajarjakartaee.dto.UserDTO;
import id.my.agungdh.belajarjakartaee.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "cdi",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    // Entity -> DTO
    UserDTO toDTO(User user);

    List<UserDTO> toDTOs(List<User> users);

    // DTO -> Entity (untuk create). Id diabaikan (Entity tidak punya setter id).
    @Mapping(target = "id", ignore = true)
    User toEntity(UserDTO dto);

    // Update entity yang sudah ada dari DTO (PUT)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UserDTO dto, @MappingTarget User entity);
}
