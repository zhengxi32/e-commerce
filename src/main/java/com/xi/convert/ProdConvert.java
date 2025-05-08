package com.xi.convert;

import com.xi.entity.tb.ProdDo;
import com.xi.entity.dto.ProdDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProdConvert {
    ProdConvert INSTANCE = Mappers.getMapper(ProdConvert.class);

    ProdDto ProdDoToDto(ProdDo prodDo);


}
