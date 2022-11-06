package lucenedemo.dao;

import java.util.List;

public interface IBaseDao<E> {

    void add(E map);

    void delete(E map);

    void update(E map);

    List<E> select(E map);

    List<E> likeSelect(E entity);

    Long likeSelectCount(E entity);

    void batchAdd(List<E> list);

    void batchDelete(List<E> list);

    void batchUpdate(List<E> list);
}
