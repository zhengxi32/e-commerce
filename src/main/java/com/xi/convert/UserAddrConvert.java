package com.xi.convert;

import com.xi.domain.UserAddrDo;
import com.xi.domain.dto.UserAddrDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAddrConvert {
    UserAddrConvert INSTANCE = Mappers.getMapper(UserAddrConvert.class);

    UserAddrDto UserAddrDoToDto(UserAddrDo userAddrDo);
}
