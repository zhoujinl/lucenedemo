package lucenedemo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import lucenedemo.entity.ArticleEntity;

@Mapper
@Repository
public interface IArticleDao extends IBaseDao<ArticleEntity> {

}
