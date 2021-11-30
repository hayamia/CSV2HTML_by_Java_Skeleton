package csv2html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import utility.StringUtility;

/**
 * ライタ：情報のテーブルをHTMLページとして書き出す。
 */
public class Writer extends IO
{
	/**
	 * ライタのコンストラクタ。
	 * @param aTable テーブル
	 */
	public Writer(Table aTable)
	{
		super(aTable);

		return;
	}

	/**
	 * HTMLページを基にするテーブルからインデックスファイル(index.html)に書き出す。
	 */
	public void perform()
	{
		try
		{
			Attributes attributes = this.attributes();
			String fileStringOfHTML = attributes.baseDirectory() + attributes.indexHTML();
			File aFile = new File(fileStringOfHTML);
			FileOutputStream outputStream = new FileOutputStream(aFile);
			OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream, StringUtility.encodingSymbol());
			BufferedWriter aWriter = new BufferedWriter(outputWriter);

			this.writeHeaderOn(aWriter);
			this.writeTableBodyOn(aWriter);
			this.writeFooterOn(aWriter);

			aWriter.close();
		}
		catch (UnsupportedEncodingException | FileNotFoundException anException) { anException.printStackTrace(); }
		catch (IOException anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * 属性リストを書き出す。
	 * @param aWriter ライタ
	 */
	public void writeAttributesOn(BufferedWriter aWriter)
	{
		return;
	}

	/**
	 * フッタを書き出す。
	 * @param aWriter ライタ
	 */
	public void writeFooterOn(BufferedWriter aWriter)
	{
		return;
	}

	/**
	 * ヘッダを書き出す。
	 * @param aWriter ライタ
	 */
	public void writeHeaderOn(BufferedWriter aWriter)
	{
		try
		{
			aWriter.write("<!DOCTYPE HTML>");
			aWriter.write("<html lang=\"ja\">");
			aWriter.write("<head>");
			aWriter.write("<meta charset=\"UTF-8\" />");
			aWriter.write("</head>");;

			aWriter.write("<body>");
			aWriter.write("<header>");
			if(this.table().attributes().getClass().equals(AttributesForPrimeMinisters.class))
			{
				aWriter.write("<h1>総理大臣</h1>");
			}else
			{
				aWriter.write("<h1>徳川幕府</h1>");
			}
			aWriter.write("</header>");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return;
	}

	/**
	 * ボディを書き出す。
	 * @param aWriter ライタ
	 */
	public void writeTableBodyOn(BufferedWriter aWriter)
	{
		this.writeAttributesOn(aWriter);
		this.writeTuplesOn(aWriter);

		return;
	}

	/**
	 * タプル群を書き出す。
	 * @param aWriter ライタ
	 */
	public void writeTuplesOn(BufferedWriter aWriter)
	{
		try
		{
			aWriter.write("<table>");
			aWriter.write("<thead><tr>");
			for(String aString : this.attributes().keys())
			{
				aWriter.write("<th>");
				aWriter.write(aString);
				aWriter.write("</th>");
			}
			aWriter.write("</tr></thead>");
			aWriter.newLine();

			aWriter.write("<tbody>");
			for(Tuple aTuple : this.table().tuples())
			{
				aWriter.write("<tr>");
				for(String aString : aTuple.values())
				{
					aWriter.write("<td>");
					aWriter.write(aString);
					aWriter.write("</td>");
					aWriter.newLine();
				}
				aWriter.write("</tr>");
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return;
	}
}
