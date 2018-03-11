package com.waverley.fileBrowser.mapper;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BasicMapper {
    @Autowired
    private Mapper mapper;

    public <T> T singleMapper(Object from, Class<T> toClass) {
        T res = null;
        if (from != null){
            res = mapper.map(from, toClass);
        }
        return res;
    }

    public <E, T> List<T> listMapToList(Iterable<E> iterable, Class<T> toClass) {

        List<T> list = new ArrayList<T>();

        for (E e : iterable) {
            list.add(mapper.map(e, toClass));
        }

        return list;
    }

}

