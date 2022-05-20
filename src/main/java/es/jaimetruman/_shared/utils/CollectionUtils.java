package es.jaimetruman._shared.utils;

import java.util.List;

public final class CollectionUtils {
    public static <T> List<T> sublist(List<T> source, int from, int to){
        return to + 1 >= source.size() ?
                source.subList(from, source.size() - 1) :
                source.subList(from, to);
    }
}
