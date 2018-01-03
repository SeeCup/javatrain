/**
 * 
 */
package win.bigdream.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

	public static void search(String indexDir,String q)throws Exception{
		Directory dir=FSDirectory.open(Paths.get(indexDir));
		IndexReader reader=DirectoryReader.open(dir);
		IndexSearcher is=new IndexSearcher(reader);
		Analyzer analyzer=new StandardAnalyzer(); // ��׼�ִ���
		QueryParser parser=new QueryParser("contents", analyzer);
		Query query=parser.parse(q);
		long start=System.currentTimeMillis();
		TopDocs hits=is.search(query, 10);
		long end=System.currentTimeMillis();
		System.out.println("ƥ�� "+q+" ���ܹ�����"+(end-start)+"����"+"��ѯ��"+hits.totalHits+"����¼");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
		reader.close();
	}
	
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
	
	public static void main(String[] args) {
		String indexDir = getFilePath("\\src\\main\\resources\\index");
		String q="Zygmunt Saloni";//java 6 
		try {
			search(indexDir,q);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

