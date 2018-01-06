/**
 * 
 */
package win.bigdreamer.lucene;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearcherHighLight {

	public static void search(String indexDir,String q)throws Exception{
		Directory dir=FSDirectory.open(Paths.get(indexDir));
		IndexReader reader=DirectoryReader.open(dir);
		IndexSearcher is=new IndexSearcher(reader);
//		Analyzer analyzer=new StandardAnalyzer(); // ��׼�ִ���
		SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
		QueryParser parser=new QueryParser("desc", analyzer);
		Query query=parser.parse(q);
		long start=System.currentTimeMillis();
		TopDocs hits=is.search(query, 10);
		long end=System.currentTimeMillis();
		System.out.println("ƥ�� "+q+" ���ܹ�����"+(end-start)+"����"+"��ѯ��"+hits.totalHits+"����¼");
		
		//���и�����ʾ���Բ�ѯ���ֶΣ�ȡ���ƥ���Ƭ��
		QueryScorer scorer = new QueryScorer(query);
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font style='color:red;'>", "</font></b>");//�����Լ��ı�ǩ
		Highlighter highlight = new Highlighter(simpleHTMLFormatter,scorer);
		highlight.setTextFragmenter(fragmenter);
		
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
			String desc = doc.get("desc");
			if(desc!=null){
				TokenStream tokenStream = analyzer.tokenStream("desc", new StringReader(desc));
				System.out.println(highlight.getBestFragment(tokenStream, desc));
			}
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
		String indexDir = getFilePath("\\src\\main\\resources\\index2");
		String q="�Ͼ�����";//�Ͼ���1����  �Ͼ��Ļ���1���� �Ͼ����У����У��Ͼ��Ļ�
		try {
			search(indexDir,q);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

