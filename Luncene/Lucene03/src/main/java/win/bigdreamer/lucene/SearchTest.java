package win.bigdreamer.lucene;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SearchTest {
	
	private Directory dir;
	private IndexReader reader;
	private IndexSearcher is;
	private String filePath = getFilePath("\\src\\main\\resources\\index");
	
	/**
	 * 获取文件的路径
	 * @return
	 */
	public static String getFilePath(String str){
		File directory = new File("");// 参数为空
        try {
			String courseFile = directory.getCanonicalPath();
			return courseFile + "\\" + str;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 加载搜索索引类
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		dir = FSDirectory.open(Paths.get(filePath));
		reader = DirectoryReader.open(dir);
		is = new IndexSearcher(reader);
	}

	@After
	public void tearDown() throws Exception {
		reader.close();
	}

	/**
	 * 对特定项搜索 
	 * @throws Exception 
	 */
	@Test
	public void testTermQuery() throws Exception {
		String searchField = "contents";
		String q = "particular";//particular 查出四条数据   particula查不出数据  
		Term t = new Term(searchField, q);
		Query query = new TermQuery(t);
		TopDocs hits = is.search(query, 10);
		System.out.println("匹配 "+q+" ，总共查询到"+hits.totalHits+"个记录");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}
	
	/**
	 * 解析表达式 搜索 
	 * @throws Exception 
	 */
	@Test
	public void testQueryParser() throws Exception {
		Analyzer analyzer = new StandardAnalyzer();//标准分词器
		String searchField = "contents";
//		String q = "particular";           //匹配单个词        匹配 particular ，总共查询到4个记录
//		String q = "particular or java";   //匹配多个词 or关系     匹配 particular or java ，总共查询到7个记录
//		String q = "particular AND ICOT";  //匹配多个词 and关系 匹配 particular AND ICOT ，总共查询到1个记录
		String q = "abc~";//模糊匹配
		QueryParser parser = new QueryParser(searchField, analyzer);
		Query query = parser.parse(q);
		TopDocs hits = is.search(query, 10);
		System.out.println("匹配 "+q+" ，总共查询到"+hits.totalHits+"个记录");
		for(ScoreDoc scoreDoc:hits.scoreDocs){//分页可以放在这里面处理
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}

}
