package lucenedemo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ArticleEntity extends CommonEntity implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
	 *  主键id
	 */
	@ApiModelProperty(value = "主键id", name = "id")
    private Integer id;
    /**
	 *  标题
	 */
	@ApiModelProperty(value = "标题", name = "title")
    private String title;
    /**
	 *  内容
	 */
	@ApiModelProperty(value = "内容", name = "content")
    private String content;


}
