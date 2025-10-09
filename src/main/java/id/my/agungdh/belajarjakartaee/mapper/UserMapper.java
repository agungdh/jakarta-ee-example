package id.my.agungdh.belajarjakartaee.mapper;

import id.my.agungdh.belajarjakartaee.dto.UserDTO;
import id.my.agungdh.belajarjakartaee.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "cdi",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        builder = @Builder(disableBuilder = true)
)
public interface UserMapper {

    UserDTO toDTO(User user);
    List<UserDTO> toDTOs(List<User> users);

    @Mapping(target = "id", ignore = true) // id di-generate DB
    User toEntity(UserDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UserDTO dto, @MappingTarget User entity);
}
