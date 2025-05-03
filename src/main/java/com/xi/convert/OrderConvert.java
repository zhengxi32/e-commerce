package com.xi.convert;

import com.xi.domain.OrderDo;
import com.xi.domain.dto.OrderDto;
import com.xi.domain.param.OrderParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderConvert {
    OrderConvert INSTANCE = Mappers.getMapper(OrderConvert.class);

    OrderDto OrderParamToDto(OrderParam orderParam);

    OrderDo OrderDtoToDo(OrderDto orderDto);

    OrderDto OrderDoToDto(OrderDo orderDo);
}
