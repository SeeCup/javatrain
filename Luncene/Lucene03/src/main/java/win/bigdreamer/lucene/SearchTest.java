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
	 * ���ض������� 
	 * @throws Exception 
	 */
	@Test
	public void testTermQuery() throws Exception {
		String searchField = "contents";
		String q = "particular";//particular �����������   particula�鲻������  
		Term t = new Term(searchField, q);
		Query query = new TermQuery(t);
		TopDocs hits = is.search(query, 10);
		System.out.println("ƥ�� "+q+" ���ܹ���ѯ��"+hits.totalHits+"����¼");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}
	
	/**
	 * �������ʽ ���� 
	 * @throws Exception 
	 */
	@Test
	public void testQueryParser() throws Exception {
		Analyzer analyzer = new StandardAnalyzer();//��׼�ִ���
		String searchField = "contents";
//		String q = "particular";           //ƥ�䵥����        ƥ�� particular ���ܹ���ѯ��4����¼
//		String q = "particular or java";   //ƥ������ or��ϵ     ƥ�� particular or java ���ܹ���ѯ��7����¼
//		String q = "particular AND ICOT";  //ƥ������ and��ϵ ƥ�� particular AND ICOT ���ܹ���ѯ��1����¼
		String q = "abc~";//ģ��ƥ��
		QueryParser parser = new QueryParser(searchField, analyzer);
		Query query = parser.parse(q);
		TopDocs hits = is.search(query, 10);
		System.out.println("ƥ�� "+q+" ���ܹ���ѯ��"+hits.totalHits+"����¼");
		for(ScoreDoc scoreDoc:hits.scoreDocs){//��ҳ���Է��������洦��
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}

}
