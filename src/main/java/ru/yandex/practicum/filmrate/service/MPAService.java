package ru.yandex.practicum.filmrate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmrate.model.MPA;
import ru.yandex.practicum.filmrate.storage.MPA.MPAStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MPAService {
    @Autowired
    private MPAStorage ratingStorage;

    public Collection<MPA> findAll() {
        return ratingStorage.findAll();
    }

    public MPA read(int id) {
        return ratingStorage.read(id);
    }
}
