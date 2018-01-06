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
	 * ��ȡ�ļ���·��
	 * @return
	 */
	public static String getFilePath(String str){
		File directory = new File("");// ����Ϊ��
        try {
			String courseFile = directory.getCanonicalPath();
			return courseFile + "\\" + str;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ��������������
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
	 * ָ���Χ���� �Ե�����
	 * �ַ�����Χ��ѯ��,�൱�ڰ��ַ���ת��asc��
	 * @throws Exception 
	 */
	@Test
	public void testTermRangeQuery() throws Exception {
		TermRangeQuery query = new TermRangeQuery("desc",new BytesRef("a".getBytes()), new BytesRef("c".getBytes()), true, true);
		TopDocs hits = is.search(query, 10);
		System.out.println("�ܹ���ѯ��"+hits.totalHits+"����¼");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}
	
	/**
	 * ָ�����ַ�Χ����
	 * @throws Exception 
	 */
	@Test
	public void testNumbericRangeQuery() throws Exception {
		NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange("id", 1, 2, true, true);
		TopDocs hits = is.search(query, 10);
		System.out.println("�ܹ���ѯ��"+hits.totalHits+"����¼");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}
	
	/**
	 * ָ���ַ�����ͷ����
	 * @throws Exception 
	 */
	@Test
	public void testPreFixQuery() throws Exception {
		PrefixQuery query = new PrefixQuery(new Term("city","q")); //a�Ҳ���   q�ҵ�qingdao
		TopDocs hits = is.search(query, 10);
		System.out.println("�ܹ���ѯ��"+hits.totalHits+"����¼");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}
	
	/**
	 * ��������ѯ
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
		System.out.println("�ܹ���ѯ��"+hits.totalHits+"����¼");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}
}
