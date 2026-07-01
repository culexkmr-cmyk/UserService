package com.culex.userService.utilities;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

public class EntityUtils {
    public static <T, ID> T findEntity(CrudRepository<T, ID> repository, ID searchValue) {
        return repository.findById(searchValue)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Сущность не найдена в репозитории по значению: %s", searchValue)
                ));
    }
}

