package org.burgas.entityreflection.mapper.contract;

import org.burgas.entityreflection.dto.Request;
import org.burgas.entityreflection.dto.Response;
import org.burgas.entityreflection.entity.BaseEntity;
import org.burgas.entityreflection.exception.EmptyEntityFieldException;
import org.springframework.stereotype.Component;

@Component
public interface EntityMapper<K, T extends Request<K>, V extends BaseEntity, S extends Response<K>, M extends Response<K>> {

    default <D> D handleData(final D requestData, final D entityData) {
        return requestData == null || requestData == "" ? entityData : requestData;
    }

    default <D> D handleDataThrowable(final D requestData, final String message) {
        if (requestData == null || requestData == "")
            throw new EmptyEntityFieldException(message);
        return requestData;
    }

    V toEntity(T t);

    S toFullResponse(V v);

    M toShortResponse(V v);
}
