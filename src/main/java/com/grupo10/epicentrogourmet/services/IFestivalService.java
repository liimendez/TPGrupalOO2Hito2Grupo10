package com.grupo10.epicentrogourmet.services;

import com.grupo10.epicentrogourmet.entities.Festival;
import org.hibernate.internal.util.Optional;

import java.util.List;

public interface IFestivalService {
    List<Festival> getAll();
    Optional<Festival> findById(Long id);
    Festival insertOrUpdate(Festival festival);
    boolean remove(Long id);
}