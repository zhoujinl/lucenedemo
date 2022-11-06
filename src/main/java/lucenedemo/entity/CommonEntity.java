package lucenedemo.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;

/**
* 实体类都继承的类
*
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonEntity {
    /**
     * 前台的排序条件
     */
    @ApiModelProperty(hidden = true)
    private List<String> orderData;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页",name = "currentPage",required = true)
    private Integer currentPage;

    /**
     * 开始下标
     */
    @ApiModelProperty(hidden = true)
    private Integer start;

    /**
     * 每页显示条数
     */
    @ApiModelProperty(hidden = true)
    private Integer pageSize;

    /**
     * 字符串格式的排序条件
     */
    @ApiModelProperty(hidden = true)
    private String orderStr;
}