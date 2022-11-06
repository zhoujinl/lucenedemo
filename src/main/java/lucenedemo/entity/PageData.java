package lucenedemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页公共类
 *
 * @param <E>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageData<E> implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;


    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 总个数
     */
    private Integer count;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 查询结果
     */
    private List<E> result;
}

