package win.bigdreamer.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
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
	 * 
	 * 指定项范围搜索 对单个词
	 * 字符串范围查询的,相当于把字符串转成asc码
	 * @throws Exception 
	 */
	@Test
	public void testTermRangeQuery() throws Exception {
		TermRangeQuery query = new TermRangeQuery("desc",new BytesRef("a".getBytes()), new BytesRef("c".getBytes()), true, true);
		TopDocs hits = is.search(query, 10);
		System.out.println("总共查询到"+hits.totalHits+"个记录");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}
	
	/**
	 * 指定数字范围搜索
	 * @throws Exception 
	 */
	@Test
	public void testNumbericRangeQuery() throws Exception {
		NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange("id", 1, 2, true, true);
		TopDocs hits = is.search(query, 10);
		System.out.println("总共查询到"+hits.totalHits+"个记录");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}
	
	/**
	 * 指定字符串开头搜索
	 * @throws Exception 
	 */
	@Test
	public void testPreFixQuery() throws Exception {
		PrefixQuery query = new PrefixQuery(new Term("city","q")); //a找不到   q找到qingdao
		TopDocs hits = is.search(query, 10);
		System.out.println("总共查询到"+hits.totalHits+"个记录");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}
	
	/**
	 * 多条件查询
	 * @throws Exception 
	 */
	@Test
	public void testBooleanQuery() throws Exception {
		NumericRangeQuery<Integer> queryNum = NumericRangeQuery.newIntRange("id", 1, 2, true, true);//1 2  13
		PrefixQuery queryPrefix = new PrefixQuery(new Term("city","q"));
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		booleanQuery.add(queryNum, BooleanClause.Occur.MUST);
		booleanQuery.add(queryPrefix, BooleanClause.Occur.MUST);
		TopDocs hits = is.search(booleanQuery.build(), 10);
		System.out.println("总共查询到"+hits.totalHits+"个记录");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}
}
