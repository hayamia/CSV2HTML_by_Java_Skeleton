package csv2html;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * トランスレータ：CSVファイルをHTMLページへと変換するプログラム。
 */
public class Translator extends Object
{
	/**
	 * CSVに由来するテーブルを記憶するフィールド。
	 */
	private Table inputTable;

	/**
	 * HTMLに由来するテーブルを記憶するフィールド。
	 */
	private Table outputTable;

	/**
	 * 属性リストのクラスを指定したトランスレータのコンストラクタ。
	 * @param classOfAttributes 属性リストのクラス
	 */
	public Translator(Class<? extends Attributes> classOfAttributes)
	{
		super();

		Attributes.flushBaseDirectory();

		try
		{
			Constructor<? extends Attributes> aConstructor = classOfAttributes.getConstructor(String.class);

			this.inputTable = new Table(aConstructor.newInstance("input"));
			this.outputTable = new Table(aConstructor.newInstance("output"));
		}
		catch (NoSuchMethodException |
			   InstantiationException |
			   IllegalAccessException |
			   InvocationTargetException anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * 在位日数を計算して、それを文字列にして応答する。
	 * @param periodString 在位期間の文字列
	 * @return 在位日数の文字列
	 */
	public String computeNumberOfDays(String periodString)
	{

		List<String> params = Arrays.asList(periodString.split("〜|年|月|日|"));

		int flag = 0;
		StringBuilder startYear = new StringBuilder();
		StringBuilder startMonth = new StringBuilder();
		StringBuilder startDay = new StringBuilder();
		StringBuilder endYear = new StringBuilder();
		StringBuilder endMonth = new StringBuilder();
		StringBuilder endDay = new StringBuilder();

		for (String aString: params)
		{
			if(aString.equals(""))
			{
				flag++;
				System.out.println("null");
				continue;
			}
			switch(flag)
			{
				case 0:
					startYear.append(aString);
					continue;
				case 1:
					startMonth.append(aString);
					continue;
				case 2:
					startDay.append(aString);
					continue;
				case 3:
					continue;
				case 4:
					endYear.append(aString);
					continue;
				case 5:
					endMonth.append(aString);
					continue;
				case 6:
					endDay.append(aString);
					continue;
			}
			System.out.println(aString);
		}

		LocalDate startDays= LocalDate.of(Integer.parseInt(startYear.toString()),
										Integer.parseInt(startMonth.toString()),
										Integer.parseInt(startDay.toString()));
		LocalDate endDays;
		String returnString;

		if(!endYear.toString().equals(""))
		{
			endDays = LocalDate.of(Integer.parseInt(endYear.toString()),
								   Integer.parseInt(endMonth.toString()),
					  			   Integer.parseInt(endDay.toString()));
			returnString = String.valueOf(ChronoUnit.DAYS.between(startDays, endDays) + 1);
		}else
		{
			returnString = "";
		}

		return returnString;
	}

	/**
	 * サムネイル画像から画像へ飛ぶためのHTML文字列を作成して、それを応答する。
	 * @param aString 画像の文字列
	 * @param aTuple タプル
	 * @param no 番号
	 * @return サムネイル画像から画像へ飛ぶためのHTML文字列
	 */
	public String computeStringOfImage(String aString, Tuple aTuple, int no)
	{
		return null;
	}

	/**
	 * CSVファイルをHTMLページへ変換する。
	 */
	public void execute()
	{
		// 必要な情報をダウンロードする。
		Downloader aDownloader = new Downloader(this.inputTable);
		aDownloader.perform();

		// CSVに由来するテーブルをHTMLに由来するテーブルへと変換する。
		//System.out.println(this.inputTable);
		//System.out.println(this.inputTable.tuples().get(1));
		this.translate();//変換を行う記述の追加が必要。
		//System.out.println(this.outputTable);
		//System.out.println(this.outputTable.tuples().get(0));

		// HTMLに由来するテーブルから書き出す。
		Writer aWriter = new Writer(this.outputTable);
		aWriter.perform();

		// ブラウザを立ち上げて閲覧する。
		try
		{
			Attributes attributes = this.outputTable.attributes();
			String fileStringOfHTML = attributes.baseDirectory() + attributes.indexHTML();
			ProcessBuilder aProcessBuilder = new ProcessBuilder("open", "-a", "Safari", fileStringOfHTML);
			aProcessBuilder.start();
		}
		catch (Exception anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * 属性リストのクラスを受け取って、CSVファイルをHTMLページへと変換するクラスメソッド。
	 * @param classOfAttributes 属性リストのクラス
	 */
	public static void perform(Class<? extends Attributes> classOfAttributes)
	{
		// トランスレータのインスタンスを生成する。
		Translator aTranslator = new Translator(classOfAttributes);
		// トランスレータにCSVファイルをHTMLページへ変換するように依頼する。
		aTranslator.execute();

		return;
	}

	/**
	 * CSVファイルを基にしたテーブルから、HTMLページを基にするテーブルに変換する。
	 */
	public void translate()
	{
		if(this.inputTable.attributes().getClass() == AttributesForPrimeMinisters.class)
		{
			System.out.println("success");
			for(Tuple inputTuple : this.inputTable.tuples())
			{
				if(inputTuple.values().get(0).equals("人目"))continue;

				List<String> values = new ArrayList<String>();
				AttributesForPrimeMinisters attributesForInput = (AttributesForPrimeMinisters) inputTuple.attributes();
				AttributesForPrimeMinisters attributesForOutput = (AttributesForPrimeMinisters) this.outputTable.attributes();

				values.add(attributesForOutput.indexOfNo(), 	inputTuple.values().get(attributesForInput.indexOfNo()));
				values.add(attributesForOutput.indexOfOrder(),  inputTuple.values().get(attributesForInput.indexOfOrder()));
				values.add(attributesForOutput.indexOfName(),   inputTuple.values().get(attributesForInput.indexOfName()));
				values.add(attributesForOutput.indexOfKana(),   inputTuple.values().get(attributesForInput.indexOfKana()));
				values.add(attributesForOutput.indexOfPeriod(), inputTuple.values().get(attributesForInput.indexOfPeriod()));
				values.add(attributesForOutput.indexOfDays(),   computeNumberOfDays(inputTuple.values().get(attributesForInput.indexOfPeriod())));
				values.add(attributesForOutput.indexOfSchool(), inputTuple.values().get(attributesForInput.indexOfSchool()));
				values.add(attributesForOutput.indexOfParty(),  inputTuple.values().get(attributesForInput.indexOfParty()));
				values.add(attributesForOutput.indexOfPlace(),  inputTuple.values().get(attributesForInput.indexOfPlace()));
				values.add(attributesForOutput.indexOfImage(),  inputTuple.values().get(attributesForInput.indexOfImage()));

				Tuple outputTuple = new Tuple(this.outputTable.attributes(), values);
				this.outputTable.add(outputTuple);
			}
		}else
		{
			System.out.println("Tokugawa");
			for(Tuple inputTuple : this.inputTable.tuples())
			{
				if(inputTuple.values().get(0).equals("代")) continue;

				List<String> values = new ArrayList<String>();
				AttributesForTokugawaShogunate attributesForInput = (AttributesForTokugawaShogunate) inputTuple.attributes();
				AttributesForTokugawaShogunate attributesForOutput = (AttributesForTokugawaShogunate) this.outputTable.attributes();

				values.add(attributesForOutput.indexOfNo(),       inputTuple.values().get(attributesForInput.indexOfNo()));
				values.add(attributesForOutput.indexOfName(),     inputTuple.values().get(attributesForInput.indexOfName()));
				values.add(attributesForOutput.indexOfKana(),     inputTuple.values().get(attributesForInput.indexOfKana()));
				values.add(attributesForOutput.indexOfPeriod(),   inputTuple.values().get(attributesForInput.indexOfPeriod()));
				values.add(attributesForOutput.indexOfDays(),     computeNumberOfDays(inputTuple.values().get(attributesForInput.indexOfPeriod())));
				values.add(attributesForOutput.indexOfFamily(),   inputTuple.values().get(attributesForInput.indexOfFamily()));
				values.add(attributesForOutput.indexOfRank(),     inputTuple.values().get(attributesForInput.indexOfRank()));
				values.add(attributesForOutput.indexOfImage(),    inputTuple.values().get(attributesForInput.indexOfImage()));
				values.add(attributesForOutput.indexOfFormer(),   inputTuple.values().get(attributesForInput.indexOfFormer()));
				values.add(attributesForOutput.indexOfCemetery(), inputTuple.values().get(attributesForInput.indexOfCemetery()));

				Tuple outPutTuple = new Tuple(this.outputTable.attributes(), values);
				this.outputTable.add(outPutTuple);
			}
		}
		return;
	}
}
