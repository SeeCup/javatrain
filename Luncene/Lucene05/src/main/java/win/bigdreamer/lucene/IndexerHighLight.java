package win.bigdreamer.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
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

public class IndexerHighLight {
	
	private Integer ids[]={1,2,3};
	private String citys[]={"�ൺ","�Ͼ�","�Ϻ�"};
	private String descs[]={
			"�ൺ��һ�������ĳ��С�",
			"�Ͼ���һ�����Ļ��ĳ��С��Ͼ���һ���Ļ��ĳ����Ͼ�����������ǽ���ʡ�ᣬ�ش��й������������������Σ�����������ȫ����Ͻ11�����������6597ƽ�����2013�꽨�������752.83ƽ�������ס�˿�818.78�����г����˿�659.1���ˡ�[1-4] �����ϼ����أ���������ݡ����Ͼ�ӵ����6000��������ʷ����2600�꽨��ʷ�ͽ�500��Ľ���ʷ�����й��Ĵ�Ŷ�֮һ���С������Ŷ�������ʮ�����ᡱ֮�ƣ����л���������Ҫ����أ���ʷ�������α��ӻ���֮��˷���������й��Ϸ������Ρ����á��Ļ����ģ�ӵ�к��ص��Ļ����̺ͷḻ����ʷ�Ŵ档[5-7] �Ͼ��ǹ�����Ҫ�Ŀƽ����ģ��Թ���������һ�������ؽ̵ĳ��У��С��������ࡱ�������ϵ�һѧ��������������2013�꣬�Ͼ��иߵ�ԺУ75��������211��У8���������ڱ����Ϻ��������ص�ʵ����25���������ص�ѧ��169������ԺԺʿ83�ˣ������й�������[8-10] ��",
			"�Ϻ���һ�������ĳ��С�"
	};
	
	private Directory dir;
	
	/**
	 * ��ȡIndexWriterʵ��
	 * @return
	 * @throws Exception
	 */
	private IndexWriter getWriter()throws Exception{
		//Analyzer analyzer=new StandardAnalyzer(); // ��׼�ִ���
		SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();//���ķִ���
		IndexWriterConfig iwc=new IndexWriterConfig(analyzer);
		IndexWriter writer=new IndexWriter(dir, iwc);
		return writer;
	}
	
	/**
	 * ��������
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
			writer.addDocument(doc);//����ĵ�
		}
		writer.close();
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
	
	public static void main(String[] args) throws Exception {
		String filePath = getFilePath("\\src\\main\\resources\\index2");;
		new IndexerHighLight().index(filePath);
	}
	
}
