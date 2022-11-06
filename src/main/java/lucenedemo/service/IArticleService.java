package lucenedemo.service;

import javax.servlet.http.HttpServletResponse;

import lucenedemo.entity.ArticleEntity;
import lucenedemo.entity.PageData;

import java.util.List;

public interface IArticleService {

	void add(ArticleEntity entity);

	void delete(ArticleEntity entity);

	void update(ArticleEntity entity);

	List<ArticleEntity> select(ArticleEntity entity);

	PageData<ArticleEntity> likeSelect(ArticleEntity entity);

	void batchAdd(List<ArticleEntity> list);

	void batchDelete(List<ArticleEntity> list);

	void batchUpdate(List<ArticleEntity> list);

	void exportExcel(ArticleEntity entity, HttpServletResponse response);

	/**
	 * 全文检索
	 *
	 * @param keyWord
	 * @return
	 */
	PageData<ArticleEntity> fullTextSearch(String keyWord, Integer page, Integer limit);
}
