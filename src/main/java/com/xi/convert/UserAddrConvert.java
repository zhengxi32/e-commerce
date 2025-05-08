package com.xi.convert;

import com.xi.entity.tb.UserAddrDo;
import com.xi.entity.dto.UserAddrDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAddrConvert {
    UserAddrConvert INSTANCE = Mappers.getMapper(UserAddrConvert.class);

    UserAddrDto UserAddrDoToDto(UserAddrDo userAddrDo);
}
