package win.bigdreamer.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
	
	private Integer ids[]={1,2,3};
	private String citys[]={"qingdao","banjing","changhai"};
	private String descs[]={
			"Qingdao is a beautiful city.",
			"Nanjing is b city of culture.",
			"Shanghai is c bustling city."
	};
	
	private Directory dir;
	
	/**
	 * 获取IndexWriter实例
	 * @return
	 * @throws Exception
	 */
	private IndexWriter getWriter()throws Exception{
		Analyzer analyzer=new StandardAnalyzer(); // 标准分词器
		IndexWriterConfig iwc=new IndexWriterConfig(analyzer);
		IndexWriter writer=new IndexWriter(dir, iwc);
		return writer;
	}
	
	/**
	 * 生成索引
	 * @param indexDir
	 * @throws Exception
	 */
	public void index(String indexDir)throws Exception{
		dir=FSDirectory.open(Paths.get(indexDir));
		IndexWriter writer = getWriter();
		for(int i=0;i<ids.length;i++){
			Document doc = new Document();
			doc.add(new IntField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("city", citys[i], Field.Store.YES));
			doc.add(new TextField("desc", descs[i], Field.Store.YES));
			writer.addDocument(doc);//添加文档
		}
		writer.close();
	}
	
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
	
	public static void main(String[] args) throws Exception {
		String filePath = getFilePath("\\src\\main\\resources\\index");;
		new Indexer().index(filePath);
	}
	
}
