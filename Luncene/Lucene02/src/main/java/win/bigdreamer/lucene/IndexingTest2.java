/**
 * 
 */
package win.bigdreamer.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.junit.Test;

/**
 * @author hh
 *
 */
public class IndexingTest2 {
	/**
	 * id����
	 */
	private String ids[]={"1","2","3","4"};
	private String authors[]={"Jack","Marry","John","Json"};
	private String positions[]={"accounting","technician","salesperson","boss"};
	private String titles[]={"Java is a good language.","Java is a cross platform language","Java powerful","You should learn java"};
	private String contents[]={
			"If possible, use the same JRE major version at both index and search time.",
			"When upgrading to a different JRE major version, consider re-indexing. ",
			"Different JRE major versions may implement different versions of Unicode,",
			"For example: with Java 1.4, `LetterTokenizer` will split around the character U+02C6,"
	};
	
	/**
	 * ��ȡ������Ŀ¼
	 */
	private String filePath = getFilePath("\\src\\main\\resources\\index2");
	
	private Directory dir;
	
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
	 * ��ȡIndexWriterʵ��
	 * @return
	 * @throws Exception
	 */
	public IndexWriter getWriter() throws Exception{
		Analyzer analyzer = new StandardAnalyzer();//��׼�ִ���
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(dir,iwc);
		return writer;
	}
	
	/**
	 * ��������
	 * @throws Exception
	 */
	@Test
	public void index() throws Exception{
		dir = FSDirectory.open(Paths.get(filePath));
		IndexWriter writer = getWriter();
		for(int i=0;i<ids.length;i++){
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));//��StingField �޷ִ�
			doc.add(new StringField("author", authors[i], Field.Store.YES));
			doc.add(new StringField("position", positions[i], Field.Store.YES));
			//doc.add(new TextField("title", titles[i], Field.Store.YES));//�����Field��Ȩ��������position��bossʱ���Ȳ鿴����ȨЧ��
			//��position��boss��Ȩ
			TextField field = new TextField("title",titles[i], Field.Store.YES);
			if("boss".equals(positions[i])){
				field.setBoost(1.5f);
			}
			doc.add(field);
			doc.add(new TextField("content", contents[i],Field.Store.NO));
			writer.addDocument(doc);
		}
		writer.close();
	}
	
	/**
	 * ��ѯ
	 * @throws Exception
	 */
	@Test
	public void search() throws Exception{
		dir = FSDirectory.open(Paths.get(filePath));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher is = new IndexSearcher(reader);
		//������  ��ѯ�ֶ�
		String searchField = "title";
		String q = "java";
		//�ŵ�����
		Term t = new Term(searchField, q);
		//��ѯ���� �����ѯ����
		Query query = new TermQuery(t);
		//TopDocs hits
		TopDocs hits = is.search(query, 10);
		//scoreDoc
		System.out.println("ƥ��  '"+q+"',����ѯ��"+hits.totalHits+"���ĵ�");
		for (ScoreDoc scoreDoc:hits.scoreDocs) {
			Document doc = is.doc(scoreDoc.doc);
			System.out.println(doc.get("author"));
		}
		//�رն�
		reader.close();
		
		/**
		 * ����Ȩ��ѯ����   John Jack Marry Json
		 * ��position��boss��Ȩ
		 * ��ѯ����   Json John Jack Marry 
		 */
	}
	
	
}
