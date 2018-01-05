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
	 * id名称
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
	 * 获取索引的目录
	 */
	private String filePath = getFilePath("\\src\\main\\resources\\index2");
	
	private Directory dir;
	
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
	 * 获取IndexWriter实例
	 * @return
	 * @throws Exception
	 */
	public IndexWriter getWriter() throws Exception{
		Analyzer analyzer = new StandardAnalyzer();//标准分词器
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(dir,iwc);
		return writer;
	}
	
	/**
	 * 生成索引
	 * @throws Exception
	 */
	@Test
	public void index() throws Exception{
		dir = FSDirectory.open(Paths.get(filePath));
		IndexWriter writer = getWriter();
		for(int i=0;i<ids.length;i++){
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));//用StingField 无分词
			doc.add(new StringField("author", authors[i], Field.Store.YES));
			doc.add(new StringField("position", positions[i], Field.Store.YES));
			//doc.add(new TextField("title", titles[i], Field.Store.YES));//对这个Field加权，条件是position是boss时候，先查看不加权效果
			//对position是boss加权
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
	 * 查询
	 * @throws Exception
	 */
	@Test
	public void search() throws Exception{
		dir = FSDirectory.open(Paths.get(filePath));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher is = new IndexSearcher(reader);
		//搜索域  查询字段
		String searchField = "title";
		String q = "java";
		//放到组中
		Term t = new Term(searchField, q);
		//查询对象 用组查询对象
		Query query = new TermQuery(t);
		//TopDocs hits
		TopDocs hits = is.search(query, 10);
		//scoreDoc
		System.out.println("匹配  '"+q+"',工查询到"+hits.totalHits+"个文档");
		for (ScoreDoc scoreDoc:hits.scoreDocs) {
			Document doc = is.doc(scoreDoc.doc);
			System.out.println(doc.get("author"));
		}
		//关闭读
		reader.close();
		
		/**
		 * 不加权查询出来   John Jack Marry Json
		 * 对position是boss加权
		 * 查询出来   Json John Jack Marry 
		 */
	}
	
	
}
