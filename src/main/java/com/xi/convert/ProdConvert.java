package com.xi.convert;

import com.xi.domain.ProdDo;
import com.xi.domain.dto.ProdDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProdConvert {
    ProdConvert INSTANCE = Mappers.getMapper(ProdConvert.class);

    ProdDto ProdDoToDto(ProdDo prodDo);


}
