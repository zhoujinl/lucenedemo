package lucenedemo.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lucenedemo.service.IArticleService;
import lucenedemo.core.annotation.LoginRequired;
import lucenedemo.core.annotation.RecordLog;
import lucenedemo.entity.PageData;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lucenedemo.entity.ArticleEntity;

import java.util.List;

@RestController
@Api(tags = "article接口")
@RequestMapping("/article")
public class ArticleController {


	private IArticleService service;

	@Autowired
	public ArticleController(IArticleService service) {
		this.service = service;
	}

	/**
	 * 全文检索
	 *
	 * @return
	 */
	@ApiOperation(value = "全文检索")
	@RecordLog
	@GetMapping(value = "/full-text-search")
	public PageData<ArticleEntity> fullTextSearch(String keyWord, @RequestParam(name = "currentPage") Integer page, @RequestParam Integer limit) {
		return service.fullTextSearch(keyWord, page, limit);
	}

	/**
	 * 查询
	 *
	 * @return
	 */
	@ApiOperation(value = "查询")
	@RecordLog
	@PostMapping(value = "/select")
	public List<ArticleEntity> select(@RequestBody ArticleEntity entity) {
		return service.select(entity);
	}

	/**
	 * 模糊查询
	 *
	 * @return
	 */
	@ApiOperation(value = "模糊查询")
	@RecordLog
	@PostMapping(value = "/likeSelect")
	public PageData<ArticleEntity> likeSelect(@RequestBody ArticleEntity entity) {
		return service.likeSelect(entity);
	}

	/**
	 * 更新
	 *
	 * @return
	 */
	@ApiOperation(value = "更新")
	@RecordLog
	@PostMapping(value = "/update")
	public void update(@RequestBody ArticleEntity entity) {
		service.update(entity);
	}

	/**
	 * 添加
	 *
	 * @return
	 */
	@ApiOperation(value = "添加")
	@RecordLog
	@PostMapping(value = "/add")
	public void add(@RequestBody ArticleEntity entity) {
		service.add(entity);
	}

	/**
	 * 删除
	 *
	 * @return
	 */
	@ApiOperation(value = "删除")
	@RecordLog
	@PostMapping(value = "/delete")
	public void delete(@RequestBody ArticleEntity entity) {
		service.delete(entity);
	}

	/**
	 * 导出excel
	 *
	 * @return
	 */
	@ApiOperation(value = "导出excel")
	@RecordLog
	@GetMapping("/exportExcel")
	public void exportExcel(ArticleEntity entity, HttpServletResponse response) {
		service.exportExcel(entity, response);
	}

}
