package com.javaroadmap.spring.s03.homework;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

/** Готовый сервис — менять не нужно. Твоя зона — веб-слой над ним. */
@Service
public class NotesService {

    private final Map<Long, Note> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public List<Note> search(String containsOrNull) {
        return storage.values().stream()
                .filter(n -> containsOrNull == null
                        || n.text().toLowerCase(Locale.ROOT).contains(containsOrNull.toLowerCase(Locale.ROOT)))
                .sorted(Comparator.comparingLong(Note::id))
                .toList();
    }

    public Note byId(long id) {
        Note note = storage.get(id);
        if (note == null) {
            throw new NoteNotFoundException(id);
        }
        return note;
    }

    public Note create(String text) {
        long id = sequence.incrementAndGet();
        Note note = new Note(id, text);
        storage.put(id, note);
        return note;
    }

    public void delete(long id) {
        if (storage.remove(id) == null) {
            throw new NoteNotFoundException(id);
        }
    }

    public void clear() {
        storage.clear();
    }
}
