package ru.yandex.practicum.filmrate.storage.MPA;

import ru.yandex.practicum.filmrate.model.MPA;

import java.util.Collection;

public interface MPAStorage {
    Collection<MPA> findAll();

    MPA read(int id);
}
