package org.burgas.entityreflection.service.contract;

import org.burgas.entityreflection.dto.Request;
import org.burgas.entityreflection.dto.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CrudService<K, T extends Request<K>, S extends Response<K>, V extends Response<K>> {

    List<V> findAll();

    S findById(final K k);

    K createOrUpdate(final T t);

    void delete(final K k);
}
