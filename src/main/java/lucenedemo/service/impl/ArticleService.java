package lucenedemo.service.impl;

import lucenedemo.config.Config;
import lucenedemo.dao.IArticleDao;
import lucenedemo.entity.ArticleEntity;
import lucenedemo.entity.PageData;
import lucenedemo.service.IArticleService;
import lucenedemo.utils.ExcelUtil;
import lucenedemo.utils.PageUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class ArticleService implements IArticleService {


	private IArticleDao dao;

	@Autowired
	public ArticleService(IArticleDao dao) {
		this.dao = dao;
	}

	@Autowired
	private Config config;

	/**
	 * mapper文件里增加 useGeneratedKeys="true" keyProperty="id" keyColumn="id"属性，否则自增主键映射不上
	 *
	 * @param entity
	 */
	@Override
	public void add(ArticleEntity entity) {
		dao.add(entity);
		addOrUpIndex(entity);
	}

	@Override
	public void update(ArticleEntity entity) {
		dao.update(entity);
		addOrUpIndex(entity);
	}

	/**
	 * 添加或更新索引
	 *
	 * @param entity
	 */
	private void addOrUpIndex(ArticleEntity entity) {
		IndexWriter indexWriter = null;
		IndexReader indexReader = null;
		Directory directory = null;
		Analyzer analyzer = null;
		try {
			//创建索引目录文件
			File indexFile = new File(config.getIndexLibrary());
			File[] files = indexFile.listFiles();
			// 1. 创建分词器,分析文档，对文档进行分词
			analyzer = new IKAnalyzer();
			// 2. 创建Directory对象,声明索引库的位置
			directory = FSDirectory.open(Paths.get(config.getIndexLibrary()));
			// 3. 创建IndexWriteConfig对象，写入索引需要的配置
			IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
			// 4.创建IndexWriter写入对象
			indexWriter = new IndexWriter(directory, writerConfig);
			// 5.写入到索引库，通过IndexWriter添加文档对象document
			Document doc = new Document();
			//查询是否有该索引，没有添加，有则更新
			TopDocs topDocs = null;
			//判断索引目录文件是否存在文件，如果没有文件，则为首次添加，有文件，则查询id是否已经存在
			if (files != null && files.length != 0) {
				//创建查询对象
				QueryParser queryParser = new QueryParser("id", analyzer);
				Query query = queryParser.parse(String.valueOf(entity.getId()));
				indexReader = DirectoryReader.open(directory);
				IndexSearcher indexSearcher = new IndexSearcher(indexReader);
				//查询获取命中条目
				topDocs = indexSearcher.search(query, 1);
			}
			//StringField 不分词 直接建索引 存储
			doc.add(new StringField("id", String.valueOf(entity.getId()), Field.Store.YES));
			//TextField 分词 建索引 存储
			doc.add(new TextField("title", entity.getTitle(), Field.Store.YES));
			//TextField 分词 建索引 存储
			doc.add(new TextField("content", entity.getContent(), Field.Store.YES));
			//如果没有查询结果，添加
			if (topDocs != null && topDocs.totalHits.value == 0) {
				indexWriter.addDocument(doc);
				//否则，更新
			} else {
				indexWriter.updateDocument(new Term("id", String.valueOf(entity.getId())), doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("添加索引库出错：" + e.getMessage());
		} finally {
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (indexReader != null) {
				try {
					indexReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (analyzer != null) {
				analyzer.close();
			}
		}
	}

	@Override
	public PageData<ArticleEntity> fullTextSearch(String keyWord, Integer page, Integer limit) {
		List<ArticleEntity> searchList = new ArrayList<>(10);
		PageData<ArticleEntity> pageData = new PageData<>();
		File indexFile = new File(config.getIndexLibrary());
		File[] files = indexFile.listFiles();
		//沒有索引文件，不然沒有查詢結果
		if (files == null || files.length == 0) {
			pageData.setCount(0);
			pageData.setTotalPage(0);
			pageData.setCurrentPage(page);
			pageData.setResult(new ArrayList<>());
			return pageData;
		}
		IndexReader indexReader = null;
		Directory directory = null;
		try (Analyzer analyzer = new IKAnalyzer()) {
			directory = FSDirectory.open(Paths.get(config.getIndexLibrary()));
			//多项查询条件
			QueryParser queryParser = new MultiFieldQueryParser(new String[]{"title", "content"}, analyzer);
			//单项
			//QueryParser queryParser = new QueryParser("title", analyzer);
			Query query = queryParser.parse(!StringUtils.isEmpty(keyWord) ? keyWord : "*:*");
			indexReader = DirectoryReader.open(directory);
			//索引查询对象
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			TopDocs topDocs = indexSearcher.search(query, 1);
			//获取条数
			int total = (int) topDocs.totalHits.value;
			pageData.setCount(total);
			int realPage = total % limit == 0 ? total / limit : total / limit + 1;
			pageData.setTotalPage(realPage);
			//获取结果集
			ScoreDoc lastSd = null;
			if (page > 1) {
				int num = limit * (page - 1);
				TopDocs tds = indexSearcher.search(query, num);
				lastSd = tds.scoreDocs[num - 1];
			}
			//通过最后一个元素去搜索下一页的元素
			TopDocs tds = indexSearcher.searchAfter(lastSd, query, limit);
			QueryScorer queryScorer = new QueryScorer(query);
			//最佳摘要
			SimpleSpanFragmenter fragmenter = new SimpleSpanFragmenter(queryScorer, 200);
			//高亮前后标签
			SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");
			//高亮对象
			Highlighter highlighter = new Highlighter(formatter, queryScorer);
			//设置高亮最佳摘要
			highlighter.setTextFragmenter(fragmenter);
			//遍历查询结果 把标题和内容替换为带高亮的最佳摘要
			for (ScoreDoc sd : tds.scoreDocs) {
				Document doc = indexSearcher.doc(sd.doc);
				ArticleEntity articleEntity = new ArticleEntity();
				Integer id = Integer.parseInt(doc.get("id"));
				//获取标题的最佳摘要
				String titleBestFragment = highlighter.getBestFragment(analyzer, "title", doc.get("title"));
				titleBestFragment = StringUtils.isEmpty(titleBestFragment) ? doc.get("title") : titleBestFragment;
				//获取文章内容的最佳摘要
				String contentBestFragment = highlighter.getBestFragment(analyzer, "content", doc.get("content"));
				contentBestFragment = StringUtils.isEmpty(contentBestFragment) ? doc.get("content") : contentBestFragment;
				articleEntity.setId(id);
				articleEntity.setTitle(titleBestFragment);
				articleEntity.setContent(contentBestFragment);
				searchList.add(articleEntity);
			}
			pageData.setCurrentPage(page);
			pageData.setResult(searchList);
			return pageData;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("全文檢索出错：" + e.getMessage());
		} finally {
			if (indexReader != null) {
				try {
					indexReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void delete(ArticleEntity entity) {
		dao.delete(entity);
		//同步删除索引
		deleteIndex(entity);
	}

	private void deleteIndex(ArticleEntity entity) {
		//删除全文检索
		IndexWriter indexWriter = null;
		Directory directory = null;
		try (Analyzer analyzer = new IKAnalyzer()) {
			directory = FSDirectory.open(Paths.get(config.getIndexLibrary()));
			IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, writerConfig);
			//根据id字段进行删除
			indexWriter.deleteDocuments(new Term("id", String.valueOf(entity.getId())));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("删除索引库出错：" + e.getMessage());
		} finally {
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<ArticleEntity> select(ArticleEntity entity) {
		return dao.select(entity);
	}

	@Override
	public PageData<ArticleEntity> likeSelect(ArticleEntity entity) {
		return PageUtil.getPageData(entity, dao);
	}

	@Override
	public void batchAdd(List<ArticleEntity> list) {
		dao.batchAdd(list);
	}

	@Override
	public void batchDelete(List<ArticleEntity> list) {
		dao.batchDelete(list);
	}

	@Override
	public void batchUpdate(List<ArticleEntity> list) {
		dao.batchUpdate(list);
	}


	@Override
	public void exportExcel(ArticleEntity entity, HttpServletResponse response) {

		// 获取头部信息（可以设置为动态）
		String[] headList = new String[]{"id", "title", "content"};

		String[] headEngList = new String[]{"id", "title", "content"};

		String[] describeList = new String[]{"", "", ""};

		//设置头部以及描述信息
		Map<String, String> headAndDescribeMap = new LinkedHashMap<>();
		for (int i = 0; i < headEngList.length; i++) {
			headAndDescribeMap.put(headEngList[i], describeList[i]);
		}

		ExcelUtil.exportExcel(entity, response, dao, headList, headAndDescribeMap);
	}

}
